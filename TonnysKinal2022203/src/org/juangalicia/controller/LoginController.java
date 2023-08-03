package org.juangalicia.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.juangalicia.bean.Login;
import org.juangalicia.bean.User;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

public class LoginController implements Initializable{
    
    private Principal principalStage;
    private ObservableList<User> userList;
    @FXML
    private AnchorPane loginPane;
    @FXML 
    private JFXTextField txtUser;
    @FXML 
    private JFXPasswordField txtPassword;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
    
    public ObservableList<User>getUser(){
        ArrayList<User> list = new ArrayList<User>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("{call sp_ReadUsers()}");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
                list.add(new User(result.getInt("codeUser"), result.getString("firstNameUser"),
                result.getString("secondNameUser"), result.getString("userLogin"),  
                result.getString("passwordUser")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList = FXCollections.observableArrayList(list);
    }
    
    @FXML
    public void login() {
        Login login = new Login();
        int x = 0;
        boolean flag = false;

        login.setUserMaster(txtUser.getText());
        login.setPasswordLogin(txtPassword.getText());

        while (x < getUser().size()) {
            String user = getUser().get(x).getUserLogin();
            String pass = getUser().get(x).getPasswordUser();
                if (user.equals(login.getUserMaster()) && pass.equals(login.getPasswordLogin())) {
                    check();
                    principalStage.principalWindow();
                    x = getUser().size();
                    flag = true;
                }
            x++; 
        }

        if (flag == false) {
            error();
        }
    }

    public void check(){
        Image img = new Image("org/juangalicia/image/success.png");
        Notifications notification = Notifications.create();
        notification.title("Login Succeded");
        notification.graphic(new ImageView(img));
        notification.text("Welcome");
        notification.hideAfter(Duration.seconds(5));
        notification.position(Pos.BASELINE_RIGHT);
        notification.show();
    }
    
    public void error(){
        Image img = new Image("org/juangalicia/image/error.png");
        Notifications notification = Notifications.create();
        notification.title("Error");
        notification.graphic(new ImageView(img));
        notification.text("Username or password is incorrect");
        notification.hideAfter(Duration.seconds(5));
        notification.position(Pos.BASELINE_RIGHT);
        notification.show();
    }
    
    public void minimize() {
        Stage stage = (Stage) loginPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    public void close(){
        System.exit(0);
    }
    
    public Principal getPrincipalStage() {
        return principalStage;
    }

    public void setPrincipalStage(Principal principalStage) {
        this.principalStage = principalStage;
    }
    
    public void userWindow(){
        principalStage.userWindow();
    }
}
