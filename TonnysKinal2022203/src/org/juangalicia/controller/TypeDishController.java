package org.juangalicia.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.juangalicia.bean.TypeDish;
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

public class TypeDishController implements Initializable {
    private enum operations {
        CREATE, SAVE, DELETE, UPDATE, CANCEL, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private Principal principalStage;
    private ObservableList<TypeDish> typeDishList;

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
    private TableView tblTypeDish;
    @FXML
    private TableColumn colTypeDishId;
    @FXML
    private TableColumn colDescription;
    @FXML
    private TextField txtTypeDishId;
    @FXML
    private TextField txtDescription;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
    }

    public void loadData() {
        tblTypeDish.setItems(getTypeDish());
        colTypeDishId.setCellValueFactory(new PropertyValueFactory<TypeDish, Integer>("codeTypeDish"));
        colDescription.setCellValueFactory(new PropertyValueFactory<TypeDish, String>("descriptionType"));
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
        switch (typeOfOperation) {
            case NONE:
                clearControls();
                unlockControls();
                btnCreate.setText("Save");
                btnRead.setText("Cancel");
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
                imgCreate.setImage(new Image("/org/juangalicia/image/save.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/cancel.png"));
                typeOfOperation = operations.SAVE;
                loadData();
                break;
            case SAVE:
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Create Type Dish");
                btnRead.setText("Read Type Dishes");
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/Add TypeDish.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/Read TypeDishes.png"));
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
                btnCreate.setText("Create Type Dish");
                btnRead.setText("Read Type Dish");
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/Add TypeDish.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/Read TypeDishes.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }

    public void edit() {
        switch (typeOfOperation) {
            case NONE:
                if (tblTypeDish.getSelectionModel().getSelectedItem() != null) {
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
                btnUpdate.setText("Update Type Dish");
                btnDelete.setText("Delete Type Dish");
                imgUpdate.setImage(new Image("/org/juangalicia/image/Update TypeDish.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/Delete TypeDish.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;
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
        switch (typeOfOperation) {
            case UPDATE:
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Type Dish");
                btnDelete.setText("Delete Type Dish");
                imgUpdate.setImage(new Image("/org/juangalicia/image/Update TypeDish.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/Delete TypeDish.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblTypeDish.getSelectionModel().getSelectedItem() != null) {
                    int answer = JOptionPane.showConfirmDialog(null,
                            "Are you sure of deleting this register?" + " You are gonna delete a foreign key",
                            "Delete Type Dish", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteTypeDish(?)");
                            procedure.setInt(1,
                                    ((TypeDish) tblTypeDish.getSelectionModel().getSelectedItem()).getCodeTypeDish());
                            procedure.execute();
                            typeDishList.remove(tblTypeDish.getSelectionModel().getSelectedItem());
                            clearControls();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You should select an element");
                }
        }
    }

    public void save() {
        TypeDish register = new TypeDish();
        // register.setCodeTypeDish(Integer.parseInt(txtTypeDishId.getText()));
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

    public void selectElement() {
        txtTypeDishId.setText(
                String.valueOf(((TypeDish) tblTypeDish.getSelectionModel().getSelectedItem()).getCodeTypeDish()));
        txtDescription.setText(((TypeDish) tblTypeDish.getSelectionModel().getSelectedItem()).getDescriptionType());
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
