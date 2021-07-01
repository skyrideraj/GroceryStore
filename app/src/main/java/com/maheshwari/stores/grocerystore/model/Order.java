package com.maheshwari.stores.grocerystore.model;


import java.util.List;

public class Order {

    String invoice_no;
    String date_ordered;
    String final_cart_price;
    String order_status;
    List<OrderDetails> order_details;




    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getDate_ordered() {
        return date_ordered;
    }

    public void setDate_ordered(String date_ordered) {
        this.date_ordered = date_ordered;
    }

    public String getFinal_cart_price() {
        return final_cart_price;
    }

    public void setFinal_cart_price(String final_cart_price) {
        this.final_cart_price = final_cart_price;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public List<OrderDetails> getOrder_details() {
        return order_details;
    }

    public void setOrder_details(List<OrderDetails> order_details) {
        this.order_details = order_details;
    }
}
