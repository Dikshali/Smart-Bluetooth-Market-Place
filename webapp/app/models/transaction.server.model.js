// Invoke 'strict' JavaScript mode
'use strict';

var mongoose = require('mongoose'),
    Schema = mongoose.Schema,
    item = require('./item.server.model');

var TransactionSchema = new Schema({
    status: {
        type: String,
        trim: true,
        required: 'Transaction status is required'
    },
    totalAmount: {
        type: Number,
        required: 'Total amount is required'
    },
    transactionId: String,
    cartItems: [{
        type: Object,
        ref: "Item"
    }],
    created: {
        type: Date,
        // Create a default 'created' value
        default: Date.now
    }
});

TransactionSchema.set('toJSON', {
    getters: true,
    virtuals: true
});

module.exports = mongoose.model('Transaction', TransactionSchema);


