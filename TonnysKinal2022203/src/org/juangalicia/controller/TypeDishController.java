package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import org.juangalicia.bean.TypeDish;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;
import org.juangalicia.report.GenerateReport;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class TypeDishController implements Initializable {
    
    private Principal principalStage;
    private ObservableList<TypeDish> typeDishList;
    private final String Background="/org/juangalicia/image/Report Background.png";   

    @FXML
    private JFXButton btnCreate;
    @FXML
    private TableView tblTypeDish;
    @FXML
    private TableColumn colTypeDishId;
    @FXML
    private TableColumn colDescription;
    @FXML
    private JFXTextField txtTypeDishId;
    @FXML
    private JFXTextField txtDescription;
    @FXML 
    private JFXTextField txtSearchTypeDish;
    @FXML
    private AnchorPane TypeDishPane;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
    }

    public void loadData() {
        tblTypeDish.setItems(getTypeDish());
        colTypeDishId.setCellValueFactory(new PropertyValueFactory<TypeDish, Integer>("codeTypeDish"));
        colDescription.setCellValueFactory(new PropertyValueFactory<TypeDish, String>("descriptionType"));
    }
    
    public void selectElement() {
        txtTypeDishId.setText(
                String.valueOf(((TypeDish) tblTypeDish.getSelectionModel().getSelectedItem()).getCodeTypeDish()));
        txtDescription.setText(((TypeDish) tblTypeDish.getSelectionModel().getSelectedItem()).getDescriptionType());
    }

    public ObservableList<TypeDish> getTypeDish() {
        ArrayList<TypeDish> list = new ArrayList<TypeDish>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadTypeDishes");
            ResultSet result = procedure.executeQuery();
            while (result.next()) {
                list.add(new TypeDish(result.getInt("codeTypeDish"),
                        result.getString("descriptionType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return typeDishList = FXCollections.observableArrayList(list);
    }

    public void create() {
        btnCreate.setOnAction(event -> {
            if (isFormValid()) {
                if (isDataExistsInTableView(txtTypeDishId.getText())) {
                    clearControls();
                } else {
                    if (showConfirmationDialog("Add Type Dish", "You want to add this Type Dish?", 
                            "Choose your option.", "Save", "Cancel")){
                        try {
                            save();
                            loadData();
                            clearControls();
                            notification(NotificationType.SUCCESS, "Type Dish added successfully", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        loadData();
                    }
                }
            } else {
                notification(NotificationType.ERROR, "please complete all the fields", 5);
            }
        });
    }
    
    public void save() {
        TypeDish register = new TypeDish();
        register.setDescriptionType(txtDescription.getText());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_CreateTypeDish(?)");
            procedure.setString(1, register.getDescriptionType());
            procedure.execute();
            typeDishList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void edit() {
        if (tblTypeDish.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Update Type Dish", "You want to modify this Type Dish?", 
                            "Choose your option.", "Update", "Cancel")) {
                            try {
                                update();
                                loadData();
                                clearControls();
                                notification(NotificationType.SUCCESS, "Type Dish successfully updated from database", 5);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            loadData();
                        }      
                }
                else {
                    notification(NotificationType.ERROR, "Please select an element from the table", 5);
                }
    }

    public void update() {
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_UpdateTypeDish(?,?)");
            TypeDish register = (TypeDish) tblTypeDish.getSelectionModel().getSelectedItem();
            register.setDescriptionType(txtDescription.getText());
            procedure.setInt(1, register.getCodeTypeDish());
            procedure.setString(2, register.getDescriptionType());
            procedure.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        if (tblTypeDish.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Delete Type Dish", "Are you sure of deleting this register?", 
                            "Choose your option.", "Delete", "Cancel")){
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_DeleteTypeDish(?)");
                            procedure.setInt(1,((TypeDish) tblTypeDish.getSelectionModel().getSelectedItem()).getCodeTypeDish());
                            procedure.execute();
                            typeDishList.remove(tblTypeDish.getSelectionModel().getSelectedItem());
                            clearControls();
                            notification(NotificationType.SUCCESS, "Type Diish successfully deleted from database", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    notification(NotificationType.ERROR, "Please select an element from the table", 5);
                }
    }
    
    public void printReport() {
        try {
            Map parameters = new HashMap();
            parameters.clear();
            parameters.put("codeTypeDish", null);
            parameters.put("Background", this.getClass().getResourceAsStream(Background));
            GenerateReport.showReport("typeDishReport.jasper", "Type Dishes Report", parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    public void typeEmployeeSearch() {

        FilteredList<TypeDish> filter = new FilteredList<>(typeDishList, e -> true);

        txtSearchTypeDish.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((TypeDish predicateTypeDish) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateTypeDish.getCodeTypeDish()).contains(searchKey)) {
                    return true;
                } else if (predicateTypeDish.getDescriptionType().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<TypeDish> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblTypeDish.comparatorProperty());
        tblTypeDish.setItems(sortList);
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
                        principalStage.typeDishWindow();
                    }
    }
       
    public void minimize() {
        Stage stage = (Stage) TypeDishPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    private enum NotificationType {
        WARNING("Warning", "org/juangalicia/image/warning.png"),
        SUCCESS("Success", "org/juangalicia/image/success.png"),
        ERROR("Error", "org/juangalicia/image/error.png");

        private final String title;
        private final String imagePath;

        NotificationType(String title, String imagePath) {
            this.title = title;
            this.imagePath = imagePath;
        }
    }

    private void notification(NotificationType type, String text, int seconds) {
        Image imgN = new Image(type.imagePath);
        Notifications notification = Notifications.create();
        notification.title(type.title);
        notification.graphic(new ImageView(imgN));
        notification.text(text);
        notification.hideAfter(Duration.seconds(seconds));
        notification.position(Pos.BASELINE_RIGHT);
        notification.show();
    }
    
    private boolean showConfirmationDialog(String title, String header, String content, String button1, String button2) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType buttonTypeYes = new ButtonType(button1);
        ButtonType buttonTypeNo = new ButtonType(button2);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }
    
    //Methods to Search existing registers on the TableView
    public boolean isDataExistsInTableView(String newData) {
        ObservableList<TypeDish> items = tblTypeDish.getItems();
        
        for (TypeDish typeDish : items) {
            if (String.valueOf(typeDish.getCodeTypeDish()).equals(newData)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTextFieldEmpty(JFXTextField textField) {
        return textField.getText().trim().isEmpty();
    }
    
    private boolean isFormValid() {
        return !isTextFieldEmpty(txtTypeDishId)
                && !isTextFieldEmpty(txtDescription);
    }

    public void lockControls() {
        txtTypeDishId.setEditable(false);
        txtDescription.setEditable(false);
    }

    public void unlockControls() {
        txtTypeDishId.setEditable(false);
        txtDescription.setEditable(true);
    }

    public void clearControls() {
        txtTypeDishId.clear();
        txtDescription.clear();
    }

    public void principalWindow(){
        this.principalStage.principalWindow();
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
    
    public void loginWindow(){
        principalStage.loginWindow();
    }
    

    public Principal getPrincipalStage() {
        return principalStage;
    }

    public void setPrincipalStage(Principal principalStage) {
        this.principalStage = principalStage;
    }

    public void menuPrincipal() {
        principalStage.principalWindow();
    } 
}
