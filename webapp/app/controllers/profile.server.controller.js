// Invoke 'strict' JavaScript mode
'use strict';

// Load the module dependencies

let User = require('mongoose').model('User'),
    config = require('../../config/config'),
    gateway = require('../../config/gateway'),
    braintree = require('braintree'),
    Transaction = require('mongoose').model('Transaction'),
    Item = require('mongoose').model('Item'),
    lodash = require('lodash');

const stripe = require('stripe')(config.stripe_secretKey);

let TRANSACTION_SUCCESS_STATUSES = [
    braintree.Transaction.Status.Authorizing,
    braintree.Transaction.Status.Authorized,
    braintree.Transaction.Status.Settled,
    braintree.Transaction.Status.Settling,
    braintree.Transaction.Status.SettlementConfirmed,
    braintree.Transaction.Status.SettlementPending,
    braintree.Transaction.Status.SubmittedForSettlement
];

exports.edit = function (req, res, next) {
    if (req.user) {
        //var user = new User(req.body);
        let message = null;
        let query = {'username': req.user.username};
        req.user.firstName = req.body.firstName;
        req.user.lastName = req.body.lastName;
        req.user.city = req.body.city;
        req.user.gender = req.body.gender;
        User.update(query, req.user, function (err, doc) {
            if (err) return res.send(500, {error: err});
            message = "Profile updated succesfully!!";
            return res.send({message});
        });
    }
};

exports.getClientToken = function (req, res, next) {
    if (req.user) {
        const stripe_version = req.body.api_version;
        if (!stripe_version) {
            res.status(400).end();
            return;
        }
        // This function assumes that some previous middleware has determined the
        // correct customerId for the session and saved it on the request object.
        stripe.ephemeralKeys.create(
            {customer: req.user.customerId},
            {stripe_version: stripe_version}
        ).then((key) => {
            res.send(200, {key});
        }).catch((err) => {
            res.send(500, {message: "Invalid Request"});
        });
    } else {
        res.send(500, {message: "Invalid Request"});
    }
};

exports.checkout = async function (req, res, next) {
    if (req.user) {
        let amount = req.user.currentTransaction.totalAmount; // In production you should not take amounts directly from clients
        let nonce = req.body.payment_method_nonce;
        let customer = req.user.customerId;
        amount = Math.round(amount * 100);

        /*stripe.customers.update(customer, {
            source: nonce,
        });*/

        const charge = await stripe.charges.create({
            amount: amount,
            currency: 'usd',
            description: 'Example charge',
            customer: customer,
            source: nonce
        });

        console.log(charge);

        if (charge) {
            let query = {'username': req.user.username};
            let receipt_url = charge.receipt_url;
            req.user.currentTransaction.transactionId = charge.id;
            let trans = {};
            trans.transactionId = charge.id;
            trans.status = req.user.currentTransaction.status;
            trans.totalAmount = req.user.currentTransaction.totalAmount;
            trans.receipt_url = receipt_url;
            req.user.transactionHistory.push(new Transaction(trans));
            req.user.currentTransaction.totalAmount = 0;
            req.user.currentTransaction.cartItems = [];
            req.user.currentTransaction.transactionId = '';
            User.update(query, req.user, function (err, doc) {
                if (err) return res.send(500, {error: err,message: "Your transaction has failed!!"});
                console.log(doc);
                let message = "Your transaction is processed successfully!!";
                return res.send(200, {message, receipt_url});
            });
        }
    }
};

exports.createCard = function (req, res, next){
    let customer = req.user.customerId;
    let cardToken = req.body.cardToken;
    stripe.customers.createSource(customer, {
        source: cardToken,
    }, function (err, card) {
        if (err) return res.send(500, {error: err});
        console.log(card);
        res.send(200,{card});
    });
};

exports.updateCard = function (req, res, next){
    /*let customer = req.user.customerId;
    let cardId = req.body.cardId;
    stripe.customers.updateSource(customer, {
        source: cardToken,
    }, function (err, card) {
        if (err) return res.send(500, {error: err});
        console.log(card);
        res.send(200,{card});
    });*/
};

exports.deleteCard = function (req, res, next){
    let customer = req.user.customerId;
    let cardId = req.body.cardId;
    stripe.customers.deleteSource(customer, cardId,
        function(err, confirmation) {
        if (err) return res.send(500, {error: err});
        console.log(confirmation);
        res.send(200,{confirmation});
    });
};

exports.listAllCards = function (req, res, next) {
    let customer = req.user.customerId;
    stripe.customers.listSources(customer, {
            object: 'card',
    }, function (err, cards) {
        if (err) return res.send(500, {error: err});
        console.log(cards);
        res.send(200,{cards});
    });
};

exports.addItem = function (req, res, next) {
    if (req.user) {
        let query = {'username': req.user.username};
        let item = new Item(req.body);
        if (isNaN(req.user.currentTransaction.totalAmount))
            req.user.currentTransaction.totalAmount = item.discount;
        else {
            req.user.currentTransaction.totalAmount = req.user.currentTransaction.totalAmount + item.discount;
            req.user.currentTransaction.totalAmount = Math.round(req.user.currentTransaction.totalAmount * 100) / 100;
        }
        req.user.currentTransaction.cartItems.push(item);
        User.update(query, req.user, function (err, doc) {
            if (err) return res.send(500, {error: err});
            console.log(doc);
            let message = "Cart updated successfully!!";
            return res.send({message});
        });
    }
};

exports.deleteItem = function (req, res, next) {
    if (req.user && req.user.currentTransaction !== undefined && req.user.currentTransaction.cartItems.length > 0) {
        let cartLength = req.user.currentTransaction.cartItems.length;
        let query = {'username': req.user.username};
        let itemId = req.body.id;
        req.user.currentTransaction.cartItems = lodash.remove(req.user.currentTransaction.cartItems, function (obj) {
            return obj._id.toString() !== itemId;
        });
        if (cartLength === req.user.currentTransaction.cartItems.length) {
            return res.send({message: "Item id not found in the cart"});
        } else {
            req.user.currentTransaction.totalAmount -= req.body.discountPrice;
            req.user.currentTransaction.totalAmount = Math.round(req.user.currentTransaction.totalAmount * 100) / 100;
            if (req.user.currentTransaction.totalAmount < 0)
                req.user.currentTransaction.totalAmount = 0;
            User.update(query, req.user, function (err, doc) {
                if (err) return res.send(500, {error: err});
                console.log(doc);
                let message = "Cart updated successfully!!";
                return res.send({message});
            });
        }
    } else {
        return res.send(500, {message: "Can't perform this operation"});
    }
};
