// Invoke 'strict' JavaScript mode
'use strict';

// Load the module dependencies
var	config = require('./config'),
	mongoose = require('mongoose'),
	autoIncrement = require('mongoose-auto-increment');

// Define the Mongoose configuration method
module.exports = function() {
	// Use Mongoose to connect to MongoDB
	var connection = mongoose.connect(config.db);

	autoIncrement.initialize(connection);

	// Load the 'User' model 
	require('../app/models/user.server.model');
	require('../app/models/item.server.model');
	require('../app/models/transaction.server.model');

	// Return the Mongoose connection instance
	return connection;
};
