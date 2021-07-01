package com.maheshwari.stores.grocerystore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrdersResult {
    @SerializedName("code")
    int code;
    @SerializedName("status")
    String status;
    @SerializedName("message")
    String message;
    @SerializedName("orders")
    List<Order> orderList;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
