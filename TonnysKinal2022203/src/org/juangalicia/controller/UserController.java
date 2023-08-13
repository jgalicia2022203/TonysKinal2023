package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.juangalicia.bean.User;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

public class UserController implements Initializable{
    private Principal principalStage;
    @FXML
    private JFXTextField txtFirstName;
    @FXML
    private JFXTextField txtSecondName;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnRegister;
    @FXML
    private JFXCheckBox chkTerms;
    @FXML
    private AnchorPane userPane;

    public UserController() {
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    public void register() {
        btnRegister.setOnAction(event -> {
            if (isFormValid()) {
                save();
                check();
                principalStage.loginWindow();
            } else {
                error();
            }
        });
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
    
    private boolean isPasswordFieldEmpty(JFXPasswordField passwordField){
        return passwordField.getText().trim().isEmpty();
    }

    private boolean isTextFieldEmpty(JFXTextField textField) {
        return textField.getText().trim().isEmpty();
    }

    private boolean isCheckBoxSelected(JFXCheckBox checkBox) {
        return checkBox.isSelected();
    }

    private boolean isFormValid() {
        return !isTextFieldEmpty(txtFirstName)
                && !isTextFieldEmpty(txtSecondName)
                && !isTextFieldEmpty(txtUsername)
                && !isPasswordFieldEmpty(txtPassword)
                && isCheckBoxSelected(chkTerms);
    }
    
    public void check(){
        Image img = new Image("org/juangalicia/image/success.png");
        Notifications notification = Notifications.create();
        notification.title("Account Registred");
        notification.graphic(new ImageView(img));
        notification.text("Welcome to Tonys");
        notification.hideAfter(Duration.seconds(5));
        notification.position(Pos.BASELINE_RIGHT);
        notification.show();
    }
    
    public void error(){
        Image img = new Image("org/juangalicia/image/error.png");
        Notifications notification = Notifications.create();
        notification.title("Error");
        notification.graphic(new ImageView(img));
        notification.text("Please fill all the fields & accept the terms & conditions");
        notification.hideAfter(Duration.seconds(5));
        notification.position(Pos.BASELINE_RIGHT);
        notification.show();
    }
    
    public void close(){
        login();
    }
    
    public void minimize() {
        Stage stage = (Stage) userPane.getScene().getWindow();
        stage.setIconified(true);
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
    
    public Principal getPrincipalStage() {
        return principalStage;
    }

    public void setPrincipalStage(Principal principalStage) {
        this.principalStage = principalStage;
    }
    
    public void login(){
        principalStage.loginWindow();
    }
}
