package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import org.juangalicia.bean.Service;
import org.juangalicia.main.Principal;
import org.juangalicia.bean.Company;
import org.juangalicia.db.Conexion;
import org.juangalicia.report.GenerateReport;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.ResourceBundle;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.controlsfx.control.Notifications;

public class ServiceController implements Initializable {
    private Principal principalStage;
    private ObservableList<Service> serviceList;
    private ObservableList<Company> companyList;
    private final String Background="/org/juangalicia/image/Report Background.png";   
    
    @FXML
    private AnchorPane servicePane;
    @FXML
    private JFXTextField txtContactPhone;
    @FXML
    private JFXTextField txtPlaceOfService;
    @FXML
    private JFXTextField txtServiceId;
    @FXML
    private JFXTextField txtTypeOfService;
    @FXML
    private TableColumn colCompanyId;
    @FXML
    private TableColumn colContactPhone;
    @FXML
    private TableColumn colDate;
    @FXML
    private TableColumn colHour;
    @FXML
    private TableColumn colPlace;
    @FXML
    private TableColumn colServiceId;
    @FXML
    private TableColumn colTypeOfService;
    @FXML
    private TableView tblServices;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private ComboBox cmbCompanyId;
    @FXML
    private JFXTimePicker jfxHourService;
    @FXML
    private JFXDatePicker date;
    @FXML
    private JFXTextField txtSearchService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        cmbCompanyId.setItems(getCompany());
        jfxHourService.setStyle("-fx-alignment: CENTER_LEFT;");
    }

    public void loadData(){
        tblServices.setItems(getService());
        colServiceId.setCellValueFactory(new PropertyValueFactory<Service, Integer>("codeService"));
        colDate.setCellValueFactory(new PropertyValueFactory<Service, Date>("dateService"));
        colTypeOfService.setCellValueFactory(new PropertyValueFactory<Service, String>("typeService"));
        colHour.setCellValueFactory(new PropertyValueFactory<Service, Time>("hourService"));
        colPlace.setCellValueFactory(new PropertyValueFactory<Service, String>("placeService"));
        colContactPhone.setCellValueFactory(new PropertyValueFactory<Service, String>("phoneContact"));
        colCompanyId.setCellValueFactory(new PropertyValueFactory<Service, Integer>("codeCompany"));
    }

    public void SelectElement() {
        Service selectedService = (Service) tblServices.getSelectionModel().getSelectedItem();
        txtServiceId.setText(String.valueOf(selectedService.getCodeService()));

        // Convert java.sql.Date to java.util.Date
        java.util.Date utilDate = new java.util.Date(selectedService.getDateService().getTime());

        // Convert java.util.Date to java.time.LocalDate
        LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        date.setValue(localDate);

        txtTypeOfService.setText(selectedService.getTypeService());
        jfxHourService.setValue(selectedService.getHourService().toLocalTime());
        txtPlaceOfService.setText(selectedService.getPlaceService());
        txtContactPhone.setText(selectedService.getPhoneContact());
        cmbCompanyId.getSelectionModel().select(searchCompany(selectedService.getCodeCompany()));
    }
    
    public Company searchCompany(int codeCompany){
        Company result = null;
        
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_SearchCompany(?)");
            procedure.setInt(1, codeCompany);
            ResultSet register = procedure.executeQuery();
            while(register.next()){
                result = new Company(register.getInt("codeCompany"), register.getString("nameCompany"),
                                        register.getString("adress"), register.getString("phone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    
    }
    
    public ObservableList<Service> getService(){
        ArrayList<Service> list = new ArrayList<Service>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadServices()");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
                list.add(new Service(result.getInt("codeService"),
                        result.getDate("dateService"),
                        result.getString("typeService"),
                        result.getTime("hourService"),
                        result.getString("placeService"),
                        result.getString("phoneContact"),
                        result.getInt("codeCompany")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceList = FXCollections.observableArrayList(list);
    }
    
    public ObservableList<Company> getCompany(){
        ArrayList<Company> list = new ArrayList<Company>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadCompanys()");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
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
    
    public void create(){
        btnCreate.setOnAction(event -> {
            if (isFormValid()) {
                if (isDataExistsInTableView(txtServiceId.getText())) {
                    clearControls();
                } else {
                    if (showConfirmationDialog("Add Service", "You want to add this service?", 
                            "Choose your option.", "Save", "Cancel")){
                        try {
                            save();
                            loadData();
                            clearControls();
                            notification(NotificationType.SUCCESS, "Service added successfully", 5);
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
        Service register = new Service();
        // Convert LocalDate to java.sql.Date
        java.sql.Date dateRequest = java.sql.Date.valueOf(date.getValue());
        register.setDateService(dateRequest);
        register.setTypeService(txtTypeOfService.getText());
        register.setHourService(Time.valueOf(jfxHourService.getValue()));
        register.setPlaceService(txtPlaceOfService.getText());
        register.setPhoneContact(txtContactPhone.getText());
        register.setCodeCompany(((Company)cmbCompanyId.getSelectionModel().getSelectedItem()).getCodeCompany());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_CreateService(?,?,?,?,?,?)");
            procedure.setDate(1, new java.sql.Date(register.getDateService().getTime()));
            procedure.setString(2, register.getTypeService());
            procedure.setTime(3, new java.sql.Time(register.getHourService().getTime()));
            procedure.setString(4, register.getPlaceService());
            procedure.setString(5, register.getPhoneContact());
            procedure.setInt(6, register.getCodeCompany());
            procedure.execute();
            serviceList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void edit(){
        if (tblServices.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Update Service", "You want to modify this Service?", 
                            "Choose your option.", "Update", "Cancel")) {
                            try {
                                update();
                                loadData();
                                clearControls();
                                notification(NotificationType.SUCCESS, "Service successfully updated from database", 5);
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
    
    public void update(){
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_UpdateService(?,?,?,?,?,?,?)");
            Service register = (Service) tblServices.getSelectionModel().getSelectedItem();
            // Convert LocalDate to java.sql.Date
            java.sql.Date dateRequest = java.sql.Date.valueOf(date.getValue());
            register.setDateService(dateRequest);
            register.setTypeService(txtTypeOfService.getText());
            register.setHourService(Time.valueOf(jfxHourService.getValue()));
            register.setPlaceService(txtPlaceOfService.getText());
            register.setPhoneContact(txtContactPhone.getText());
            register.setCodeCompany(((Company)cmbCompanyId.getSelectionModel().getSelectedItem()).getCodeCompany());
            procedure.setInt(1, register.getCodeService());
            procedure.setDate(2, new java.sql.Date(register.getDateService().getTime()));
            procedure.setString(3, register.getTypeService());
            procedure.setTime(4, register.getHourService());
            procedure.setString(5, register.getPlaceService());
            procedure.setString(6, register.getPhoneContact());
            procedure.setInt(7, register.getCodeCompany());
            procedure.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void delete(){
        if (tblServices.getSelectionModel().getSelectedItem() != null) {
                    if (showConfirmationDialog("Delete Service", "Are you sure of deleting this register?", 
                            "Choose your option.", "Delete", "Cancel")){
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_DeleteService(?)");
                            procedure.setInt(1,((Service) tblServices.getSelectionModel().getSelectedItem()).getCodeService());
                            procedure.execute();
                            serviceList.remove(tblServices.getSelectionModel().getSelectedItem());
                            clearControls();
                            notification(NotificationType.SUCCESS, "Service successfully deleted from database", 5);
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
            parameters.put("codeService", null);
            parameters.put("Background", this.getClass().getResourceAsStream(Background));
            GenerateReport.showReport("serviceReport.jasper", "Services Report", parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void serviceSearch() {

        FilteredList<Service> filter = new FilteredList<>(serviceList, e -> true);

        txtSearchService.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Service predicateService) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateService.getCodeService()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateService.getDateService()).contains(searchKey)) {
                    return true;
                } else if (predicateService.getTypeService().toLowerCase().contains(searchKey)) {
                    return true;
                } else if(String.valueOf(predicateService.getHourService()).toLowerCase().contains(searchKey)){
                    return true;
                } else if(predicateService.getPlaceService().toLowerCase().contains(searchKey)){
                    return true;
                } else if(predicateService.getPhoneContact().toLowerCase().contains(searchKey)){
                    return true;
                } else if(String.valueOf(predicateService.getCodeCompany()).toLowerCase().contains(searchKey)){
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Service> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblServices.comparatorProperty());
        tblServices.setItems(sortList);
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
                        principalStage.serviceWindow();
                    }
    }
    
    public void minimize() {
        Stage stage = (Stage) servicePane.getScene().getWindow();
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
        ObservableList<Service> items = tblServices.getItems();
        
        for (Service service : items) {
            if (String.valueOf(service.getCodeService()).equals(newData)) {
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
    
    private boolean isTimePickerEmpty(JFXTimePicker jfxHourService) {
    return jfxHourService.getValue() == null;
    }
    
    private boolean isFormValid() {
        return !isTextFieldEmpty(txtTypeOfService)
                && !isTextFieldEmpty(txtPlaceOfService)
                && !isTextFieldEmpty(txtContactPhone)
                && !isDatePickerEmpty(date)
                && !isTimePickerEmpty(jfxHourService)
                && !isComboBoxEmpty(cmbCompanyId);
    }
    
    public void lockControls(){
            txtServiceId.setEditable(false);
            date.setDisable(true);
            txtTypeOfService.setEditable(false);
            jfxHourService.setDisable(true);
            txtPlaceOfService.setEditable(false);
            txtContactPhone.setEditable(false);
            cmbCompanyId.setDisable(true);
    }
        
    public void unlockControls(){
            txtServiceId.setEditable(false);
            date.setDisable(false);
            txtTypeOfService.setEditable(true);
            jfxHourService.setDisable(false);
            txtPlaceOfService.setEditable(true);
            txtContactPhone.setEditable(true);
            cmbCompanyId.setDisable(false);
    }
        
    public void clearControls(){
            txtServiceId.clear();
            date.setValue(null);
            txtTypeOfService.clear();
            jfxHourService.setValue(null);
            txtPlaceOfService.clear();
            txtContactPhone.clear();
            cmbCompanyId.setValue(null);
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

