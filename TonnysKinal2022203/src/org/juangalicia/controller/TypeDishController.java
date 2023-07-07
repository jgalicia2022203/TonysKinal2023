package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

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

public class TypeDishController implements Initializable {
    private enum operations {
        CREATE, SAVE, DELETE, UPDATE, CANCEL, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private Principal principalStage;
    private ObservableList<TypeDish> typeDishList;

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
                btnCreate.setText("Create Type Dish");
                btnUpdate.setText("Update Type Dish");
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
                    showAlert(Alert.AlertType.ERROR, "No element selected", null, "Please select an element");
                }
                break;
            case SAVE:
                clearControls();
                lockControls();
                btnCreate.setText("Create Type Dish");
                btnUpdate.setText("Update Type Dish");
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
                btnUpdate.setText("Update Type Dish");
                btnDelete.setText("Delete Type Dish");
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
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblTypeDish.getSelectionModel().getSelectedItem() != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Type Dish");
                    alert.setHeaderText("Are you sure of deleting this register? You are gonna delete a foreign key");
                    alert.setContentText("Choose your option.");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
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
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("You should select an element");
                    alert.showAndWait();
                }
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
       
    public void minimize() {
        Stage stage = (Stage) TypeDishPane.getScene().getWindow();
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
