package org.juangalicia.bean;

public class Login {
    private String userMaster;
    private String passwordLogin;

    public Login() {
    }

    public Login(String userMaster, String passwordLogin) {
        this.userMaster = userMaster;
        this.passwordLogin = passwordLogin;
    }

    public String getUserMaster() {
        return userMaster;
    }

    public void setUserMaster(String userMaster) {
        this.userMaster = userMaster;
    }

    public String getPasswordLogin() {
        return passwordLogin;
    }

    public void setPasswordLogin(String passwordLogin) {
        this.passwordLogin = passwordLogin;
    }
    
}
