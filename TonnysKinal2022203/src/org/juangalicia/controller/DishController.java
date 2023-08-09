package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import org.juangalicia.bean.Dish;
import org.juangalicia.bean.TypeDish;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;
import org.juangalicia.report.GenerateReport;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class DishController implements Initializable {
    private Principal principalStage;
    private ObservableList<Dish> dishList;
    private ObservableList<TypeDish> typeDishList;
    private final String Background="/org/juangalicia/image/Report Background.png";   

    @FXML
    private AnchorPane dishPane;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private ComboBox cmbTypeDish;
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
        txtDishId.setText(String.valueOf(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getCodeDish()));
        txtQuantity.setText(String.valueOf(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getQuantity()));
        txtNameDish.setText(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getNameDish());
        txtDescription.setText(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getDescriptionDish());
        txtPriceDish.setText(String.valueOf(((Dish) tblDishes.getSelectionModel().getSelectedItem()).getPriceDish()));
        cmbTypeDish.getSelectionModel().select(searchTypeDish((
                (Dish) tblDishes.getSelectionModel().getSelectedItem()).getCodeTypeDish()));
    }

    public TypeDish searchTypeDish(int codeTypeDish) {
        TypeDish result = null;
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_SearchTypeDish(?)");
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
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadTypeDishes()");
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
       btnCreate.setOnAction(event -> {
            if (isFormValid()) {
                if (isDataExistsInTableView(txtDishId.getText())) {
                    clearControls();
                } else {
                    if (showConfirmationDialog("Save Dish", "You want to add this dish?",
                            "Choose your option.", "Save", "Cancel")){
                        try {
                            save();
                            loadData();
                            clearControls();
                            notification(NotificationType.SUCCESS, "Dish added successfully", 5);
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

    public void edit() {
        if (tblDishes.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Update Dish", "You want to modify this dish?", 
                            "Choose your option.", "Update", "Cancel")) {
                            try {
                                update();
                                loadData();
                                clearControls();
                                notification(NotificationType.SUCCESS, "Dish successfully updated from database", 5);
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
        if (tblDishes.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Delete Dish", "Are you sure of deleting this register?", 
                            "Choose your option.", "Delete", "Cancel")){
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_DeleteDish(?)");
                            procedure.setInt(1,((Dish) tblDishes.getSelectionModel().getSelectedItem()).getCodeDish());
                            procedure.execute();
                            dishList.remove(tblDishes.getSelectionModel().getSelectedItem());
                            clearControls();
                            notification(NotificationType.SUCCESS, "Dish successfully deleted from database", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    notification(NotificationType.ERROR, "Please select an element from the table", 5);
                }
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
                        principalStage.dishWindow();
                    }
    }
    
    public void minimize() {
        Stage stage = (Stage) dishPane.getScene().getWindow();
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
        ObservableList<Dish> items = tblDishes.getItems();
        
        for (Dish dish : items) {
            if (String.valueOf(dish.getCodeDish()).equals(newData)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTextFieldEmpty(JFXTextField textField) {
        return textField.getText().trim().isEmpty();
    }

    private <T> boolean isComboBoxEmpty(ComboBox<T> comboBox) {
        return comboBox.getValue() == null;
    }
    
    private boolean isFormValid() {
        return !isTextFieldEmpty(txtNameDish)
                && !isTextFieldEmpty(txtDescription)
                && !isTextFieldEmpty(txtQuantity)
                && !isTextFieldEmpty(txtPriceDish)
                && !isComboBoxEmpty(cmbTypeDish);
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
