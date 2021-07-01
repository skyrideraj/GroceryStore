package com.maheshwari.stores.grocerystore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceOrder {
    @SerializedName("token")
    String token;
    @SerializedName("fname")
    String fname;
    @SerializedName("email")
    String email;
    @SerializedName("mobile")
    String mobile;
    @SerializedName("area")
    String area;
    @SerializedName("address")
    String address;
    @SerializedName("user_id")
    String user_id;
    @SerializedName("orderitems")
    List<OrderItem> orderitems;
    @SerializedName("total_cart_amount")
    String total_cart_amount;
    @SerializedName("final_cart_amount")
    String final_cart_amount;
    @SerializedName("date_ordered")
    String date_ordered;

    public PlaceOrder(String token, String fname, String email, String mobile, String area, String address, String user_id, List<OrderItem> orderitems, String total_cart_amount, String final_cart_amount, String date_ordered) {
        this.token = token;
        this.fname = fname;
        this.email = email;
        this.mobile = mobile;
        this.area = area;
        this.address = address;
        this.user_id = user_id;
        this.orderitems = orderitems;
        this.total_cart_amount = total_cart_amount;
        this.final_cart_amount = final_cart_amount;
        this.date_ordered = date_ordered;
    }
}
