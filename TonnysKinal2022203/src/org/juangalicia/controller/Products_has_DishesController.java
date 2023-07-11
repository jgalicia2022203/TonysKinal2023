package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.juangalicia.bean.Dish;
import org.juangalicia.bean.Product;
import org.juangalicia.bean.Products_has_Dishes;
import org.juangalicia.bean.Services_has_Dishes;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

public class Products_has_DishesController implements Initializable{
    private Principal principalStage;
    
    private enum operations {
        SAVE, DELETE, UPDATE, NONE
    }
    
    private operations typeOfOperation = operations.NONE;
    private ObservableList<Products_has_Dishes> productsHasDishesList;
    private ObservableList<Product> productList;
    private ObservableList<Dish> dishList;
    
    @FXML 
    private AnchorPane PHDPane;
    @FXML
    private JFXTextField txtSearchPHD;
    @FXML 
    private JFXButton btnCreate;
    @FXML 
    private JFXButton btnCancel;
    @FXML 
    private JFXComboBox cmbCodeDish;
    @FXML
    private JFXComboBox cmbCodeProduct;
    @FXML
    private ImageView imgCreate;
    @FXML
    private ImageView imgCancel;
    @FXML
    private TableColumn colProducts_CodeProduct;
    @FXML 
    private TableColumn colCodeDish;
    @FXML 
    private TableColumn colCodeProduct;
    @FXML
    private TableView tblProductsHasDishes;
    @FXML 
    private JFXTextField txtProducts_CodeProduct;    
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        loadData();
        cmbCodeDish.setItems(getDish());
        cmbCodeProduct.setItems(getProduct());
    }
    
    public void loadData(){
        tblProductsHasDishes.setItems(getProductsHasDishes());
        colProducts_CodeProduct.setCellValueFactory(new PropertyValueFactory<Products_has_Dishes, Integer>("Products_codeProduct"));
        colCodeDish.setCellValueFactory(new PropertyValueFactory<Products_has_Dishes, Integer>("codeDish"));
        colCodeProduct.setCellValueFactory(new PropertyValueFactory<Products_has_Dishes, Integer>("codeProduct"));
    }
   
    public void SelectElement(){
        txtProducts_CodeProduct.setText(String.valueOf(((Products_has_Dishes)tblProductsHasDishes.getSelectionModel().getSelectedItem()).getProducts_codeProduct()));
        cmbCodeDish.getSelectionModel().select(searchDish(((Products_has_Dishes)tblProductsHasDishes.getSelectionModel().getSelectedItem()).getCodeDish()));
        cmbCodeProduct.getSelectionModel().select(searchProduct(((Products_has_Dishes)tblProductsHasDishes.getSelectionModel().getSelectedItem()).getCodeProduct()));
    }
    
    public Dish searchDish(int codeDish){
        Dish result = null;
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_SearchDish(?)");
            procedure.setInt(1, codeDish);
            ResultSet register = procedure.executeQuery();
            while(register.next()){
                result = new Dish(register.getInt("codeDish"), register.getInt("quantity"), register.getString("nameDish"),
                                  register.getString("descriptionDish"), register.getDouble("priceDish"), register.getInt("codeTypeDish"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public Product searchProduct(int codeProduct){
        Product result = null;
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_SearchProduct(?)");
            procedure.setInt(1, codeProduct);
            ResultSet register = procedure.executeQuery();
            while(register.next()){
                result = new Product(register.getInt("codeProduct"), register.getString("nameProduct"), register.getInt("quantity"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public ObservableList<Products_has_Dishes> getProductsHasDishes(){
        ArrayList<Products_has_Dishes> list = new ArrayList<Products_has_Dishes>();
        try {
                PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadProduct_has_Dishes()");
                ResultSet result = procedure.executeQuery();
                while(result.next()){
                    list.add(new Products_has_Dishes(result.getInt("Products_codeProduct"), result.getInt("codeDish"), result.getInt("codeProduct")));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productsHasDishesList = FXCollections.observableArrayList(list);
    }
    
    public ObservableList<Dish> getDish(){
        ArrayList<Dish> list = new ArrayList<Dish>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadDishes()");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
                list.add(new Dish(result.getInt("codeDish"), result.getInt("quantity"), result.getString("nameDish"),
                                    result.getString("descriptionDish"), result.getDouble("priceDish"), result.getInt("codeTypeDish")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dishList = FXCollections.observableArrayList(list);
    }
    
    public ObservableList <Product> getProduct(){
        ArrayList<Product> list = new ArrayList<Product>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadProducts()");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
                list.add(new Product(result.getInt("codeProduct"), result.getString("nameProduct"), result.getInt("quantity")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productList = FXCollections.observableArrayList(list);
    }
    
    public void create(){
        switch(typeOfOperation){
            case NONE:
                clearControls();
                unlockControls();
                btnCreate.setText("Save");
                btnCancel.setText("Cancel");
                imgCreate.setImage(new Image("/org/juangalicia/image/save.png"));
                imgCancel.setImage(new Image ("org/juangalicia/image/cancel.png"));
                typeOfOperation = operations.SAVE;
                loadData();
                break;
                
            case SAVE: 
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Create Product Has Dish");
                btnCancel.setText(" ");
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgCancel.setImage(null);
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }
    
    public void cancel(){
        switch(typeOfOperation){
            case SAVE:
                clearControls();
                lockControls();
                btnCreate.setText("Create Products has Dish");
                btnCancel.setText(" ");
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgCancel.setImage(null);
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }
    
    public void save(){
        Products_has_Dishes register = new Products_has_Dishes();
        register.setProducts_codeProduct(Integer.parseInt(txtProducts_CodeProduct.getText()));
        register.setCodeDish(((Dish)cmbCodeDish.getSelectionModel().getSelectedItem()).getCodeDish());
        register.setCodeProduct(((Product)cmbCodeProduct.getSelectionModel().getSelectedItem()).getCodeProduct());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_CreateProduct_has_Dish(?,?,?)");
            procedure.setInt(1, register.getProducts_codeProduct());
            procedure.setInt(2, register.getCodeDish());
            procedure.setInt(3, register.getCodeProduct());
            procedure.execute();
            productsHasDishesList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ProductsHasDishSearch() {

        FilteredList<Products_has_Dishes> filter = new FilteredList<>(productsHasDishesList, e -> true);

        txtSearchPHD.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Products_has_Dishes predicatePHD) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicatePHD.getProducts_codeProduct()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicatePHD.getCodeDish()).toLowerCase().contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicatePHD.getCodeProduct()).toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Products_has_Dishes> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblProductsHasDishes.comparatorProperty());
        tblProductsHasDishes.setItems(sortList);
    }
    
    public void minimize() {
        Stage stage = (Stage) PHDPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    public void close() {
        System.exit(0);
    }   
    
    public void lockControls(){
        txtProducts_CodeProduct.setEditable(false);
        cmbCodeDish.setDisable(true);
        cmbCodeProduct.setDisable(true);
    }
    
    public void unlockControls(){
        txtProducts_CodeProduct.setEditable(true);
        cmbCodeDish.setDisable(false);
        cmbCodeProduct.setDisable(false);
    }
    
    public void clearControls(){
        txtProducts_CodeProduct.clear();
        cmbCodeDish.setValue(null);
        cmbCodeProduct.setValue(null);
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
