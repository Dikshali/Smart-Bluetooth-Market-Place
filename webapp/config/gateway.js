// Invoke 'strict' JavaScript mode
'use strict';

var braintree = require("braintree"),
    config = require('./config');

var gateway = braintree.connect({
    environment: braintree.Environment.Sandbox,
    merchantId: config.sandbox_merchantId,
    publicKey: config.sandbox_publicKey,
    privateKey: config.sandbox_privateKey
});

module.exports = gateway;