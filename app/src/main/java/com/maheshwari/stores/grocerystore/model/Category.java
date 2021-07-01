package com.maheshwari.stores.grocerystore.model;


public class Category {
    String category_id;
    String category;
    String category_img;
    String token;

    public Category(String category_id, String category, String category_img) {
        this.category_id = category_id;
        this.category = category;
        this.category_img = category_img;
    }

    public Category(String category, String token) {
        this.category = category;
        this.token = token;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_img() {
        return category_img;
    }

    public void setCategory_img(String category_img) {
        this.category_img = category_img;
    }
}
