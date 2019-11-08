// Invoke 'strict' JavaScript mode
'use strict';

// Load the module dependencies
var User = require('mongoose').model('User'),
	passport = require('passport'),
	jwt = require('jsonwebtoken'),
	config = require('../../config/config');

// Create a new error handling controller method
var getErrorMessage = function(err) {
	// Define the error message variable
	var message = '';

	// If an internal MongoDB error occurs get the error message
	if (err.code) {
		switch (err.code) {
			// If a unique index error occurs set the message error
			case 11000:
			case 11001:
				message = 'Username already exists';
				break;
			// If a general error occurs set the message error
			default:
				message = 'Something went wrong';
		}
	} else {
		// Grab the first error message from a list of possible errors
		for (var errName in err.errors) {
			if (err.errors[errName].message) message = err.errors[errName].message;
		}
	}

	// Return the message error
	return message;
};

// Create a new controller method that creates new 'regular' users
exports.signup = function(req, res, next) {
	// If user is not connected, create and login a new user, otherwise redirect the user back to the main application page
	if (!req.user) {
		// Create a new 'User' model instance
		var user = new User(req.body);
		var message = null;

		// Set the user provider property
		user.provider = 'local';

		// Try saving the new user document
		user.save(function(err) {
			// If an error occurs, use flash messages to report the error
			if (err) {
				// Use the error handling method to get the error message
				var message = getErrorMessage(err);

				return res.json({message:message});
			}

			// If the user was created successfully use the Passport 'login' method to login
			req.login(user, { session: false }, (err) => {
				if (err) {
					res.send(err);
				}
				var message = "User created successfully";
				// generate a signed son web token with the contents of user object and return it in the response
				const token = jwt.sign({id: user.id}, config.sessionSecret, {
					expiresIn: 604800 // 1 week
				});
				return res.json({ token,  message});
			});
		});
	} else {
		return res.redirect('/');
	}
};

exports.requiresLogin = function(req, res, next) {
  if (!req.isAuthenticated()) {
    return res.status(401).send({
      message: 'User is not logged in'
    });
  }

  next();
};

// Create a new controller method for signing out
exports.signout = function(req, res) {
	// Use the Passport 'logout' method to logout
	req.logout();

	// Redirect the user back to the main application page
	res.redirect('/');
};

exports.getUserIdList = function(req, res){
	User.find({}).select('userId firstName lastName role username').exec(function(err, users) {
		if (err) {
		  return res.status(400).send({
			message: getErrorMessage(err)
		  });
		} else {
		  res.json(users);
		}
	});
};

exports.changeUserRole = function(req, res, next){
	var user = new User(req.body);
	console.log(user);
	var query = { _id: user._id };
	User.findOneAndUpdate(query,{role:user.role}).exec(function(err) {
		if (err) {
		  return res.status(400).send({
			message: getErrorMessage(err)
		  });
		} else {
		  res.json({message: user.username+' : User role updated successfully...'});
		}
	});
};