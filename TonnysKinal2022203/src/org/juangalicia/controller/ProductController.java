package org.juangalicia.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.juangalicia.bean.Product;
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

public class ProductController implements Initializable {
    private enum operations {
        CREATE, SAVE, DELETE, UPDATE, CANCEL, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private Principal principalStage;
    private ObservableList<Product> productList;

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
    private TableView tblProducts;
    @FXML
    private TableColumn colProductId;
    @FXML
    private TableColumn colProductName;
    @FXML
    private TableColumn colQuantity;
    @FXML
    private TextField txtProductId;
    @FXML
    private TextField txtProductName;
    @FXML
    private TextField txtQuantity;

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
                btnCreate.setText("Create Product");
                btnRead.setText("Read Products");
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/Add Product.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/Read Products.png"));
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
                btnCreate.setText("Create Product");
                btnRead.setText("Read Products");
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/Add Product.png"));
                imgRead.setImage(new Image("/org/juangalicia/image/Read Products.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
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
                    JOptionPane.showMessageDialog(null, "You should select an element");
                }
                break;
            case UPDATE:
                update();
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Product");
                btnDelete.setText("Delete Product");
                imgUpdate.setImage(new Image("/org/juangalicia/image/Update Product.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/Delete Product.png"));
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
                imgUpdate.setImage(new Image("/org/juangalicia/image/Update Product.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/Delete Product.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblProducts.getSelectionModel().getSelectedItem() != null) {
                    int answer = JOptionPane.showConfirmDialog(null,
                            "Are you sure of deleting this register?" + " You are gonna delete a foreign key",
                            "Delete Product", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
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
                    } else {
                        JOptionPane.showMessageDialog(null, "You should select an element");
                    }
                }
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

    public void selectElement() {
        txtProductId.setText(
                String.valueOf(((Product) tblProducts.getSelectionModel().getSelectedItem()).getCodeProduct()));
        txtProductName.setText(((Product) tblProducts.getSelectionModel().getSelectedItem()).getNameProduct());
        txtQuantity
                .setText(String.valueOf(((Product) tblProducts.getSelectionModel().getSelectedItem()).getQuantity()));
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
