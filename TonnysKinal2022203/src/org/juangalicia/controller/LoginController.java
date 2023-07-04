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
import javax.swing.JOptionPane;
import org.juangalicia.bean.Login;
import org.juangalicia.bean.User;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class LoginController implements Initializable{
    private Principal principalStage;
    private ObservableList<User> userList;
    
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

        boolean flag = false; // flag finds registers in an ArrayList

        login.setUserMaster(txtUser.getText());
        login.setPasswordLogin(txtPassword.getText());

        while (x < getUser().size()) {
            String user = getUser().get(x).getUserLogin();
            String pass = getUser().get(x).getPasswordUser();
                if (user.equals(login.getUserMaster()) && pass.equals(login.getPasswordLogin())) {
                    showAlert(Alert.AlertType.INFORMATION, "Logged In", "Welcome!",
                    "Logged in\n" + getUser().get(x).getFirstNameUser() + " "
                            + getUser().get(x).getSecondNameUser() + "\n" + "Welcome!!");
                                principalStage.principalWindow();
                                    x = getUser().size();
                                        flag = true;
        }
            x++; // only ordinal variables
    }

        if (flag == false) {
            showAlert(Alert.AlertType.ERROR, "Incorrect User or Password", null, "Incorrect User or Password");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
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
