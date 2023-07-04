package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.Optional;

import org.juangalicia.bean.Budget;
import org.juangalicia.bean.Company;
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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.juangalicia.report.GenerateReport;

public class BudgetController implements Initializable {
    private Principal principalStage;

    private enum operations {
        SAVE, DELETE, UPDATE, NONE
    }

    private operations typeOfOperation = operations.NONE;
    private ObservableList<Budget> budgetList;
    private ObservableList<Company> companyList;
    private DatePicker date;
    
    @FXML 
    private AnchorPane budgetPane;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private JFXButton btnRead;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXComboBox cmbCompanyId;
    @FXML
    private GridPane grpDate;
    @FXML
    private ImageView imgCreate;
    @FXML
    private ImageView imgRead;
    @FXML
    private ImageView imgUpdate;
    @FXML
    private ImageView imgDelete;
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
        date = new DatePicker(Locale.ENGLISH);
        date.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        date.getCalendarView().todayButtonTextProperty().set("Today");
        date.getCalendarView().setShowWeeks(false);
        date.getStylesheets().add("/org/juangalicia/resource/TonysKinal.css");
        grpDate.add(date, 3, 0);
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
        txtBudgetId
                .setText(String.valueOf(((Budget) tblBudgets.getSelectionModel().getSelectedItem()).getCodeBudget()));
        date.selectedDateProperty().set(((Budget) tblBudgets.getSelectionModel().getSelectedItem()).getDateRequest());
        txtAmount
                .setText(String.valueOf(((Budget) tblBudgets.getSelectionModel().getSelectedItem()).getAmountBudget()));
        cmbCompanyId.getSelectionModel()
                .select(searchCompany(((Budget) tblBudgets.getSelectionModel().getSelectedItem()).getCodeCompany()));
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
                btnCreate.setText("Create Budget");
                btnUpdate.setText("Read Budget");
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
        Budget register = new Budget();
        register.setDateRequest(date.getSelectedDate());
        register.setAmountBudget(Double.parseDouble(txtAmount.getText()));
        register.setCodeCompany(((Company) cmbCompanyId.getSelectionModel().getSelectedItem()).getCodeCompany());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_CreateBudget(?,?,?)");
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
        switch (typeOfOperation) {
            case NONE:
                if (tblBudgets.getSelectionModel().getSelectedItem() != null) {
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
                btnCreate.setText("Create Budget");
                btnUpdate.setText("Update Budget");
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
                btnUpdate.setText("Update Budget");
                btnDelete.setText("Delete Budget");
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
                    .prepareCall("call sp_UpdateBudget(?,?,?,?)");
            Budget register = (Budget) tblBudgets.getSelectionModel().getSelectedItem();
            register.setDateRequest(date.getSelectedDate());
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
        switch (typeOfOperation) {
            case UPDATE:
                update();
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Budget");
                btnDelete.setText("Delete Budget");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblBudgets.getSelectionModel().getSelectedItem() != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Budget");
                    alert.setHeaderText("Are you sure of deleting this register? You are gonna delete a foreign key");
                    alert.setContentText("Choose your option.");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteBudget(?)");
                            procedure.setInt(1,
                                    ((Budget) tblBudgets.getSelectionModel().getSelectedItem()).getCodeCompany());
                            procedure.execute();
                            budgetList.remove(tblBudgets.getSelectionModel().getSelectedItem());
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
    
    public void generateReport(){
            switch(typeOfOperation){
                case NONE:
                    printReport();
                    break;
               
            }
    }
    
    public void printReport(){
        Map parameters = new HashMap();
        int codCompany = Integer.valueOf(((Company)cmbCompanyId.getSelectionModel().getSelectedItem()).getCodeCompany());
        parameters.put("codCompany", codCompany);
        GenerateReport.showReport("reportBudgets.jasper", "Budget's Report", parameters);
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
        
    public void minimize() {
        Stage stage = (Stage) budgetPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    public void close() {
        System.exit(0);
    }        

    public void lockControls() {
        txtBudgetId.setEditable(false);
        txtAmount.setEditable(false);
        cmbCompanyId.setDisable(true);
        date.setDisable(true);
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
        date.selectedDateProperty().set(null);
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
    
    public void companiesWindow() {
        principalStage.companyWindow();
    }
}
