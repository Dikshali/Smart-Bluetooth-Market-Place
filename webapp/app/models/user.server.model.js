// Invoke 'strict' JavaScript mode
'use strict';

// Load the module dependencies
var mongoose = require('mongoose'),
	crypto = require('crypto'),
	Schema = mongoose.Schema,
	autoIncrement = require('mongoose-auto-increment'),
	gateway = require('../../config/gateway'),
	Transaction = require('./transaction.server.model'),
	config = require('../../config/config'),
	Object = Schema.Types.Object;


// Define a new 'UserSchema'
var UserSchema = new Schema({
	firstName: {
		type: String,
		required: 'First Name is required',
		trim: true
	},
	lastName: {
		type: String,
		required: 'Last Name is required',
		trim: true
	},
	email: {
		type: String,
		// Validate the email format
		match: [/.+\@.+\..+/, "Please fill a valid email address"],
		unique: true,
		trim: true,
		required: 'Email id is required'
	},
	username: {
		type: String,
		// Set a unique 'username' index
		unique: true,
		// Validate 'username' value existance
		required: 'Username is required',
		// Trim the 'username' field
		trim: true
	},
	password: {
		type: String,
		// Validate the 'password' value length
		validate: [

			function (password) {
				return password && password.length > 6;
			}, 'Password should be longer'
		]
	},
	salt: {
		type: String
	},
	role: {
		type: String,
		default: 'User'
	},
	city: {
		type: String,
		required: 'City is required'
	},
	gender: {
		type: String,
		required: 'Gender is required'
	},
	customerId: String,
	created: {
		type: Date,
		// Create a default 'created' value
		default: Date.now
	},
	resetPasswordToken: String,
	resetPasswordExpires: Date,
	currentTransaction : {
		type: Object,
		ref: "Transaction"
	},
	transactionHistory: [{
		type: Object,
		ref: "Transaction"
	}]
});

// Set the 'fullname' virtual property
UserSchema.virtual('fullName').get(function () {
	return this.firstName + ' ' + this.lastName;
}).set(function (fullName) {
	var splitName = fullName.split(' ');
	this.firstName = splitName[0] || '';
	this.lastName = splitName[1] || '';
});

// Use a pre-save middleware to hash the password
UserSchema.pre('save', async function (next) {
	if (this.password) {
		//this.salt = new Buffer(crypto.randomBytes(16).toString('base64'), 'base64');
		this.salt = genRandomString(16);
		this.password = this.hashPassword(this.password);
	}
	this.currentTransaction = new Transaction();
	const stripe = require('stripe')(config.stripe_secretKey);
	let customer = await stripe.customers.create({
		"name": this.firstName + ' ' + this.lastName,
		"email": this.email
	});

	console.log(customer);
	if(customer){
		this.customerId = customer.id;
		next();
	}else {
		var message = 'Failed to create the user!';
		return res.json({message:message});
	}

});

/**
 * generates random string of characters i.e salt
 * @function
 * @param {number} length - Length of the random string.
 */
var genRandomString = function (length) {
	return crypto.randomBytes(Math.ceil(length / 2))
		.toString('hex') /** convert to hexadecimal format */
		.slice(0, length);   /** return required number of characters */
};

// Create an instance method for hashing a password
UserSchema.methods.hashPassword = function (password) {
	//return crypto.pbkdf2Sync(password, this.salt, 10000, 64).toString('base64');
	var hash = crypto.createHmac('sha512', this.salt); /** Hashing algorithm sha512 */
	hash.update(password);
	var value = hash.digest('hex');
	return value;
};

// Create an instance method for authenticating user
UserSchema.methods.authenticate = function (password) {
	return this.password === this.hashPassword(password);
};

// Find possible not used username
UserSchema.statics.findUniqueUsername = function (username, suffix, callback) {
	var _this = this;

	// Add a 'username' suffix
	var possibleUsername = username + (suffix || '');

	// Use the 'User' model 'findOne' method to find an available unique username
	_this.findOne({
		username: possibleUsername
	}, function (err, user) {
		// If an error occurs call the callback with a null value, otherwise find find an available unique username
		if (!err) {
			// If an available unique username was found call the callback method, otherwise call the 'findUniqueUsername' method again with a new suffix
			if (!user) {
				callback(possibleUsername);
			} else {
				return _this.findUniqueUsername(username, (suffix || 0) + 1, callback);
			}
		} else {
			callback(null);
		}
	});
};

// Configure the 'UserSchema' to use getters and virtuals when transforming to JSON
UserSchema.set('toJSON', {
	getters: true,
	virtuals: true
});

UserSchema.plugin(autoIncrement.plugin, { model: 'User', field: 'userId' });

// Create the 'User' model out of the 'UserSchema'
module.exports = mongoose.model('User', UserSchema);
