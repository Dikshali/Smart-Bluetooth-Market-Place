package com.practice.bluetoothbeacondetection.models;


import com.practice.bluetoothbeacondetection.utilities.Parameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String customerId;
    private String userId;
    private String userProfileImageUrl;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String city;
    private String gender;

    public User(String firstName, String lastName, String email, String username, String password, String city, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.city = city;
        this.gender = gender;
    }

    public User() {

    }

    public User(JSONObject json){
        try {
            this.id = json.getString(Parameters.ID);
            this.customerId = json.getString(Parameters.CUSTOMER_ID);
            this.userId = json.getString(Parameters.USER_ID);
            this.firstName = json.getString(Parameters.FIRST_NAME);
            this.lastName = json.getString(Parameters.LAST_NAME);
            this.email = json.getString(Parameters.EMAIL);
            this.username = json.getString(Parameters.USERNAME);
            this.city = json.getString(Parameters.CITY);
            this.gender = json.getString(Parameters.GENDER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
