package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.ResourceBundle;

import org.juangalicia.bean.Service;
import org.juangalicia.main.Principal;

import com.jfoenix.controls.JFXTimePicker;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
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
import org.juangalicia.bean.Company;
import org.juangalicia.db.Conexion;

public class ServiceController implements Initializable {
    private enum operations {
        CREATE, SAVE, DELETE, UPDATE, CANCEL, NONE
    };

    private operations typeOfOperation = operations.NONE;
    private Principal principalStage;
    private ObservableList<Service> serviceList;
    private ObservableList<Company> companyList;
    private DatePicker date;

    
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
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnRead;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXComboBox cmbCompanyId;

    @FXML
    private ImageView imgCreate;

    @FXML
    private ImageView imgDelete;

    @FXML
    private ImageView imgRead;

    @FXML
    private ImageView imgUpdate;

    @FXML
    private GridPane grpDate;

    @FXML
    private JFXTimePicker jfxHourService;
    
    @FXML
    private JFXTextField txtSearchService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        date = new DatePicker(Locale.ENGLISH);
        date.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        date.getCalendarView().todayButtonTextProperty().set("Today");
        date.getCalendarView().setShowWeeks(false);
        date.getStylesheets().add("org/juangalicia/resource/TonysKinal.css");
        grpDate.add(date, 3, 0);
        cmbCompanyId.setItems(getCompany());
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

    public void SelectElement(){
        txtServiceId.setText(String.valueOf(((Service)tblServices.getSelectionModel().getSelectedItem()).getCodeService()));
        date.selectedDateProperty().set(((Service)tblServices.getSelectionModel().getSelectedItem()).getDateService());
        txtTypeOfService.setText(((Service)tblServices.getSelectionModel().getSelectedItem()).getTypeService());
        jfxHourService.setValue((((Service)tblServices.getSelectionModel().getSelectedItem()).getHourService()).toLocalTime());
        txtPlaceOfService.setText(((Service)tblServices.getSelectionModel().getSelectedItem()).getPlaceService());
        txtContactPhone.setText(((Service)tblServices.getSelectionModel().getSelectedItem()).getPhoneContact());
        cmbCompanyId.getSelectionModel().select(searchCompany(((Service)tblServices.getSelectionModel().getSelectedItem()).getCodeCompany()));
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
        switch(typeOfOperation){
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
                btnCreate.setText("Create Service");
                btnUpdate.setText("Update Service");
                btnDelete.setDisable(false);
                btnRead.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }
    
    public void edit(){
        switch(typeOfOperation){
            case NONE:
                if (tblServices.getSelectionModel().getSelectedItem() != null) {
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
                btnCreate.setText("Create Service");
                btnUpdate.setText("Update Service");
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
                btnUpdate.setText("Update Service");
                btnDelete.setText("Delete Service");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;
        }
    }
    
    public void update(){
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_UpdateService(?,?,?,?,?,?,?)");
            Service register = (Service) tblServices.getSelectionModel().getSelectedItem();
            register.setDateService(date.getSelectedDate());
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
        switch(typeOfOperation){
            case UPDATE:
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnUpdate.setText("Update Service");
                btnDelete.setText("Delete Service");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;
                
            default:
                if (tblServices.getSelectionModel().getSelectedItem() != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Service");
                    alert.setHeaderText("Are you sure of deleting this register? You are gonna delete a foreign key");
                    alert.setContentText("Choose your option.");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteService(?)");
                            procedure.setInt(1,
                                    ((Service) tblServices.getSelectionModel().getSelectedItem()).getCodeService());
                            procedure.execute();
                            serviceList.remove(tblServices.getSelectionModel().getSelectedItem());
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
    
    public void save() {
        Service register = new Service();
        register.setDateService(date.getSelectedDate());
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
    
    public void minimize() {
        Stage stage = (Stage) servicePane.getScene().getWindow();
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
            date.selectedDateProperty().set(null);
            txtTypeOfService.clear();
            jfxHourService.setValue(null);
            txtPlaceOfService.clear();
            txtContactPhone.clear();
            cmbCompanyId.setValue(null);
        }
    
        public void setPrincipalStage(Principal principalStage) {
        this.principalStage = principalStage;
    }
        public void menuPrincipal(){
            principalStage.principalWindow();
        }
        
}
