package com.example.fruitandvegetableshop.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Order implements Serializable {
    private String email;
    private HashMap<String,Integer> products;
    private Date timeStamp;

    public Order(String email, HashMap<String, Integer> products) {
        this.email = email;
        this.products = products;
        this.timeStamp=new Date();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Order() {}

    public String getEmail() {
        return email;
    }

    public void setUser(String email) {
        this.email = email;
    }

    public HashMap<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, Integer> products) {
        this.products = products;
    }
}
