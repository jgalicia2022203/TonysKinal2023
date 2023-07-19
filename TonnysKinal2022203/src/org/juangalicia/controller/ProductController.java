package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;


import org.juangalicia.bean.Product;
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
import org.juangalicia.bean.Company;
import org.juangalicia.report.GenerateReport;

public class ProductController implements Initializable {
    private enum operations {
        CREATE, SAVE, DELETE, UPDATE, CANCEL, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private Principal principalStage;
    private ObservableList<Product> productList;
    private final String Background="/org/juangalicia/image/Report Background.png";
    
    @FXML 
    private AnchorPane productPane;
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
    private TableView tblProducts;
    @FXML
    private TableColumn colProductId;
    @FXML
    private TableColumn colProductName;
    @FXML
    private TableColumn colQuantity;
    @FXML
    private JFXTextField txtProductId;
    @FXML
    private JFXTextField txtProductName;
    @FXML
    private JFXTextField txtQuantity;
    @FXML
    private JFXTextField txtProductSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
    }

    public void loadData() {
        tblProducts.setItems(getProduct());
        colProductId.setCellValueFactory(new PropertyValueFactory<Product, Integer>("codeProduct"));
        colProductName.setCellValueFactory(new PropertyValueFactory<Product, String>("nameProduct"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));
    }

    public ObservableList<Product> getProduct() {
        ArrayList<Product> list = new ArrayList<Product>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadProducts");
            ResultSet result = procedure.executeQuery();
            while (result.next()) {
                list.add(new Product(result.getInt("codeProduct"),
                        result.getString("nameProduct"),
                        result.getInt("quantity")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productList = FXCollections.observableArrayList(list);
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
                btnCreate.setText("Create Product");
                btnUpdate.setText("Update Products");
                btnDelete.setDisable(false);
                btnRead.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }
    
        public void save() {
        Product register = new Product();
        register.setCodeProduct(Integer.parseInt(txtProductId.getText()));
        register.setNameProduct(txtProductName.getText());
        register.setQuantity(Integer.parseInt(txtQuantity.getText()));
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_CreateProduct(?,?,?)");
            procedure.setInt(1, register.getCodeProduct());
            procedure.setString(2, register.getNameProduct());
            procedure.setInt(3, register.getQuantity());
            procedure.execute();
            productList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void edit() {
        switch (typeOfOperation) {
            case NONE:
                if (tblProducts.getSelectionModel().getSelectedItem() != null) {
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
                btnCreate.setText("Create Product");
                btnUpdate.setText("Update Product");
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
                btnUpdate.setText("Update Product");
                btnDelete.setText("Delete Product");
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
                    .prepareCall("call sp_UpdateProduct(?,?,?)");
            Product register = (Product) tblProducts.getSelectionModel().getSelectedItem();
            register.setNameProduct(txtProductName.getText());
            register.setQuantity(Integer.parseInt(txtQuantity.getText()));
            procedure.setInt(1, register.getCodeProduct());
            procedure.setString(2, register.getNameProduct());
            procedure.setInt(3, register.getQuantity());
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
                btnUpdate.setText("Update Product");
                btnDelete.setText("Delete Product");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblProducts.getSelectionModel().getSelectedItem() != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Product");
                    alert.setHeaderText("Are you sure of deleting this register? You are gonna delete a foreign key");
                    alert.setContentText("Choose your option.");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteProduct(?)");
                            procedure.setInt(1,
                                    ((Product) tblProducts.getSelectionModel().getSelectedItem()).getCodeProduct());
                            procedure.execute();
                            productList.remove(tblProducts.getSelectionModel().getSelectedItem());
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

    public void selectElement() {
        txtProductId.setText(
                String.valueOf(((Product) tblProducts.getSelectionModel().getSelectedItem()).getCodeProduct()));
        txtProductName.setText(((Product) tblProducts.getSelectionModel().getSelectedItem()).getNameProduct());
        txtQuantity
                .setText(String.valueOf(((Product) tblProducts.getSelectionModel().getSelectedItem()).getQuantity()));
    }
    
    public void generateReport() {
        switch (typeOfOperation) {
            case NONE:
                printReport();
                break;
        }
    }

    public void printReport() {
        try {
            Map parameters = new HashMap();
            parameters.clear();
            parameters.put("codeCompany", null);
            parameters.put("Background", this.getClass().getResourceAsStream(Background));
            GenerateReport.showReport("ProductReport.jasper", "Companies Report", parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
        public void productSearch() {

        FilteredList<Product> filter = new FilteredList<>(productList, e -> true);

        txtProductSearch.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Product predicateProduct) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateProduct.getCodeProduct()).contains(searchKey)) {
                    return true;
                } else if (predicateProduct.getNameProduct().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateProduct.getQuantity()).toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Product> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblProducts.comparatorProperty());
        tblProducts.setItems(sortList);
    }
        
    public void minimize() {
        Stage stage = (Stage) productPane.getScene().getWindow();
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
        txtProductId.setEditable(false);
        txtProductName.setEditable(false);
        txtQuantity.setEditable(false);
    }

    public void unlockControls() {
        txtProductId.setEditable(true);
        txtProductName.setEditable(true);
        txtQuantity.setEditable(true);
    }

    public void clearControls() {
        txtProductId.clear();
        txtProductName.clear();
        txtQuantity.clear();
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
