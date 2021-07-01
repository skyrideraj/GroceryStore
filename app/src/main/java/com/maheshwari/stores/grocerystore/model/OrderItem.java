package com.maheshwari.stores.grocerystore.model;

public class OrderItem {
    String product_id;
    String quantity;
    String price;
    String discounted_price;
    String subtotal;

    public OrderItem() {
    }

    public OrderItem(String product_id, String quantity, String price, String discounted_price, String subtotal) {
        this.product_id = product_id;
        this.quantity = quantity;
        this.price = price;
        this.discounted_price = discounted_price;
        this.subtotal = subtotal;
    }
}
