package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.juangalicia.bean.User;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

public class UserController implements Initializable{
    private Principal principalStage;
    private enum operations{
        SAVE, DELETE, UPDATE, NONE
    }
    
    private operations typeOfOperation = operations.NONE;
    
    @FXML
    private JFXTextField txtFirstName;
    @FXML
    private JFXTextField txtSecondName;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private JFXButton btnCancel;

    public UserController() {
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
    }
    
    public void create(){
        switch(typeOfOperation){
            case NONE:
                clearControls();
                unlockControls();
                btnCreate.setText("Save");
                btnCancel.setText("Cancel");
                typeOfOperation = operations.SAVE;
                break;
                
            case SAVE:
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Register");
                btnCancel.setText(" ");
                typeOfOperation = operations.NONE;
                login();
                break;
        }
    }
    
    public void cancel(){
        switch(typeOfOperation){
            case SAVE: 
                clearControls();
                lockControls();
                btnCreate.setText("Register");
                btnCancel.setText(" ");
                typeOfOperation = operations.NONE;
                break;
        }
    }
    
    public void save(){
        User register = new User();
        register.setFirstNameUser(txtFirstName.getText());
        register.setSecondNameUser(txtSecondName.getText());
        register.setUserLogin(txtUsername.getText());
        register.setPasswordUser(txtPassword.getText());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("{call sp_CreateUser(?,?,?,?)}");
            procedure.setString(1, register.getFirstNameUser());
            procedure.setString(2, register.getSecondNameUser());
            procedure.setString(3, register.getUserLogin());
            procedure.setString(4, register.getPasswordUser());
            procedure.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void close(){
        login();
    }
    
    public Principal getPrincipalStage() {
        return principalStage;
    }

    public void setPrincipalStage(Principal principalStage) {
        this.principalStage = principalStage;
    }
    
    public void login(){
        principalStage.loginWindow();
    }
    
    public void unlockControls(){
        txtFirstName.setEditable(true);
        txtSecondName.setEditable(true);
        txtUsername.setEditable(true);
        txtPassword.setEditable(true);
    }
    
    public void lockControls(){
        txtFirstName.setEditable(false);
        txtSecondName.setEditable(false);
        txtUsername.setEditable(false);
        txtPassword.setEditable(false);
    }
    
    public void clearControls(){
        txtFirstName.clear();
        txtSecondName.clear();
        txtUsername.clear();
        txtPassword.clear();
    }
    
    public void menuPrincipal() {
        principalStage.principalWindow();
    }
}
