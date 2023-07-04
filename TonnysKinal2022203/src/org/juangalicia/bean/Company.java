package org.juangalicia.bean;

public class Company {
    private int codeCompany;
    private String nameCompany;
    private String adress;
    private String phone;
            
    public Company() {

    }

    public Company(int codeCompany, String nameCompany, String adress, String phone) {
        this.codeCompany = codeCompany;
        this.nameCompany = nameCompany;
        this.adress = adress;
        this.phone = phone;
    }
    

    public int getCodeCompany() {
        return codeCompany;
    }

    public void setCodeCompany(int codeCompany) {
        this.codeCompany = codeCompany;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public void setNameCompany(String nameCompany) {
        this.nameCompany = nameCompany;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Override
    public String toString() {
        return codeCompany + "│" + nameCompany;
    }

}
