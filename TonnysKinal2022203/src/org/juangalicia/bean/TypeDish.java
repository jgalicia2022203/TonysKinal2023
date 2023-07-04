package org.juangalicia.bean;

public class TypeDish {
    private int codeTypeDish;
    private String descriptionType;

    public TypeDish() {
    }

    public TypeDish(int codeTypeDish, String descriptionType) {
        this.codeTypeDish = codeTypeDish;
        this.descriptionType = descriptionType;
    }

    public int getCodeTypeDish() {
        return codeTypeDish;
    }

    public void setCodeTypeDish(int codeTypeDish) {
        this.codeTypeDish = codeTypeDish;
    }

    public String getDescriptionType() {
        return descriptionType;
    }

    public void setDescriptionType(String descriptionType) {
        this.descriptionType = descriptionType;
    }

    @Override
    public String toString() {
        return codeTypeDish + " │ " + descriptionType;
    }
}
