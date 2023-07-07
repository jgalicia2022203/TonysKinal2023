/*
    Name: Juan Luis Galicia Mazariegos
    Technical code: IN5BV
    ID: 2022203       
*/

package org.juangalicia.main;

import java.io.InputStream;

import org.juangalicia.controller.BudgetController;
import org.juangalicia.controller.CompanyController;
import org.juangalicia.controller.EmployeeController;
import org.juangalicia.controller.PrincipalMenuController;
import org.juangalicia.controller.ProductController;
import org.juangalicia.controller.ProgrammerController;
import org.juangalicia.controller.TypeDishController;
import org.juangalicia.controller.TypeEmployeeController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.juangalicia.controller.DishController;
import org.juangalicia.controller.LoginController;
import org.juangalicia.controller.Products_has_DishesController;
import org.juangalicia.controller.ServiceController;
import org.juangalicia.controller.Services_has_DishesController;
import org.juangalicia.controller.Services_has_EmployeesController;
import org.juangalicia.controller.UserController;

import javafx.application.Platform;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/juangalicia/view/";
    private Stage principalStage;
    private Scene scene;

    @Override
    public void start(Stage principalStage) throws Exception {
        this.principalStage = principalStage;
        this.principalStage.setTitle("Tony's");
        principalStage.initStyle(StageStyle.TRANSPARENT);
        principalStage.getIcons().add(new Image("/org/juangalicia/image/Tonys.png"));
        principalStage.setResizable(false);
        loginWindow();
        principalStage.show();
    }

    public void principalWindow() {
        try {
            PrincipalMenuController menu = (PrincipalMenuController) switchScene("PrincipalMenuView.fxml", 1366, 768);
            principalStage.centerOnScreen();
            menu.setPrincipalStage(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void programmerWindow() {
        try {
            ProgrammerController programmer = (ProgrammerController) switchScene("ProgrammerView.fxml", 958, 454);
            programmer.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void companyWindow() {
        try {
            CompanyController company = (CompanyController) switchScene("CompanyView.fxml", 1366, 768);
            company.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void budgetWindow() {
        try {
            BudgetController budget = (BudgetController) switchScene("BudgetView.fxml", 1366, 768);
            budget.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void typeEmployeeWindow() {
        try {
            TypeEmployeeController typeE = (TypeEmployeeController) switchScene("TypeEmployeeView.fxml", 1366, 768);
            typeE.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void productWindow() {
        try {
            ProductController product = (ProductController) switchScene("ProductView.fxml", 1366, 768);
            product.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void employeeWindow() {
        try {
            EmployeeController employee = (EmployeeController) switchScene("EmployeeView.fxml", 1366, 768);
            employee.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void typeDishWindow() {
        try {
            TypeDishController typeDish = (TypeDishController) switchScene("TypeDishView.fxml", 1366, 768);
            typeDish.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void dishWindow() {
        try {
            DishController dish = (DishController) switchScene("DishView.fxml", 1366, 768);
            dish.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void serviceWindow(){
        try {
            ServiceController service = (ServiceController) switchScene("ServiceView.fxml", 1366, 768);
            service.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void Products_has_DishesWindow(){
        try {
            Products_has_DishesController PhD = (Products_has_DishesController) switchScene("ProductsHasDishesView.fxml", 958, 454);
            PhD.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void Services_has_DishesWindow(){
        try {
            Services_has_DishesController ShD = (Services_has_DishesController) switchScene("ServicesHasDishesView.fxml", 958, 454);
            ShD.setPrincipalStage(this);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void Services_has_EmployeesWindow(){
        try {
            Services_has_EmployeesController ShE = (Services_has_EmployeesController) switchScene("ServicesHasEmployeesView.fxml", 958, 454);
            ShE.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void userWindow(){
        try{
            UserController user = (UserController) switchScene("UserView.fxml", 958, 454);
            user.setPrincipalStage(this);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loginWindow(){
        try {
            LoginController login = (LoginController) switchScene("LoginView.fxml", 1366, 768);
            login.setPrincipalStage(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    public Initializable switchScene(String fxml, int width, int height) throws Exception {
    Initializable result = null;
    FXMLLoader loaderFXML = new FXMLLoader();
    InputStream file = Principal.class.getResourceAsStream(PAQUETE_VISTA + fxml);
    loaderFXML.setBuilderFactory(new JavaFXBuilderFactory());
    loaderFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA + fxml));
    scene = new Scene((AnchorPane) loaderFXML.load(file), width, height);
    principalStage.setScene(scene);
    principalStage.sizeToScene();
    principalStage.setResizable(false);

    result = (Initializable) loaderFXML.getController();

    principalStage.setOnShown((WindowEvent event) -> {
        Platform.runLater(() -> {
            principalStage.centerOnScreen();
        });
    });

    return result;
    }


}
