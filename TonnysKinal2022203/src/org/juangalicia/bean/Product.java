package org.juangalicia.bean;

public class Product {
    private int codeProduct;
    private String nameProduct;
    private int quantity;

    public Product() {
    }

    public Product(int codeProduct, String nameProduct, int quantity) {
        this.codeProduct = codeProduct;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
    }

    public int getCodeProduct() {
        return codeProduct;
    }

    public void setCodeProduct(int codeProduct) {
        this.codeProduct = codeProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return codeProduct + " │ " + nameProduct;
    }
    
    

}
