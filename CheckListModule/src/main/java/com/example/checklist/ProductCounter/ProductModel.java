package com.example.checklist.ProductCounter;

public class ProductModel {

    private long productId;
    private int stock;
    private String shopId;
    private String productName;

    public ProductModel() {

    }
    public ProductModel(long productId,int stock,String shopId,String productName) {
        this.productId = productId;
        this.stock = stock;
        this.shopId = shopId;
        this.productName = productName;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
