package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

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

public class TypeEmployeeController implements Initializable {
    private enum operations {
        CREATE, SAVE, DELETE, UPDATE, CANCEL, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private Principal principalStage;
    private ObservableList<TypeEmployee> typeEmployeeList;

    @FXML
    private AnchorPane TypeEmployeePane;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private JFXButton btnRead;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private ImageView imgCreate;
    @FXML
    private ImageView imgRead;
    @FXML
    private ImageView imgUpdate;
    @FXML
    private ImageView imgDelete;
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
                imgUpdate.setImage(new Image("org/juangalicia/image/cancel.png"));
                typeOfOperation = operations.SAVE;
                loadData();
                break;
            case SAVE:
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Create Type Employee");
                btnUpdate.setText("Update Type Employee");
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
                if (tblTypeEmployees.getSelectionModel().getSelectedItem() != null) {
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
                btnCreate.setText("Create Type Employee");
                btnUpdate.setText("Update Type Employee");
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
                btnUpdate.setText("Update Type Employee");
                btnDelete.setText("Delete Type Employee");
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
        switch (typeOfOperation) {
            case UPDATE:
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Type Employee");
                btnDelete.setText("Delete Type Employee");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblTypeEmployees.getSelectionModel().getSelectedItem() != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Type Employee");
                    alert.setHeaderText("Are you sure of deleting this register? You are gonna delete a foreign key");
                    alert.setContentText("Choose your option.");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteTypeEmployee(?)");
                            procedure.setInt(1,
                                    ((TypeEmployee) tblTypeEmployees.getSelectionModel().getSelectedItem()).getCodeTypeEmployee());
                            procedure.execute();
                            typeEmployeeList.remove(tblTypeEmployees.getSelectionModel().getSelectedItem());
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

    public void selectElement() {
        txtTypeEmployeeId.setText(String.valueOf(
                ((TypeEmployee) tblTypeEmployees.getSelectionModel().getSelectedItem()).getCodeTypeEmployee()));
        txtDescription.setText(((TypeEmployee) tblTypeEmployees.getSelectionModel().getSelectedItem()).getDescript());
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
       
    public void minimize() {
        Stage stage = (Stage) TypeEmployeePane.getScene().getWindow();
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
