// Invoke 'strict' JavaScript mode
'use strict';
var express = require('express'),
    router = express.Router(),
    profile = require('../controllers/profile.server.controller');

// Define the routes module' method

router.get('/', function (req, res, next) {
    res.send('respond with a resource');
});

/* GET user profile. */
router.get('/profile', function (req, res, next) {
    res.send(req.user);
});

router.post('/edit', profile.edit);

router.post('/client_token', profile.getClientToken);

router.post("/checkout", profile.checkout);

router.post("/addItem", profile.addItem);

router.post("/deleteItem", profile.deleteItem);

router.post("/createCard",profile.createCard);

router.post("/updateCard",profile.updateCard);

router.post("/deleteCard",profile.deleteCard);

router.get("/listAllCards", profile.listAllCards);

module.exports = router;

