package org.juangalicia.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.juangalicia.bean.Dish;
import org.juangalicia.bean.TypeDish;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DishController implements Initializable {
    private Principal principalStage;

    private enum operations {
        SAVE, DELETE, UPDATE, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private ObservableList<Dish> dishList;
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
    private ComboBox cmbTypeDish;
    @FXML
    private ImageView imgCreate;
    @FXML
    private ImageView imgRead;
    @FXML
    private ImageView imgUpdate;
    @FXML
    private ImageView imgDelete;
    @FXML
    private TableColumn colDishId;
    @FXML
    private TableColumn colQuantity;
    @FXML
    private TableColumn colName;
    @FXML
    private TableColumn colDescription;
    @FXML
    private TableColumn colPrice;
    @FXML
    private TableColumn colTypeDish;
    @FXML
    private TableView tblDishes;
    @FXML
    private TextField txtDishId;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtNameDish;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtPriceDish;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        cmbTypeDish.setItems(getTypeDish());
    }

    public void loadData() {
        tblDishes.setItems(getDish());
        colDishId.setCellValueFactory(new PropertyValueFactory<Dish, Integer>("codeDish"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<Dish, Integer>("quantity"));
        colName.setCellValueFactory(new PropertyValueFactory<Dish, String>("nameDish"));
        colDescription.setCellValueFactory(new PropertyValueFactory<Dish, String>("descriptionDish"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Dish, Double>("priceDish"));
        colTypeDish.setCellValueFactory(new PropertyValueFactory<Dish, Integer>("codeTypeDish"));
    }

    public void SelectElement() {
        txtDishId.setText(
                String.valueOf(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getCodeDish()));
        txtQuantity.setText(
                String.valueOf(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getQuantity()));
        txtNameDish.setText(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getNameDish());
        txtDescription.setText(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getDescriptionDish());
        txtPriceDish.setText(String.valueOf(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getPriceDish()));
        cmbTypeDish.getSelectionModel().select(searchTypeDish(
                ((Dish) tblDishes.getSelectionModel().getSelectedItem()).getCodeTypeDish()));
    }

    public TypeDish searchTypeDish(int codeTypeDish) {
        TypeDish result = null;
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_SearchTypeDish(?)");
            procedure.setInt(1, codeTypeDish);
            ResultSet register = procedure.executeQuery();
            while (register.next()) {
                result = new TypeDish(register.getInt("codeTypeDish"), register.getString("descriptionType"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ObservableList<Dish> getDish() {
        ArrayList<Dish> list = new ArrayList<Dish>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadDishes()");
            ResultSet result = procedure.executeQuery();
            while (result.next()) {
                list.add(new Dish(result.getInt("codeDish"),
                        result.getInt("quantity"),
                        result.getString("nameDish"),
                        result.getString("descriptionDish"),
                        result.getDouble("priceDish"),
                        result.getInt("codeTypeDish")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dishList = FXCollections.observableArrayList(list);
    }

    public ObservableList<TypeDish> getTypeDish() {
        ArrayList<TypeDish> list = new ArrayList<TypeDish>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_ReadTypeDishes()");
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
                btnCreate.setText("Create Dish");
                btnRead.setText("Read Dishes");
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/Add Dish.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/Read Dishes.png"));
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
                btnCreate.setText("Create Dish");
                btnRead.setText("Read Dishes");
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/Add Dish.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/Read Dishes.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }

    public void edit() {
        switch (typeOfOperation) {
            case NONE:
                if (tblDishes.getSelectionModel().getSelectedItem() != null) {
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
                btnUpdate.setText("Update Dish");
                btnDelete.setText("Delete Dish");
                imgUpdate.setImage(new Image("/org/juangalicia/image/Update Dish.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/Delete Dish.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;
        }
    }

    public void update() {
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_UpdateDish(?,?,?,?,?,?)");
            Dish register = (Dish) tblDishes.getSelectionModel().getSelectedItem();
            register.setQuantity(Integer.parseInt(txtQuantity.getText()));
            register.setNameDish(txtNameDish.getText());
            register.setDescriptionDish(txtDescription.getText());
            register.setPriceDish(Double.parseDouble(txtPriceDish.getText()));
            register.setCodeTypeDish(((TypeDish) cmbTypeDish.getSelectionModel().getSelectedItem()).getCodeTypeDish());
            procedure.setInt(1, register.getCodeDish());
            procedure.setInt(2, register.getQuantity());
            procedure.setString(3, register.getNameDish());
            procedure.setString(4, register.getDescriptionDish());
            procedure.setDouble(5, register.getPriceDish());
            procedure.setInt(6, register.getCodeTypeDish());
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
                btnUpdate.setText("Update Dish");
                btnDelete.setText("Delete Dish");
                imgUpdate.setImage(new Image("/org/juangalicia/image/Update Dish.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/Delete Dish.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblDishes.getSelectionModel().getSelectedItem() != null) {
                    int answer = JOptionPane.showConfirmDialog(null, "Are you sure of deleting this register",
                            "Delete Dish", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("sp_DeleteDish(?)");
                            procedure.setInt(1, ((Dish) tblDishes.getSelectionModel().getSelectedItem()).getCodeDish());
                            procedure.execute();
                            dishList.remove(tblDishes.getSelectionModel().getSelectedItem());
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
        Dish register = new Dish();
        register.setQuantity(Integer.parseInt(txtQuantity.getText()));
        register.setNameDish(txtNameDish.getText());
        register.setDescriptionDish(txtDescription.getText());
        register.setPriceDish(Double.parseDouble(txtPriceDish.getText()));
        register.setCodeTypeDish(((TypeDish) cmbTypeDish.getSelectionModel().getSelectedItem()).getCodeTypeDish());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_CreateDish(?,?,?,?,?)");
            procedure.setInt(1, register.getQuantity());
            procedure.setString(2, register.getNameDish());
            procedure.setString(3, register.getDescriptionDish());
            procedure.setDouble(4, register.getPriceDish());
            procedure.setInt(5, register.getCodeTypeDish());
            procedure.execute();
            dishList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lockControls() {
        txtDishId.setEditable(false);
        txtQuantity.setEditable(false);
        txtNameDish.setEditable(false);
        txtDescription.setEditable(false);
        txtPriceDish.setEditable(false);
        cmbTypeDish.setDisable(true);
    }

    public void unlockControls() {
        txtDishId.setEditable(false);
        txtQuantity.setEditable(true);
        txtNameDish.setEditable(true);
        txtDescription.setEditable(true);
        txtPriceDish.setEditable(true);
        cmbTypeDish.setDisable(false);
    }

    public void clearControls() {
        txtDishId.clear();
        txtQuantity.clear();
        txtNameDish.clear();
        txtDescription.clear();
        txtPriceDish.clear();
        cmbTypeDish.setValue(null);
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
