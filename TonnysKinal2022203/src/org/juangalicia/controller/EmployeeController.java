package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import org.juangalicia.bean.Employee;
import org.juangalicia.bean.TypeEmployee;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EmployeeController implements Initializable {
    private Principal principalStage;

    private enum operations {
        SAVE, DELETE, UPDATE, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private ObservableList<Employee> employeeList;
    private ObservableList<TypeEmployee> typeEmployeeList;
    
    
    @FXML
    private AnchorPane employeePane;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private JFXButton btnRead;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXComboBox cmbCodeTypeEmployee;
    @FXML
    private ImageView imgCreate;
    @FXML
    private ImageView imgRead;
    @FXML
    private ImageView imgUpdate;
    @FXML
    private ImageView imgDelete;
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
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_SearchTypeEmployee(?)");
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
        switch (typeOfOperation) {
            case NONE:
                clearControls();
                unlockControls();
                btnCreate.setText("Save");
                btnUpdate.setText("Cancel");
                btnDelete.setDisable(true);
                btnRead.setDisable(true);
                imgCreate.setImage(new Image("/org/juangalicia/image/save.png"));
                imgUpdate.setImage(new Image("/org/juangalicia/image/cancel.png"));
                typeOfOperation = operations.SAVE;
                loadData();
                break;

            case SAVE:
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Create Employee");
                btnUpdate.setText("Update Employee");
                btnDelete.setDisable(false);
                btnRead.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }

    public void edit() {
        switch (typeOfOperation) {
            case NONE:
                if (tblEmployees.getSelectionModel().getSelectedItem() != null) {
                    btnCreate.setDisable(true);
                    btnRead.setDisable(true);
                    btnUpdate.setText("Update");
                    btnDelete.setText("Cancel");
                    imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                    imgDelete.setImage(new Image("/org/juangalicia/image/cancel.png"));
                    unlockControls();
                    typeOfOperation = operations.UPDATE;
                } else {
                    showAlert(Alert.AlertType.ERROR, "No element selected", null, "Please select an element");
                }
                break;
            
             case SAVE:
                clearControls();
                lockControls();
                btnCreate.setText("Create Employee");
                btnUpdate.setText("Update Employee");
                btnDelete.setDisable(false);
                btnRead.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgUpdate.setImage(new Image("org/juangalicia/image/update.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
            
            case UPDATE:
                update();
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Employee");
                btnDelete.setText("Delete Employee");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;
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
        switch (typeOfOperation) {
            case UPDATE:
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Employee");
                btnDelete.setText("Delete Employee");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblEmployees.getSelectionModel().getSelectedItem() != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Employee");
                    alert.setHeaderText("Are you sure of deleting this register? You are gonna delete a foreign key");
                    alert.setContentText("Choose your option.");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteEmployee(?)");
                            procedure.setInt(1,
                                    ((Employee) tblEmployees.getSelectionModel().getSelectedItem()).getCodeEmployee());
                            procedure.execute();
                            employeeList.remove(tblEmployees.getSelectionModel().getSelectedItem());
                            clearControls();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("You should select an element");
                    alert.showAndWait();
                }
        }
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
    
    public void productSearch() {

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

    public void minimize() {
        Stage stage = (Stage) employeePane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    public void close() {
        System.exit(0);
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }    
    public void lockControls() {
        txtEmployeeId.setEditable(false);
        txtEmployeeNumber.setEditable(false);
        txtSecondName.setEditable(false);
        txtFirstName.setEditable(false);
        txtAdress.setEditable(false);
        txtContactPhone.setEditable(false);
        txtCookDegree.setEditable(false);
        cmbCodeTypeEmployee.setDisable(true);
    }

    public void unlockControls() {
        txtEmployeeId.setEditable(false);
        txtEmployeeNumber.setEditable(true);
        txtSecondName.setEditable(true);
        txtFirstName.setEditable(true);
        txtAdress.setEditable(true);
        txtContactPhone.setEditable(true);
        txtCookDegree.setEditable(true);
        cmbCodeTypeEmployee.setDisable(false);
    }

    public void clearControls() {
        txtEmployeeId.clear();
        txtEmployeeNumber.clear();
        txtSecondName.clear();
        txtFirstName.clear();
        txtAdress.clear();
        txtContactPhone.clear();
        txtCookDegree.clear();
        cmbCodeTypeEmployee.setValue(null);
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
