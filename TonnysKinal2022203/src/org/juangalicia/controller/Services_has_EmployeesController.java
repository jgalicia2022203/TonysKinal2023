package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.juangalicia.bean.Employee;
import org.juangalicia.bean.Service;
import org.juangalicia.bean.Services_has_Dishes;
import org.juangalicia.bean.Services_has_Employees;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

public class Services_has_EmployeesController implements Initializable{
    private Principal principalStage;
    
    private enum operations{
        SAVE, DELETE, UPDATE, NONE
    }
    private operations typeOfOperation = operations.NONE;
            
    private ObservableList<Services_has_Employees> servicesHasEmployeesList;
    private ObservableList<Service> serviceList;
    private ObservableList<Employee> employeeList;
    private DatePicker date;
    
    @FXML
    private AnchorPane ServiceHasEmployeesPane;
    @FXML 
    private JFXButton btnCreate;
    @FXML
    private JFXButton btnCancel;
    @FXML 
    private JFXComboBox cmbCodeEmployee;
    @FXML
    private JFXComboBox cmbCodeService;
    @FXML 
    private GridPane grpDate;
    @FXML 
    private ImageView imgCreate;
    @FXML 
    private ImageView imgCancel;
    @FXML 
    private TableColumn colCodeEmployee;
    @FXML
    private TableColumn colCodeService;
    @FXML
    private TableColumn colDateEvent;
    @FXML
    private TableColumn colHourEvent;
    @FXML
    private TableColumn colPlaceEvent;
    @FXML
    private TableColumn colServices_ServiceId;
    @FXML 
    private TableView tblServicesHasEmployees;
    @FXML 
    private JFXTextField txtPlaceEvent;
    @FXML 
    private JFXTextField txtServices_ServiceId;
    @FXML
    private JFXTextField txtSearchSHE;
    @FXML
    private JFXTimePicker jfxHourEvent;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        loadData();
        date = new DatePicker(Locale.ENGLISH);
        date.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        date.getCalendarView().todayButtonTextProperty().set("Today");
        date.getCalendarView().setShowWeeks(false);
        date.getStylesheets().add("org/juangalicia/resource/TonysKinal.css");
        grpDate.add(date, 1, 1);
        cmbCodeEmployee.setItems(getEmployee());
        cmbCodeService.setItems(getService());
    }
    
    public void loadData(){
        tblServicesHasEmployees.setItems(getServicesHasEmployees());
        colServices_ServiceId.setCellValueFactory(new PropertyValueFactory<Services_has_Employees, Integer>("Services_codeService"));
        colPlaceEvent.setCellValueFactory(new PropertyValueFactory<Services_has_Employees, String>("placeEvent"));
        colHourEvent.setCellValueFactory(new PropertyValueFactory<Services_has_Employees, Time>("hourEvent"));
        colDateEvent.setCellValueFactory(new PropertyValueFactory<Services_has_Employees, Date>("dateEvent"));
        colCodeService.setCellValueFactory(new PropertyValueFactory<Services_has_Employees, Integer>("codeService"));
        colCodeEmployee.setCellValueFactory(new PropertyValueFactory<Services_has_Employees, Integer>("codeEmployee"));
    }

    public void SelectElement(){
        txtServices_ServiceId.setText(String.valueOf(((Services_has_Employees)tblServicesHasEmployees.getSelectionModel().getSelectedItem()).getServices_codeService()));
        cmbCodeService.getSelectionModel().select(searchService(((Services_has_Employees)tblServicesHasEmployees.getSelectionModel().getSelectedItem()).getCodeService()));
        cmbCodeEmployee.getSelectionModel().select(searchEmployee(((Services_has_Employees)tblServicesHasEmployees.getSelectionModel().getSelectedItem()).getCodeEmployee()));
        date.selectedDateProperty().set(((Services_has_Employees)tblServicesHasEmployees.getSelectionModel().getSelectedItem()).getDateEvent());
        jfxHourEvent.setValue((((Services_has_Employees)tblServicesHasEmployees.getSelectionModel().getSelectedItem()).getHourEvent()).toLocalTime());
        txtPlaceEvent.setText((((Services_has_Employees)tblServicesHasEmployees.getSelectionModel().getSelectedItem()).getPlaceEvent()));
    }
    
    public Service searchService(int codeService){
        Service result = null;
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_SearchService(?)");
            procedure.setInt(1, codeService);
            ResultSet register = procedure.executeQuery();
            while(register.next()){
                result = new Service(register.getInt("codeService"), register.getDate("dateService"), register.getString("typeService"),
                                        register.getTime("hourService"), register.getString("placeService"), register.getString("phoneContact"),
                                        register.getInt("codeCompany"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public Employee searchEmployee(int codeEmployee){
        Employee result = null;
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_SearchEmployee(?)");
            procedure.setInt(1, codeEmployee);
            ResultSet register = procedure.executeQuery();
            while(register.next()){
                result = new Employee(register.getInt("codeEmployee"), register.getInt("numberEmployee"), 
                        register.getString("secondNameEmployee"), register.getString("firstNameEmployee"), 
                        register.getString("AdressEmployee"), register.getString("contactPhone"),
                        register.getString("cookDegree"), register.getInt("codeTypeEmployee"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public ObservableList<Services_has_Employees> getServicesHasEmployees(){
        ArrayList <Services_has_Employees> list = new ArrayList<Services_has_Employees>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadServices_has_Employees()");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
                list.add(new Services_has_Employees(result.getInt("Services_codeService"), result.getInt("codeService"), 
                                                result.getInt("codeEmployee"), result.getDate("dateEvent"), result.getTime("hourEvent"),
                                                result.getString("placeEvent")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return servicesHasEmployeesList = FXCollections.observableArrayList(list);
    
    }
    
    public ObservableList<Service> getService(){
        ArrayList<Service> list = new ArrayList<Service>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadServices()");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
                list.add(new Service(result.getInt("codeService"), result.getDate("dateService"), result.getString("typeService"),
                                     result.getTime("hourService"), result.getString("placeService"), result.getString("phoneContact"),
                                        result.getInt("codeCompany")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceList = FXCollections.observableArrayList(list);
    }
    
    public ObservableList<Employee> getEmployee(){
        ArrayList<Employee> list = new ArrayList<Employee>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadEmployees()");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
                list.add(new Employee(result.getInt("codeEmployee"), result.getInt("numberEmployee"), result.getString("secondNameEmployee"),
                                    result.getString("firstNameEmployee"), result.getString("adressEmployee"), result.getString("contactPhone"),
                                    result.getString("cookDegree"), result.getInt("codeTypeEmployee")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employeeList = FXCollections.observableArrayList(list);
    }
    
    public void create(){
        switch(typeOfOperation){
            case NONE:
                clearControls();
                unlockControls();
                btnCreate.setText("Save");
                btnCancel.setText("Cancel");
                imgCreate.setImage(new Image("/org/juangalicia/image/save.png"));
                imgCancel.setImage(new Image("/org/juangalicia/image/cancel.png"));
                typeOfOperation = operations.SAVE;
                loadData();
                break;
                
            case SAVE:
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Create Service has Employees");
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
                btnCreate.setText("Create Service has Employee");
                btnCancel.setText(" ");
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgCancel.setImage(null);
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }
    
    public void save(){
        Services_has_Employees register = new Services_has_Employees();
        register.setServices_codeService(Integer.parseInt(txtServices_ServiceId.getText()));
        register.setCodeService((((Service)cmbCodeService.getSelectionModel().getSelectedItem()).getCodeService()));
        register.setCodeEmployee((((Employee)cmbCodeEmployee.getSelectionModel().getSelectedItem()).getCodeEmployee()));
        register.setDateEvent(date.getSelectedDate());
        register.setHourEvent(Time.valueOf(jfxHourEvent.getValue()));
        register.setPlaceEvent(txtPlaceEvent.getText());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_CreateService_has_Employee(?,?,?,?,?,?)");
            procedure.setInt(1, register.getServices_codeService());
            procedure.setInt(2, register.getCodeService());
            procedure.setInt(3, register.getCodeEmployee());
            procedure.setDate(4, new java.sql.Date(register.getDateEvent().getTime()));
            procedure.setTime(5, new java.sql.Time(register.getHourEvent().getTime()));
            procedure.setString(6, register.getPlaceEvent());
            procedure.execute();
            servicesHasEmployeesList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ServiceHasEmployeeSearch() {

        FilteredList<Services_has_Employees> filter = new FilteredList<>(servicesHasEmployeesList, e -> true);

        txtSearchSHE.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Services_has_Employees predicateSHE) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateSHE.getServices_codeService()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateSHE.getCodeService()).toLowerCase().contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateSHE.getCodeEmployee()).toLowerCase().contains(searchKey)) {
                    return true;
                } else if(String.valueOf(predicateSHE.getDateEvent()).toLowerCase().contains(searchKey)){
                    return true;
                } else if(String.valueOf(predicateSHE.getHourEvent()).toLowerCase().contains(searchKey)){
                    return true;
                } else if(predicateSHE.getPlaceEvent().toLowerCase().contains(searchKey)){
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Services_has_Employees> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblServicesHasEmployees.comparatorProperty());
        tblServicesHasEmployees.setItems(sortList);
    }
    
    public void minimize() {
        Stage stage = (Stage) ServiceHasEmployeesPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    public void close() {
        System.exit(0);
    }   
    
    public void lockControls(){
        txtServices_ServiceId.setEditable(false);
        cmbCodeService.setDisable(true);
        cmbCodeEmployee.setDisable(true);
        date.setDisable(true);
        jfxHourEvent.setDisable(true);
        txtPlaceEvent.setEditable(false);
    }
    
    public void unlockControls(){
        txtServices_ServiceId.setEditable(true);
        cmbCodeService.setDisable(false);
        cmbCodeEmployee.setDisable(false);
        date.setDisable(false);
        jfxHourEvent.setDisable(false);
        txtPlaceEvent.setEditable(true);
    }
    
    public void clearControls(){
        txtServices_ServiceId.clear();
        cmbCodeService.setValue(null);
        cmbCodeEmployee.setValue(null);
        date.selectedDateProperty().set(null);
        jfxHourEvent.setValue(null);
        txtPlaceEvent.clear();
    }
    
    public Principal getPrincipalStage() {
        return principalStage;
    }

    public void setPrincipalStage(Principal principalStage) {
        this.principalStage = principalStage;
    }
    
    public void menuPrincipal(){
            principalStage.principalWindow();
        }
    
}
