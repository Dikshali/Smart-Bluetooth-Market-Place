// Invoke 'strict' JavaScript mode
'use strict';

// Load the module dependencies
var users = require('../../app/controllers/users.server.controller'),
	passport = require('passport'),
	jwt = require('jsonwebtoken'),
	express = require('express'),
	router = express.Router(),
	config = require('../../config/config');

// Set up the 'signup' routes 
router.post('/signup', users.signup);

// Set up the 'signin' routes 
router.post('/signin', function (req, res, next) {
	passport.authenticate('local', { session: false }, (err, user, info) => {
		if (err || !user) {
			return res.status(400).json({
				message: 'Invalid username or password',
				user: user
			});
		}
		req.login(user, { session: false }, (err) => {
			if (err) {
				res.send(err);
			}
			var message = 'Logged in Successfully';
			// generate a signed son web token with the contents of user object and return it in the response
			const token = jwt.sign({id: user.id}, config.sessionSecret, {
				expiresIn: 604800 // 1 week
			});
			return res.json({ token, message });
		});
	})(req, res);
});

// Set up the 'signout' route
router.get('/signout', users.signout);

module.exports = router;
