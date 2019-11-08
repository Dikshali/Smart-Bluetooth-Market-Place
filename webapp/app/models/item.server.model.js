// Invoke 'strict' JavaScript mode
'use strict';

var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var ItemSchema = new Schema({
    name: {
        type: String,
        trim: true,
        required: 'Item name is required'
    },
    price: {
        type: Number,
        required: 'Item price is required'
    },
    region: String,
    photo: String,
    discount: Number,
    discountPrice: Number
});

ItemSchema.set('toJSON', {
    getters: true,
    virtuals: true
});

module.exports = mongoose.model('Item', ItemSchema);
