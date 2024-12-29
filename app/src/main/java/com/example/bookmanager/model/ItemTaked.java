package com.example.bookmanager.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ItemTaked {
    private int id;
    private String title;
    private float price;
    private int quantity;
    @SerializedName("image")
    private String urlImage;

    public ItemTaked(int id,String title, float price, int quantity,String urlImage) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.urlImage =urlImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
    @NonNull
    @Override
    public String toString() {
        return "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", urlImage='" + urlImage + '\'';
    }
}
