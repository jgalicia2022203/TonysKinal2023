package org.juangalicia.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.juangalicia.bean.TypeEmployee;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TypeEmployeeController implements Initializable {
    private enum operations {
        CREATE, SAVE, DELETE, UPDATE, CANCEL, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private Principal principalStage;
    private ObservableList<TypeEmployee> typeEmployeeList;

    @FXML
    private Button btnCreate;
    @FXML
    private Button btnRead;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
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
    private TextField txtTypeEmployeeId;
    @FXML
    private TextField txtDescription;

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
                btnRead.setText("Cancel");
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
                imgCreate.setImage(new Image("/org/juangalicia/image/save.png"));
                imgRead.setImage(new Image("org/juangalicia/image/cancel.png"));
                typeOfOperation = operations.SAVE;
                loadData();
                break;
            case SAVE:
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Create Type Employee");
                btnRead.setText("Read Type Employee");
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/Add TypeEmployee.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/Read TypeEmployees.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }

    public void read() {
        switch (typeOfOperation) {
            case SAVE:
                clearControls();
                lockControls();
                btnCreate.setText("Create Type Employee");
                btnRead.setText("Read Type Employees");
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/Add TypeEmployee.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/Read TypeEmployees.png"));
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
                    JOptionPane.showMessageDialog(null, "You should select an element");
                }
                break;
            case UPDATE:
                update();
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Type Employee");
                btnDelete.setText("Delete Type Employee");
                imgUpdate.setImage(new Image("/org/juangalicia/image/Update TypeEmployee.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/Delete TypeEmployee.png"));
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
                update();
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Type Employee");
                btnDelete.setText("Delete Type Employee");
                imgUpdate.setImage(new Image("/org/juangalicia/image/Update TypeEmployee.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/Delete TypeEmployee.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblTypeEmployees.getSelectionModel().getSelectedItem() != null) {
                    int answer = JOptionPane.showConfirmDialog(null,
                            "Are you sure of deleting this register?" + " You are gonna delete a foreign key",
                            "Delete Type Employee", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteTypeEmployee(?)");
                            procedure.setInt(1, ((TypeEmployee) tblTypeEmployees.getSelectionModel().getSelectedItem())
                                    .getCodeTypeEmployee());
                            procedure.execute();
                            typeEmployeeList.remove(tblTypeEmployees.getSelectionModel().getSelectedItem());
                            clearControls();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showConfirmDialog(null, "You should select an element");
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
