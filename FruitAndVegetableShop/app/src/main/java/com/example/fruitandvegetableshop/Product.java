package com.example.fruitandvegetableshop;

public class Product {
    private String name;
    private String info;
    private String price;
    private final int imgres;

    public Product(int imgres, String price, String info, String name) {
        this.imgres = imgres;
        this.price = price;
        this.info = info;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImgres() {
        return imgres;
    }
}
