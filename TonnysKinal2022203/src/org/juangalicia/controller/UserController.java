package org.juangalicia.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private TextField txtUserId;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtSecondName;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;
    @FXML
    private ImageView imgCreate;
    @FXML 
    private ImageView imgCancel;

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
                imgCreate.setImage(new Image("/org/juangalicia/image/save.png"));
                imgCancel.setImage(new Image("/org/juangalicia/image/cancel.png"));
                typeOfOperation = operations.SAVE;
                break;
                
            case SAVE:
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Create User");
                btnCancel.setText("Delete User");
                imgCreate.setImage(new Image("/org/juangalicia/image/Add Employee.png"));
                imgCancel.setImage(null);
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
                btnCreate.setText("Create User");
                btnCancel.setText("");
                imgCreate.setImage(new Image("/org/juangalicia/image/Add Employee.png"));
                imgCancel.setImage(null);
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
        txtUserId.setEditable(false);
        txtFirstName.setEditable(true);
        txtSecondName.setEditable(true);
        txtUsername.setEditable(true);
        txtPassword.setEditable(true);
    }
    
    public void lockControls(){
        txtUserId.setEditable(false);
        txtFirstName.setEditable(false);
        txtSecondName.setEditable(false);
        txtUsername.setEditable(false);
        txtPassword.setEditable(false);
    }
    
    public void clearControls(){
        txtUserId.clear();
        txtFirstName.clear();
        txtSecondName.clear();
        txtUsername.clear();
        txtPassword.clear();
    }
    
    public void menuPrincipal() {
        principalStage.principalWindow();
    }
}
