package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import org.juangalicia.bean.Company;
import org.juangalicia.db.Conexion; 
import org.juangalicia.main.Principal;
import org.juangalicia.report.GenerateReport;
import org.controlsfx.control.Notifications;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Optional;
import javafx.collections.FXCollections; 
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; 
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CompanyController implements Initializable {
   
    //Global Variables
    private Principal principalStage;
    private ObservableList<Company> companyList;
    private final String Background="/org/juangalicia/image/Report Background.png";   
    
    @FXML
    private AnchorPane companyPane;
    @FXML
    private JFXTextField txtCompanyId;
    @FXML
    private JFXTextField txtCompanyName;
    @FXML
    private JFXTextField txtCompanyAdress;
    @FXML
    private JFXTextField txtCompanyPhone;
    @FXML
    private JFXTextField txtCompanySearch;
    @FXML
    private TableView tblCompanys;
    @FXML
    private TableColumn colCompanyId;
    @FXML
    private TableColumn colCompanyName;
    @FXML
    private TableColumn colCompanyAdress;
    @FXML
    private TableColumn colCompanyPhone;
    @FXML
    private JFXButton btnCreate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        unlockControls();
    }
    
    //Set data on the table view
    public void loadData() {
        tblCompanys.setItems(getCompany());
        colCompanyId.setCellValueFactory(new PropertyValueFactory<Company, Integer>("codeCompany"));
        colCompanyName.setCellValueFactory(new PropertyValueFactory<Company, String>("nameCompany"));
        colCompanyAdress.setCellValueFactory(new PropertyValueFactory<Company, String>("adress"));
        colCompanyPhone.setCellValueFactory(new PropertyValueFactory<Company, String>("phone")); 
    }
    
    //Put info from TableView on the TextFields
    public void selectElement() {
        txtCompanyId.setText(String.valueOf(((Company) tblCompanys.getSelectionModel().getSelectedItem()).getCodeCompany()));
        txtCompanyName.setText(((Company) tblCompanys.getSelectionModel().getSelectedItem()).getNameCompany());
        txtCompanyAdress.setText(((Company) tblCompanys.getSelectionModel().getSelectedItem()).getAdress());
        txtCompanyPhone.setText(((Company) tblCompanys.getSelectionModel().getSelectedItem()).getPhone());
    }

    //Calls the SQL Storage Procedure and almacenates the registers on a ArrayList
    public ObservableList<Company> getCompany() {
        ArrayList<Company> list = new ArrayList<Company>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadCompanys");
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

    //Main add method
    public void create() {
        btnCreate.setOnAction(event -> {
            if (isFormValid()) {
                if (isDataExistsInTableView(txtCompanyName.getText())) {
                    notification("Warning", "org/juangalicia/image/warning.png", "The Company already exists in database", 5);
                    clearControls();
                } else {
                    if (showConfirmationDialog("Save Company", "You want to add this company?", 
                            "Choose your option.", "Save", "Cancel")){
                        try {
                            save();
                            loadData();
                            clearControls();
                            notification("Company Registred", "org/juangalicia/image/success.png", "Company added successfully", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        loadData();
                    }
                }
            } else {
                notification("Error", "org/juangalicia/image/error.png", "please complete all the fields", 5);
            }
        });
    }
    
    //Get the TextField info, almacenates on the bean class variables trought ObservableList and calls SQL Storage Procedure.
    public void save() {
        Company register = new Company();
        register.setNameCompany(txtCompanyName.getText());
        register.setAdress(txtCompanyAdress.getText());
        register.setPhone(txtCompanyPhone.getText());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_CreateCompany(?,?,?)");
            procedure.setString(1, register.getNameCompany());
            procedure.setString(2, register.getAdress());
            procedure.setString(3, register.getPhone());
            procedure.execute();
            companyList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Main update method
    public void edit() {
                if (tblCompanys.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Update Company", "You want to modify this company?", 
                            "Choose your option.", "Update", "Cancel")) {
                            try {
                                update();
                                loadData();
                                clearControls();
                                notification("Company Updated", "org/juangalicia/image/success.png", "Company successfully updated from database", 5);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            loadData();
                        }      
                }
                else {
                    notification("No element selected", "org/juangalicia/image/error.png", "Please select an element from the table", 5);
                }
    }

    //Calls SQL Storage Procedure and extracts the info on the variables to set them on the procedure.
    public void update() {
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_UpdateCompany(?,?,?,?)");
            Company register = (Company) tblCompanys.getSelectionModel().getSelectedItem();
            register.setNameCompany(txtCompanyName.getText());
            register.setAdress(txtCompanyAdress.getText());
            register.setPhone(txtCompanyPhone.getText());
            procedure.setInt(1, register.getCodeCompany());
            procedure.setString(2, register.getNameCompany());
            procedure.setString(3, register.getAdress());
            procedure.setString(4, register.getPhone());
            procedure.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Calls SQL Storage Procedure and set the ID from the TableView selected register on the procedure.
    public void delete() {
                if (tblCompanys.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Delete Company", "Are you sure of deleting this register?", 
                            "Choose your option.", "Delete", "Cancel")){
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_DeleteCompany(?)");
                            procedure.setInt(1,((Company) tblCompanys.getSelectionModel().getSelectedItem()).getCodeCompany());
                            procedure.execute();
                            companyList.remove(tblCompanys.getSelectionModel().getSelectedItem());
                            clearControls();
                            notification("Company Deleted", "org/juangalicia/image/success.png", 
                                    "Company successfully deleted from database", 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    notification("No element selected", "org/juangalicia/image/error.png", "Please select an element from the table", 5);
                }
    }

    //Report statements
    public void generateReport() {
                printReport();
    }

    public void printReport() {
        try {
            Map parameters = new HashMap();
            parameters.clear();
            parameters.put("codeCompany", null);
            parameters.put("Background", this.getClass().getResourceAsStream(Background));
            GenerateReport.showReport("companyReport.jasper", "Companies Report", parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void printGeneralReport() {            
        try{
            Map parameters = new HashMap();
            parameters.clear();
            int codeCompany = Integer.valueOf(((Company)tblCompanys.getSelectionModel().getSelectedItem()).getCodeCompany());
            parameters.put("codeCompany", codeCompany);
            parameters.put("fondo", Background);
            GenerateReport.showReport("GeneralReport.jasper", "General Report", parameters);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //FiltredList for the search TextField
    public void companySearch() {

        FilteredList<Company> filter = new FilteredList<>(companyList, e -> true);

        txtCompanySearch.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Company predicateCompany) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateCompany.getCodeCompany()).contains(searchKey)) {
                    return true;
                } else if (predicateCompany.getNameCompany().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateCompany.getAdress().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateCompany.getPhone().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Company> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblCompanys.comparatorProperty());
        tblCompanys.setItems(sortList);
    }
    
    //Menubar buttons methods
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
                        principalStage.companyWindow();
                    }
    }
    
    public void minimize() {
        Stage stage = (Stage) companyPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    //Notifications and dialogs methods
    public void notification(String title, String img, String text, int seconds){
        Image imgN = new Image(img);
        Notifications notification = Notifications.create();
        notification.title(title);
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
        ObservableList<Company> items = tblCompanys.getItems();
        
        for (Company company : items) {
            if (company.getNameCompany().equals(newData)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTextFieldEmpty(JFXTextField textField) {
        return textField.getText().trim().isEmpty();
    }
    
    private boolean isFormValid() {
        return !isTextFieldEmpty(txtCompanyName)
                && !isTextFieldEmpty(txtCompanyAdress)
                && !isTextFieldEmpty(txtCompanyPhone);
    }

    //Controls methods
    public void lockControls() {
        txtCompanyId.setEditable(false);
        txtCompanyName.setEditable(false);
        txtCompanyAdress.setEditable(false);
        txtCompanyPhone.setEditable(false);
    }

    public void unlockControls() {
        txtCompanyId.setEditable(false);
        txtCompanyName.setEditable(true);
        txtCompanyAdress.setEditable(true);
        txtCompanyPhone.setEditable(true);
    }

    public void clearControls() {
        txtCompanyId.clear();
        txtCompanyName.clear();
        txtCompanyAdress.clear();
        txtCompanyPhone.clear();
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
    
    public void Products_has_DishesWindow(){
        principalStage.Products_has_DishesWindow();
    }
    
    public void Services_has_DishesWindow(){
        principalStage.Services_has_DishesWindow();
    }
    
    public void Services_has_EmployeesWindow(){
        principalStage.Services_has_EmployeesWindow();
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
