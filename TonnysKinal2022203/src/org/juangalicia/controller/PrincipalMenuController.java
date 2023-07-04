package org.juangalicia.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.juangalicia.main.Principal;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class PrincipalMenuController implements Initializable {
    private Principal principalStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Principal getPrincipalStage() {
        return principalStage;
    }

    public void setPrincipalStage(Principal principalStage) {
        this.principalStage = principalStage;
    }

    public void programmerWindow() {
        principalStage.programmerWindow();
    }

    public void companyWindow() {
        principalStage.companyWindow();
    }

    public void typeEmployeeWindow() {
        principalStage.typeEmployeeWindow();
    }

    public void productWindow() {
        principalStage.productWindow();
    }

    public void employeeWindow() {
        principalStage.employeeWindow();
    }

    public void typeDishWindow() {
        principalStage.typeDishWindow();
    }

    public void budgetWindow() {
        principalStage.budgetWindow();
    }
    
    public void dishWindow(){
        principalStage.dishWindow();
    }
    
    public void serviceWindow(){
        principalStage.serviceWindow();
    }
    
    public void userWindow(){
        principalStage.userWindow();
    }
    
    public void Products_has_DishesWindow(){
        principalStage.Products_has_DishesWindow();
    }
    
    public void Services_has_DishesWindow(){
        principalStage.Services_has_DishesWindow();
    }
    
    public void Services_has_EmployeesWindow(){
        principalStage.Services_has_EmployeesWindow();
    }
    
    public void loginWindow(){
        principalStage.loginWindow();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
    }
    
    public void logout() {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle("Logout");
       alert.setHeaderText("Are you sure you want to logout?");
       alert.setContentText("Choose your option.");
       ButtonType buttonTypeYes = new ButtonType("Yes");
       ButtonType buttonTypeNo = new ButtonType("No");
       alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
       
        Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            principalStage.loginWindow();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        principalStage.principalWindow();
                }
    }
    
}
