package com.practice.bluetoothbeacondetection.models;

public class HistoryItems {
    String transactionId,totalAmount;

    public HistoryItems(String transactionId, String totalAmount) {
        this.transactionId = transactionId;
        this.totalAmount = totalAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
