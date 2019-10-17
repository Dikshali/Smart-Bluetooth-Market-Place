package com.practice.bluetoothbeacondetection.models;

public class CartItems {
    String itemName, region, _id, photo;
    Double discount, price;
    int discountPrice;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public CartItems(String itemName, String region, String _id, String photo, Double discount, Double price, int discountPrice) {
        this.itemName = itemName;
        this.region = region;
        this._id = _id;
        this.photo = photo;
        this.discount = discount;
        this.discountPrice = discountPrice;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }
}
