package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import org.juangalicia.bean.TypeEmployee;
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

public class TypeEmployeeController implements Initializable {
    private Principal principalStage;
    private ObservableList<TypeEmployee> typeEmployeeList;
    private final String Background="/org/juangalicia/image/Report Background.png";   

    @FXML
    private AnchorPane TypeEmployeePane;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private TableView tblTypeEmployees;
    @FXML
    private TableColumn colTypeEmployeeId;
    @FXML
    private TableColumn colDescription;
    @FXML
    private JFXTextField txtTypeEmployeeId;
    @FXML
    private JFXTextField txtDescription;
    @FXML
    private JFXTextField txtSearchTypeEmployee;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
    }

    public void loadData() {
        tblTypeEmployees.setItems(getTypeEmployee());
        colTypeEmployeeId.setCellValueFactory(new PropertyValueFactory<TypeEmployee, Integer>("codeTypeEmployee"));
        colDescription.setCellValueFactory(new PropertyValueFactory<TypeEmployee, String>("descript"));
    }
    
    public void selectElement() {
        txtTypeEmployeeId.setText(String.valueOf(
                ((TypeEmployee) tblTypeEmployees.getSelectionModel().getSelectedItem()).getCodeTypeEmployee()));
        txtDescription.setText(((TypeEmployee) tblTypeEmployees.getSelectionModel().getSelectedItem()).getDescript());
    }    

    public ObservableList<TypeEmployee> getTypeEmployee() {
        ArrayList<TypeEmployee> list = new ArrayList<TypeEmployee>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadTypeEmployees");
            ResultSet result = procedure.executeQuery();
            while (result.next()) {
                list.add(new TypeEmployee(result.getInt("codeTypeEmployee"),
                        result.getString("descript")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return typeEmployeeList = FXCollections.observableArrayList(list);
    }

    public void create() {
        btnCreate.setOnAction(event -> {
            if (isFormValid()) {
                if (isDataExistsInTableView(txtTypeEmployeeId.getText())) {
                    clearControls();
                } else {
                    if (showConfirmationDialog("Add Type Employee", "You want to add this Type Employee?", 
                            "Choose your option.", "Save", "Cancel")){
                        try {
                            save();
                            loadData();
                            clearControls();
                            notification(NotificationType.SUCCESS, "Type Employee added successfully", 5);
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
        TypeEmployee register = new TypeEmployee();
        // register.setCodeTypeEmployee(Integer.parseInt(txtTypeEmployeeId.getText()));
        register.setDescript(txtDescription.getText());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_CreateTypeEmployee(?)");
            procedure.setString(1, register.getDescript());
            procedure.execute();
            typeEmployeeList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void edit() {
        if (tblTypeEmployees.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Update Type Employee", "You want to modify this Type Employee?", 
                            "Choose your option.", "Update", "Cancel")) {
                            try {
                                update();
                                loadData();
                                clearControls();
                                notification(NotificationType.SUCCESS, "Type Employee successfully updated from database", 5);
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
                    .prepareCall("call sp_UpdateTypeEmployee(?,?)");
            TypeEmployee register = (TypeEmployee) tblTypeEmployees.getSelectionModel().getSelectedItem();
            register.setDescript(txtDescription.getText());
            procedure.setInt(1, register.getCodeTypeEmployee());
            procedure.setString(2, register.getDescript());
            procedure.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        if (tblTypeEmployees.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Delete Type Employee", "Are you sure of deleting this register?", 
                            "Choose your option.", "Delete", "Cancel")){
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_DeleteTypeEmployee(?)");
                            procedure.setInt(1,((TypeEmployee) tblTypeEmployees.getSelectionModel().getSelectedItem()).getCodeTypeEmployee());
                            procedure.execute();
                            typeEmployeeList.remove(tblTypeEmployees.getSelectionModel().getSelectedItem());
                            clearControls();
                            notification(NotificationType.SUCCESS, "Type Employee successfully deleted from database", 5);
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
            parameters.put("codeTypeEmployee", null);
            parameters.put("Background", this.getClass().getResourceAsStream(Background));
            GenerateReport.showReport("typeEmployeeReport.jasper", "Type Employees Report", parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void typeEmployeeSearch() {

        FilteredList<TypeEmployee> filter = new FilteredList<>(typeEmployeeList, e -> true);

        txtSearchTypeEmployee.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((TypeEmployee predicateTypeEmployee) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateTypeEmployee.getCodeTypeEmployee()).contains(searchKey)) {
                    return true;
                } else if (predicateTypeEmployee.getDescript().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<TypeEmployee> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblTypeEmployees.comparatorProperty());
        tblTypeEmployees.setItems(sortList);
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
                        principalStage.typeEmployeeWindow();
                    }
    }
    
    public void minimize() {
        Stage stage = (Stage) TypeEmployeePane.getScene().getWindow();
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
        ObservableList<TypeEmployee> items = tblTypeEmployees.getItems();
        
        for (TypeEmployee typeEmployee : items) {
            if (String.valueOf(typeEmployee.getCodeTypeEmployee()).equals(newData)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTextFieldEmpty(JFXTextField textField) {
        return textField.getText().trim().isEmpty();
    }
    
    private boolean isFormValid() {
        return !isTextFieldEmpty(txtTypeEmployeeId)
                && !isTextFieldEmpty(txtDescription);
    }
    
    public void lockControls() {
        txtTypeEmployeeId.setEditable(false);
        txtDescription.setEditable(false);
    }

    public void unlockControls() {
        txtTypeEmployeeId.setEditable(false);
        txtDescription.setEditable(true);
    }

    public void clearControls() {
        txtTypeEmployeeId.clear();
        txtDescription.clear();
    }

    //Stages
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
