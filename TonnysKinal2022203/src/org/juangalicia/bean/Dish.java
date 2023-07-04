package org.juangalicia.bean;

public class Dish {
    private int codeDish;
    private int quantity;
    private String nameDish;
    private String descriptionDish;
    private double priceDish;
    private int codeTypeDish;

    public Dish() {
    }

    public Dish(int codeDish, int quantity, String nameDish, String descriptionDish, double priceDish,
            int codeTypeDish) {
        this.codeDish = codeDish;
        this.quantity = quantity;
        this.nameDish = nameDish;
        this.descriptionDish = descriptionDish;
        this.priceDish = priceDish;
        this.codeTypeDish = codeTypeDish;
    }

    public int getCodeDish() {
        return codeDish;
    }

    public void setCodeDish(int codeDish) {
        this.codeDish = codeDish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNameDish() {
        return nameDish;
    }

    public void setNameDish(String nameDish) {
        this.nameDish = nameDish;
    }

    public String getDescriptionDish() {
        return descriptionDish;
    }

    public void setDescriptionDish(String descriptionDish) {
        this.descriptionDish = descriptionDish;
    }

    public double getPriceDish() {
        return priceDish;
    }

    public void setPriceDish(double priceDish) {
        this.priceDish = priceDish;
    }

    public int getCodeTypeDish() {
        return codeTypeDish;
    }

    public void setCodeTypeDish(int codeTypeDish) {
        this.codeTypeDish = codeTypeDish;
    }

    @Override
    public String toString() {
        return codeDish + " │ " + nameDish;
    }
    
    

}
