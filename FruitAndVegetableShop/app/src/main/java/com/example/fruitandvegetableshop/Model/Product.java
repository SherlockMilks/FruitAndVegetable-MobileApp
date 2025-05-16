package com.example.fruitandvegetableshop.Model;

import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {
    private String documentId="";
    private String name;
    private String info;
    private int price;
    private int imgres;

    public Product(){}

    public Product(int imgres, int price, String info, String name) {
        this.imgres = imgres;
        this.price = price;
        this.info = info;
        this.name = name;
    }

    public Product(Product product) {
        this.name=product.name;
        this.info=product.info;
        this.price=product.price;
        this.imgres=product.imgres;
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImgres() {
        return imgres;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(this.name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imgres, price, info, name);
    }

    @Override
    public String toString() {
        return "name:"+this.name+" price:"+this.price+" info:"+this.info+" imgres:"+this.imgres;
    }
}
