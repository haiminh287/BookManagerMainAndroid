package com.example.bookmanager.model;

public class ResponsePayment {
    private String momo_deep_link;

    public String getMomo_deep_link() {
        return momo_deep_link;
    }

    public void setMomo_deep_link(String momo_deep_link) {
        this.momo_deep_link = momo_deep_link;
    }

    public ResponsePayment(String momo_deep_link) {
        this.momo_deep_link = momo_deep_link;
    }
}
