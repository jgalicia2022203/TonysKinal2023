package org.juangalicia.bean;

public class Employee {
    private int codeEmployee;
    private int numberEmployee;
    private String secondNameEmployee;
    private String firstNameEmployee;
    private String adressEmployee;
    private String contactPhone;
    private String cookDegree;
    private int codeTypeEmployee;

    public Employee() {
    }

    public Employee(int codeEmployee, int numberEmployee, String secondNameEmployee, String firstNameEmployee,
            String adressEmployee, String contactPhone, String cookDegree, int codeTypeEmployee) {
        this.codeEmployee = codeEmployee;
        this.numberEmployee = numberEmployee;
        this.secondNameEmployee = secondNameEmployee;
        this.firstNameEmployee = firstNameEmployee;
        this.adressEmployee = adressEmployee;
        this.contactPhone = contactPhone;
        this.cookDegree = cookDegree;
        this.codeTypeEmployee = codeTypeEmployee;
    }

    public int getCodeEmployee() {
        return codeEmployee;
    }

    public void setCodeEmployee(int codeEmployee) {
        this.codeEmployee = codeEmployee;
    }

    public int getNumberEmployee() {
        return numberEmployee;
    }

    public void setNumberEmployee(int numberEmployee) {
        this.numberEmployee = numberEmployee;
    }

    public String getSecondNameEmployee() {
        return secondNameEmployee;
    }

    public void setSecondNameEmployee(String secondNameEmployee) {
        this.secondNameEmployee = secondNameEmployee;
    }

    public String getFirstNameEmployee() {
        return firstNameEmployee;
    }

    public void setFirstNameEmployee(String firstNameEmployee) {
        this.firstNameEmployee = firstNameEmployee;
    }

    public String getAdressEmployee() {
        return adressEmployee;
    }

    public void setAdressEmployee(String adressEmployee) {
        this.adressEmployee = adressEmployee;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCookDegree() {
        return cookDegree;
    }

    public void setCookDegree(String cookDegree) {
        this.cookDegree = cookDegree;
    }

    public int getCodeTypeEmployee() {
        return codeTypeEmployee;
    }

    public void setCodeTypeEmployee(int codeTypeEmployee) {
        this.codeTypeEmployee = codeTypeEmployee;
    }

    @Override
    public String toString() {
        return codeEmployee + " │ " + secondNameEmployee + " " + firstNameEmployee;
    }
    
    

}
