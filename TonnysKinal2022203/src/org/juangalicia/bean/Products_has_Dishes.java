package org.juangalicia.bean;

public class Products_has_Dishes {
    private int Products_codeProduct;
    private int codeDish;
    private int codeProduct;

    public Products_has_Dishes() {
    }

    public Products_has_Dishes(int Products_codeProduct, int codeDish, int codeProduct) {
        this.Products_codeProduct = Products_codeProduct;
        this.codeDish = codeDish;
        this.codeProduct = codeProduct;
    }

    public int getProducts_codeProduct() {
        return Products_codeProduct;
    }

    public void setProducts_codeProduct(int Products_codeProduct) {
        this.Products_codeProduct = Products_codeProduct;
    }

    public int getCodeDish() {
        return codeDish;
    }

    public void setCodeDish(int codeDish) {
        this.codeDish = codeDish;
    }

    public int getCodeProduct() {
        return codeProduct;
    }

    public void setCodeProduct(int codeProduct) {
        this.codeProduct = codeProduct;
    }
    
}
