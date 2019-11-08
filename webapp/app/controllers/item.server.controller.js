// Invoke 'strict' JavaScript mode
'use strict';

// Load the module dependencies
var Item = require('mongoose').model('Item');

exports.getItems = function(req,res,next){
    Item.find({}, function(err, items) {
        var itemMap = {};
    
        items.forEach(function(item) {
            itemMap[item._id] = item;
        });
    
        res.send(itemMap);  
      });
};

exports.getItemsRegion = function (req, res, next) {
    let region = req.query.region;
    Item.find({ 'region': region }, function(err, items) {
        var itemMap = {};

        items.forEach(function(item) {
            itemMap[item._id] = item;
        });

        res.send(itemMap);
    });
};
