package com.example.bookmanager.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CardItem {
    private int id;
    private String title;
    private String description;
    @SerializedName("image")
    private String urlImage;
    private List<Price> prices;
    private int quantity;
    private String author;
    public static class Price {
        private String name_price;
        private float price;

        public Price(String name_price, float price) {
            this.name_price = name_price;
            this.price = price;
        }

        public String getNamePrice() {
            return name_price;
        }

        public float getPrice() {
            return price;
        }
    }


    public CardItem(String title, String description) {
        this.title = title;
        this.description = description;
    }
    public CardItem(int id ,String title,String description, String urlImage,String author,List<Price> prices,int quantity){
        this.id = id;
        this.title = title;
        this.description = description;
        this.urlImage = urlImage;
        this.author=author;
        this.prices = prices;
        this.quantity=quantity;
    }

    public int getId(){
        return id;
    }
    public String getTitle() {
        return title;
    }

    public float getPrice() {
        return prices.isEmpty() ? 0 : prices.get(0).getPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }
    public String getUrlImage() {
        return urlImage;
    }
    public String getAuthor(){
        return author;
    }
}

