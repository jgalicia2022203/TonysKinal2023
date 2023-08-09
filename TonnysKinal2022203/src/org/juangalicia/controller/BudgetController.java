package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import org.juangalicia.bean.Budget;
import org.juangalicia.bean.Company;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;
import org.controlsfx.control.Notifications;
import org.juangalicia.report.GenerateReport;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.Optional;
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

public class BudgetController implements Initializable {
    private Principal principalStage;
    private ObservableList<Budget> budgetList;
    private ObservableList<Company> companyList;
    private final String Background = "/org/juangalicia/image/Report Background.png";
    
    @FXML 
    private JFXDatePicker date;
    @FXML
    private AnchorPane budgetPane;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private ComboBox cmbCompanyId;
    @FXML
    private TableColumn colAmount;
    @FXML
    private TableColumn colBudgetId;
    @FXML
    private TableColumn colCompanyId;
    @FXML
    private TableColumn colDateOfRequest;
    @FXML
    private TableView tblBudgets;
    @FXML
    private JFXTextField txtAmount;
    @FXML
    private JFXTextField txtBudgetId;
    @FXML
    private JFXTextField txtBudgetSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        cmbCompanyId.setItems(getCompany());
    }

    public void loadData() {
        tblBudgets.setItems(getBudget());
        colBudgetId.setCellValueFactory(new PropertyValueFactory<Budget, Integer>("codeBudget"));
        colDateOfRequest.setCellValueFactory(new PropertyValueFactory<Budget, Date>("dateRequest"));
        colAmount.setCellValueFactory(new PropertyValueFactory<Budget, Double>("amountBudget"));
        colCompanyId.setCellValueFactory(new PropertyValueFactory<Budget, Integer>("codeCompany"));
    }

    public void SelectElement() {
        try {
            Budget selectedBudget = (Budget) tblBudgets.getSelectionModel().getSelectedItem();
            txtBudgetId.setText(String.valueOf(selectedBudget.getCodeBudget()));

            // Convert java.sql.Date to java.util.LocalDate
            java.util.Date utilDate = new java.util.Date(selectedBudget.getDateRequest().getTime());
            LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            date.setValue(localDate);
            txtAmount.setText(String.valueOf(selectedBudget.getAmountBudget()));
            cmbCompanyId.getSelectionModel().select(searchCompany(selectedBudget.getCodeCompany()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Company searchCompany(int codeCompany) {
        Company result = null;
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_SearchCompany(?)");
            procedure.setInt(1, codeCompany);
            ResultSet register = procedure.executeQuery();
            while (register.next()) {
                result = new Company(register.getInt("codeCompany"), register.getString("nameCompany"),
                        register.getString("adress"), register.getString("phone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ObservableList<Budget> getBudget() {
        ArrayList<Budget> list = new ArrayList<Budget>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadBudgets()");
            ResultSet result = procedure.executeQuery();
            while (result.next()) {
                list.add(new Budget(result.getInt("codeBudget"),
                        result.getDate("dateRequest"),
                        result.getDouble("amountBudget"),
                        result.getInt("codeCompany")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return budgetList = FXCollections.observableArrayList(list);
    }

    public ObservableList<Company> getCompany() {
        ArrayList<Company> list = new ArrayList<Company>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadCompanys()");
            ResultSet result = procedure.executeQuery();
            while (result.next()) {
                list.add(new Company(result.getInt("codeCompany"),
                        result.getString("nameCompany"),
                        result.getString("adress"),
                        result.getString("phone")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return companyList = FXCollections.observableArrayList(list);
    }

    public void create() {
        btnCreate.setOnAction(event -> {
            if (isFormValid()) {
                if (isDataExistsInTableView(txtBudgetId.getText())) {
                    clearControls();
                } else {
                    if (showConfirmationDialog("Add Budget", "You want to add this budget?", 
                            "Choose your option.", "Save", "Cancel")){
                        try {
                            save();
                            loadData();
                            clearControls();
                            notification(NotificationType.SUCCESS, "Budget added successfully", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        loadData();
                    }
                }
            } else {
                notification(NotificationType.ERROR, "please complete all the fields", 5);
            }
        });
    }

    public void save() {
         Budget register = new Budget();
        // Convert LocalDate to java.sql.Date
        java.sql.Date dateRequest = java.sql.Date.valueOf(date.getValue());
        register.setDateRequest(dateRequest);
        register.setAmountBudget(Double.parseDouble(txtAmount.getText()));
        register.setCodeCompany(((Company) cmbCompanyId.getSelectionModel().getSelectedItem()).getCodeCompany());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_CreateBudget(?,?,?)");
            procedure.setDate(1, new java.sql.Date(register.getDateRequest().getTime()));
            procedure.setDouble(2, register.getAmountBudget());
            procedure.setInt(3, register.getCodeCompany());
            procedure.execute();
            budgetList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void edit() {
        if (tblBudgets.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Update Budget", "You want to modify this Budget?", 
                            "Choose your option.", "Update", "Cancel")) {
                            try {
                                update();
                                loadData();
                                clearControls();
                                notification(NotificationType.SUCCESS, "Budget successfully updated from database", 5);
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
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_UpdateBudget(?,?,?,?)");
            Budget register = (Budget) tblBudgets.getSelectionModel().getSelectedItem();

            // Convert LocalDate to java.sql.Date
            java.sql.Date dateRequest = java.sql.Date.valueOf(date.getValue());

            register.setDateRequest(dateRequest);
            register.setAmountBudget(Double.parseDouble(txtAmount.getText()));
            register.setCodeCompany(((Company) cmbCompanyId.getSelectionModel().getSelectedItem()).getCodeCompany());

            procedure.setInt(1, register.getCodeBudget());
            procedure.setDate(2, new java.sql.Date(register.getDateRequest().getTime()));
            procedure.setDouble(3, register.getAmountBudget());
            procedure.setInt(4, register.getCodeCompany());
            procedure.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        if (tblBudgets.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Delete Budget", "Are you sure of deleting this register?", 
                            "Choose your option.", "Delete", "Cancel")){
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_DeleteBudget(?)");
                            procedure.setInt(1,((Budget) tblBudgets.getSelectionModel().getSelectedItem()).getCodeBudget());
                            procedure.execute();
                            budgetList.remove(tblBudgets.getSelectionModel().getSelectedItem());
                            clearControls();
                            notification(NotificationType.SUCCESS, "Budget successfully deleted from database", 5);
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
            GenerateReport.showReport("BudgetReport.jasper", "Budget Report", parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void budgetSearch() {

        FilteredList<Budget> filter = new FilteredList<>(budgetList, e -> true);

        txtBudgetSearch.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Budget predicateBudget) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateBudget.getCodeBudget()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateBudget.getDateRequest()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateBudget.getAmountBudget()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateBudget.getCodeCompany()).contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Budget> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblBudgets.comparatorProperty());
        tblBudgets.setItems(sortList);
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
                        principalStage.budgetWindow();
                    }
    }
    
    public void minimize() {
        Stage stage = (Stage) budgetPane.getScene().getWindow();
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
        ObservableList<Budget> items = tblBudgets.getItems();
        
        for (Budget budget : items) {
            if (String.valueOf(budget.getCodeBudget()).equals(newData)) {
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
    
    private boolean isDatePickerEmpty(JFXDatePicker date) {
    return date.getValue() == null;
    }
    
    private boolean isFormValid() {
        return !isTextFieldEmpty(txtBudgetId)
                && !isTextFieldEmpty(txtAmount)
                && !isDatePickerEmpty(date)
                && !isComboBoxEmpty(cmbCompanyId);
    }

    public void lockControls() {
        txtBudgetId.setEditable(false);
        txtAmount.setEditable(false);
        cmbCompanyId.setDisable(true);
        date.setDisable(true);
    }

    public void unlockControls() {
        txtBudgetId.setEditable(false);
        txtAmount.setEditable(true);
        cmbCompanyId.setDisable(false);
        date.setDisable(false);
    }

    public void clearControls() {
        txtBudgetId.clear();
        txtAmount.clear();
        cmbCompanyId.setValue(null);
        date.setValue(null);
    }

    //Stages
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
