package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import org.juangalicia.bean.Product;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.juangalicia.report.GenerateReport;

public class ProductController implements Initializable {
    private Principal principalStage;
    private ObservableList<Product> productList;
    private final String Background="/org/juangalicia/image/Report Background.png";
    
    @FXML 
    private AnchorPane productPane;
    @FXML
    private JFXButton btnCreate;
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
    
    public void selectElement() {
        txtProductId.setText(String.valueOf(((Product) tblProducts.getSelectionModel().getSelectedItem()).getCodeProduct()));
        txtProductName.setText(((Product) tblProducts.getSelectionModel().getSelectedItem()).getNameProduct());
        txtQuantity.setText(String.valueOf(((Product) tblProducts.getSelectionModel().getSelectedItem()).getQuantity()));
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
       btnCreate.setOnAction(event -> {
            if (isFormValid()) {
                if (isDataExistsInTableView(txtProductId.getText())) {
                    clearControls();
                } else {
                    if (showConfirmationDialog("Save Product", "You want to add this product?",
                            "Choose your option.", "Save", "Cancel")){
                        try {
                            save();
                            loadData();
                            clearControls();
                            notification(NotificationType.SUCCESS, "Product added successfully", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        loadData();
                    }
                }
            } else {
                notification(NotificationType.ERROR, "Please complete all the fields", 5);
            }
        });
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
        if (tblProducts.getSelectionModel().getSelectedItem() != null) {
                if (showConfirmationDialog("Update Product", "You want to modify this company?", 
                        "Choose your option.", "Update", "Cancel")) {
                            try {
                                update();
                                loadData();
                                clearControls();
                                notification(NotificationType.SUCCESS, "Product successfully updated from database", 5);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            loadData();
                        }      
                }
                else {
                    notification(NotificationType.ERROR, "Please select an element from the table", 5);
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
        if (tblProducts.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Delete Product", "Are you sure of deleting this register?", 
                            "Choose your option.", "Delete", "Cancel")){
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteProduct(?)");
                            procedure.setInt(1,((Product) tblProducts.getSelectionModel().getSelectedItem()).getCodeProduct());
                            procedure.execute();
                            productList.remove(tblProducts.getSelectionModel().getSelectedItem());
                            clearControls();
                            notification(NotificationType.SUCCESS, "Product successfully deleted from database", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    notification(NotificationType.ERROR, "Please select an element from the table", 5);
                }
        
    }
    
    public void generateReport() {
        printReport();
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
    
    public void logout() {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle("Logout");
       alert.setHeaderText("Are you sure you want to logout?");
       alert.setContentText("Choose your option.");
       ButtonType buttonTypeYes = new ButtonType("Yes");
       ButtonType buttonTypeNo = new ButtonType("No");
       alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
       
        Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            principalStage.loginWindow();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        principalStage.productWindow();
                    }
    }
    
    public void minimize() {
        Stage stage = (Stage) productPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    private enum NotificationType {
        WARNING("Warning", "org/juangalicia/image/warning.png"),
        SUCCESS("Success", "org/juangalicia/image/success.png"),
        ERROR("Error", "org/juangalicia/image/error.png");

        private final String title;
        private final String imagePath;

        NotificationType(String title, String imagePath) {
            this.title = title;
            this.imagePath = imagePath;
        }
    }

    private void notification(NotificationType type, String text, int seconds) {
        Image imgN = new Image(type.imagePath);
        Notifications notification = Notifications.create();
        notification.title(type.title);
        notification.graphic(new ImageView(imgN));
        notification.text(text);
        notification.hideAfter(Duration.seconds(seconds));
        notification.position(Pos.BASELINE_RIGHT);
        notification.show();
    }
    
    private boolean showConfirmationDialog(String title, String header, String content, String button1, String button2) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType buttonTypeYes = new ButtonType(button1);
        ButtonType buttonTypeNo = new ButtonType(button2);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }
    
    //Methods to Search existing registers on the TableView
    public boolean isDataExistsInTableView(String newData) {
        ObservableList<Product> items = tblProducts.getItems();
        
        for (Product product : items) {
            if (String.valueOf(product.getCodeProduct()).equals(newData)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTextFieldEmpty(JFXTextField textField) {
        return textField.getText().trim().isEmpty();
    }
    
    private boolean isFormValid() {
        return !isTextFieldEmpty(txtProductId)
                && !isTextFieldEmpty(txtProductName)
                && !isTextFieldEmpty(txtQuantity);
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

    public void principalWindow(){
        this.principalStage.principalWindow();
    }

    public void programmerWindow() {
        principalStage.programmerWindow();
    }

    public void companyWindow() {
        principalStage.companyWindow();
    }

    public void typeEmployeeWindow() {
        principalStage.typeEmployeeWindow();
    }

    public void productWindow() {
        principalStage.productWindow();
    }

    public void employeeWindow() {
        principalStage.employeeWindow();
    }

    public void typeDishWindow() {
        principalStage.typeDishWindow();
    }

    public void budgetWindow() {
        principalStage.budgetWindow();
    }
    
    public void dishWindow(){
        principalStage.dishWindow();
    }
    
    public void serviceWindow(){
        principalStage.serviceWindow();
    }
    
    public void userWindow(){
        principalStage.userWindow();
    }
    
    public void loginWindow(){
        principalStage.loginWindow();
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
