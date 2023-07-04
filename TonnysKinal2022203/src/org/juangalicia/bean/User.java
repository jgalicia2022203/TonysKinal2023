package org.juangalicia.bean;

public class User {
    private int codeUser;
    private String firstNameUser;
    private String secondNameUser;
    private String userLogin;
    private String passwordUser;

    public User() {
    }

    public User(int codeUser, String firstNameUser, String secondNameUser, String userLogin, String passwordUser) {
        this.codeUser = codeUser;
        this.firstNameUser = firstNameUser;
        this.secondNameUser = secondNameUser;
        this.userLogin = userLogin;
        this.passwordUser = passwordUser;
    }

    public int getCodeUser() {
        return codeUser;
    }

    public void setCodeUser(int codeUser) {
        this.codeUser = codeUser;
    }

    public String getFirstNameUser() {
        return firstNameUser;
    }

    public void setFirstNameUser(String firstNameUser) {
        this.firstNameUser = firstNameUser;
    }

    public String getSecondNameUser() {
        return secondNameUser;
    }

    public void setSecondNameUser(String secondNameUser) {
        this.secondNameUser = secondNameUser;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }
    
    
}
