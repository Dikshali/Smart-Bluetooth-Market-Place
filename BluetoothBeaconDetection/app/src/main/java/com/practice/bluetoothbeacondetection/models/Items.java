package com.practice.bluetoothbeacondetection.models;

import java.io.Serializable;

public class Items implements Serializable {

    String itemName, region, id, photo;
    Double discount;

    @Override
    public String toString() {
        return "{" +
                "name='" + itemName + '\'' +
                ", region='" + region + '\'' +
                ", id='" + id + '\'' +
                ", photo='" + photo + '\'' +
                ", discount=" + discount +
                ", price=" + price +
                '}';
    }

    Double price;

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

    public String getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String image) {
        this.photo = image;
    }

    public Items(String itemName, String region, String id, Double discount, Double price, String image) {
        this.itemName = itemName;
        this.region = region;
        this.id = id;
        this.discount = discount;
        this.price = price;
        this.photo = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
