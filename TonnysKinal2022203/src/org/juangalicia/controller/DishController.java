package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;


import org.juangalicia.bean.Dish;
import org.juangalicia.bean.TypeDish;
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
import org.juangalicia.report.GenerateReport;

public class DishController implements Initializable {
    private Principal principalStage;

    private enum operations {
        SAVE, DELETE, UPDATE, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private ObservableList<Dish> dishList;
    private ObservableList<TypeDish> typeDishList;
    private final String Background="/org/juangalicia/image/Report Background.png";   

    @FXML
    private AnchorPane dishPane;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private JFXButton btnRead;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXComboBox cmbTypeDish;
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
    private JFXTextField txtDishId;
    @FXML
    private JFXTextField txtQuantity;
    @FXML
    private JFXTextField txtNameDish;
    @FXML
    private JFXTextField txtDescription;
    @FXML
    private JFXTextField txtPriceDish;
    @FXML 
    private JFXTextField txtDishSearch;

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
                btnCreate.setText("Create Dish");
                btnUpdate.setText("Update Dish");
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
                    showAlert(Alert.AlertType.ERROR, "No element selected", null, "Please select an element");
                }
                break;
            
            case SAVE:
                clearControls();
                lockControls();
                btnCreate.setText("Create Dish");
                btnUpdate.setText("Update Dish");
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
                btnUpdate.setText("Update Dish");
                btnDelete.setText("Delete Dish");
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
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Dish");
                btnDelete.setText("Delete Dish");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblDishes.getSelectionModel().getSelectedItem() != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Dish");
                    alert.setHeaderText("Are you sure of deleting this register? You are gonna delete a foreign key");
                    alert.setContentText("Choose your option.");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteDish(?)");
                            procedure.setInt(1,
                                    ((Dish) tblDishes.getSelectionModel().getSelectedItem()).getCodeDish());
                            procedure.execute();
                            dishList.remove(tblDishes.getSelectionModel().getSelectedItem());
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

        public void dishSearch() {

        FilteredList<Dish> filter = new FilteredList<>(dishList, e -> true);

        txtDishSearch.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Dish predicateDish) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateDish.getCodeDish()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateDish.getQuantity()).contains(searchKey)) {
                    return true;
                } else if (predicateDish.getNameDish().toLowerCase().contains(searchKey)) {
                    return true;
                } else if(predicateDish.getDescriptionDish().toLowerCase().contains(searchKey)){
                    return true;
                } else if(String.valueOf(predicateDish.getPriceDish()).toLowerCase().contains(searchKey)){
                    return true;
                } else if(String.valueOf(predicateDish.getCodeTypeDish()).toLowerCase().contains(searchKey)){
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Dish> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblDishes.comparatorProperty());
        tblDishes.setItems(sortList);
    }
        
    public void printReport() {
        try {
            Map parameters = new HashMap();
            parameters.clear();
            parameters.put("codeDish", null);
            parameters.put("Background", this.getClass().getResourceAsStream(Background));
            GenerateReport.showReport("dishReport.jasper", "Dishes Report", parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void minimize() {
        Stage stage = (Stage) dishPane.getScene().getWindow();
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
