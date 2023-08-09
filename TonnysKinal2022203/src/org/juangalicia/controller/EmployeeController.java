package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import org.juangalicia.bean.Employee;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class EmployeeController implements Initializable {
    
    private Principal principalStage;
    private ObservableList<Employee> employeeList;
    private ObservableList<TypeEmployee> typeEmployeeList;
    private final String Background="/org/juangalicia/image/Report Background.png";

    @FXML
    private AnchorPane employeePane;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private ComboBox cmbCodeTypeEmployee;
    @FXML
    private TableColumn colAdress;
    @FXML
    private TableColumn colCodeTypeEmployee;
    @FXML
    private TableColumn colCookDegree;
    @FXML
    private TableColumn colEmployeeId;
    @FXML
    private TableColumn colFirstName;
    @FXML
    private TableColumn colNumber;
    @FXML
    private TableColumn colPhone;
    @FXML
    private TableColumn colSecondName;
    @FXML
    private TableView tblEmployees;
    @FXML
    private JFXTextField txtAdress;
    @FXML
    private JFXTextField txtContactPhone;
    @FXML
    private JFXTextField txtCookDegree;
    @FXML
    private JFXTextField txtEmployeeId;
    @FXML
    private JFXTextField txtEmployeeNumber;
    @FXML
    private JFXTextField txtFirstName;
    @FXML
    private JFXTextField txtSecondName;
    @FXML
    private JFXTextField txtEmployeeSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        unlockControls();
        cmbCodeTypeEmployee.setItems(getTypeEmployee());
    }
   
    public void loadData() {
        tblEmployees.setItems(getEmployee());
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("codeEmployee"));
        colNumber.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("numberEmployee"));
        colSecondName.setCellValueFactory(new PropertyValueFactory<Employee, String>("secondNameEmployee"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<Employee, String>("firstNameEmployee"));
        colAdress.setCellValueFactory(new PropertyValueFactory<Employee, String>("adressEmployee"));
        colPhone.setCellValueFactory(new PropertyValueFactory<Employee, String>("contactPhone"));
        colCookDegree.setCellValueFactory(new PropertyValueFactory<Employee, String>("cookDegree"));
        colCodeTypeEmployee.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("codeTypeEmployee"));
    }

    public void SelectElement() {
        txtEmployeeId.setText(
                String.valueOf(((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getCodeEmployee()));
        txtEmployeeNumber.setText(
                String.valueOf(((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getNumberEmployee()));
        txtSecondName.setText(((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getSecondNameEmployee());
        txtFirstName.setText(((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getFirstNameEmployee());
        txtAdress.setText(((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getAdressEmployee());
        txtContactPhone.setText(((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getContactPhone());
        txtCookDegree.setText(((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getCookDegree());
        cmbCodeTypeEmployee.getSelectionModel().select(searchTypeEmployee(
                ((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getCodeTypeEmployee()));
    }

    public TypeEmployee searchTypeEmployee(int codeTypeEmployee) {
        TypeEmployee result = null;
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_SearchTypeEmployee(?)");
            procedure.setInt(1, codeTypeEmployee);
            ResultSet register = procedure.executeQuery();
            while (register.next()) {
                result = new TypeEmployee(register.getInt("codeTypeEmployee"), register.getString("descript"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ObservableList<Employee> getEmployee() {
        ArrayList<Employee> list = new ArrayList<Employee>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadEmployees()");
            ResultSet result = procedure.executeQuery();
            while (result.next()) {
                list.add(new Employee(result.getInt("codeEmployee"),
                        result.getInt("numberEmployee"),
                        result.getString("secondNameEmployee"),
                        result.getString("firstNameEmployee"),
                        result.getString("adressEmployee"),
                        result.getString("contactPhone"),
                        result.getString("cookDegree"),
                        result.getInt("codeTypeEmployee")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employeeList = FXCollections.observableArrayList(list);
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
                if (isDataExistsInTableView(txtEmployeeId.getText())) {
                    clearControls();
                } else {
                    if (showConfirmationDialog("Add Employee", "You want to add this employee?", 
                            "Choose your option.", "Save", "Cancel")){
                        try {
                            save();
                            loadData();
                            clearControls();
                            notification(NotificationType.SUCCESS, "Employee added successfully", 5);
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
        Employee register = new Employee();
        register.setNumberEmployee(Integer.parseInt(txtEmployeeNumber.getText()));
        register.setSecondNameEmployee(txtSecondName.getText());
        register.setFirstNameEmployee(txtFirstName.getText());
        register.setAdressEmployee(txtAdress.getText());
        register.setContactPhone(txtContactPhone.getText());
        register.setCookDegree(txtCookDegree.getText());
        register.setCodeTypeEmployee(
                ((TypeEmployee) cmbCodeTypeEmployee.getSelectionModel().getSelectedItem()).getCodeTypeEmployee());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_CreateEmployee(?,?,?,?,?,?,?)");
            procedure.setInt(1, register.getNumberEmployee());
            procedure.setString(2, register.getSecondNameEmployee());
            procedure.setString(3, register.getFirstNameEmployee());
            procedure.setString(4, register.getAdressEmployee());
            procedure.setString(5, register.getContactPhone());
            procedure.setString(6, register.getCookDegree());
            procedure.setInt(7, register.getCodeTypeEmployee());
            procedure.execute();
            employeeList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    public void edit() {
        if (tblEmployees.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Update Employee", "You want to modify this Employee?", 
                            "Choose your option.", "Update", "Cancel")) {
                            try {
                                update();
                                loadData();
                                clearControls();
                                notification(NotificationType.SUCCESS, "Employee successfully updated from database", 5);
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
                    .prepareCall("call sp_UpdateEmployee(?,?,?,?,?,?,?,?)");
            Employee register = (Employee) tblEmployees.getSelectionModel().getSelectedItem();
            register.setNumberEmployee(Integer.parseInt(txtEmployeeNumber.getText()));
            register.setSecondNameEmployee(txtSecondName.getText());
            register.setFirstNameEmployee(txtFirstName.getText());
            register.setAdressEmployee(txtAdress.getText());
            register.setContactPhone(txtContactPhone.getText());
            register.setCookDegree(txtCookDegree.getText());
            register.setCodeTypeEmployee(
                    ((TypeEmployee) cmbCodeTypeEmployee.getSelectionModel().getSelectedItem()).getCodeTypeEmployee());
            procedure.setInt(1, register.getCodeEmployee());
            procedure.setInt(2, register.getNumberEmployee());
            procedure.setString(3, register.getSecondNameEmployee());
            procedure.setString(4, register.getFirstNameEmployee());
            procedure.setString(5, register.getAdressEmployee());
            procedure.setString(6, register.getContactPhone());
            procedure.setString(7, register.getCookDegree());
            procedure.setInt(8, register.getCodeTypeEmployee());
            procedure.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
       if (tblEmployees.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Delete Employee", "Are you sure of deleting this register?", 
                            "Choose your option.", "Delete", "Cancel")){
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_DeleteEmployee(?)");
                            procedure.setInt(1,((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getCodeEmployee());
                            procedure.execute();
                            employeeList.remove(tblEmployees.getSelectionModel().getSelectedItem());
                            clearControls();
                            notification(NotificationType.SUCCESS, "Employee successfully deleted from database", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    notification(NotificationType.ERROR, "Please select an element from the table", 5);
                }
    }
    
    public void generateReport() {
        printReport();
    }

    public void printReport() {
        try {
            Map parameters = new HashMap();
            parameters.clear();
            parameters.put("codeCompany", null);
            parameters.put("Background", this.getClass().getResourceAsStream(Background));
            GenerateReport.showReport("EmployeeReport.jasper", "Companies Report", parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void employeeSearch() {

        FilteredList<Employee> filter = new FilteredList<>(employeeList, e -> true);

        txtEmployeeSearch.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Employee predicateEmployee) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateEmployee.getCodeEmployee()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateEmployee.getNumberEmployee()).contains(searchKey)) {
                    return true;
                } else if (predicateEmployee.getSecondNameEmployee().toLowerCase().contains(searchKey)) {
                    return true;
                } else if(predicateEmployee.getFirstNameEmployee().toLowerCase().contains(searchKey)){
                    return true;
                } else if(predicateEmployee.getAdressEmployee().toLowerCase().contains(searchKey)){
                    return true;
                } else if(predicateEmployee.getContactPhone().toLowerCase().contains(searchKey)){
                    return true;
                } else if(predicateEmployee.getCookDegree().toLowerCase().contains(searchKey)){
                    return true;
                } else if(String.valueOf(predicateEmployee.getCodeTypeEmployee()).contains(searchKey)){
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Employee> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblEmployees.comparatorProperty());
        tblEmployees.setItems(sortList);
    }
   
    //Menubar buttons methods
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
                        principalStage.employeeWindow();
                    }
    }
    
    public void minimize() {
        Stage stage = (Stage) employeePane.getScene().getWindow();
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
        ObservableList<Employee> items = tblEmployees.getItems();
        
        for (Employee employee : items) {
            if (String.valueOf(employee.getCodeEmployee()).equals(newData)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTextFieldEmpty(JFXTextField textField) {
        return textField.getText().trim().isEmpty();
    }
    
    private <T> boolean isComboBoxEmpty(ComboBox<T> comboBox) {
        return comboBox.getValue() == null;
    }

    
    private boolean isFormValid() {
        return !isTextFieldEmpty(txtEmployeeNumber)
                && !isTextFieldEmpty(txtFirstName)
                && !isTextFieldEmpty(txtSecondName)
                && !isTextFieldEmpty(txtAdress)
                && !isTextFieldEmpty(txtContactPhone)
                && !isTextFieldEmpty(txtCookDegree)
                && !isComboBoxEmpty(cmbCodeTypeEmployee);
    }

    //Controls methods
    public void lockControls() {
        txtEmployeeId.setEditable(false);
        txtEmployeeNumber.setEditable(false);
        txtFirstName.setEditable(false);
        txtSecondName.setEditable(false);
        txtAdress.setEditable(false);
        txtContactPhone.setEditable(false);
        txtCookDegree.setEditable(false);
        cmbCodeTypeEmployee.setDisable(true);
    }

    public void unlockControls() {
        txtEmployeeId.setEditable(false);
        txtEmployeeNumber.setEditable(true);
        txtFirstName.setEditable(true);
        txtSecondName.setEditable(true);
        txtAdress.setEditable(true);
        txtContactPhone.setEditable(true);
        txtCookDegree.setEditable(true);
        cmbCodeTypeEmployee.setDisable(false);
    }

    public void clearControls() {
        txtEmployeeId.clear();
        txtEmployeeNumber.clear();
        txtFirstName.clear();
        txtSecondName.clear();
        txtAdress.clear();
        txtContactPhone.clear();
        txtCookDegree.clear();
        cmbCodeTypeEmployee.setValue(null);
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
