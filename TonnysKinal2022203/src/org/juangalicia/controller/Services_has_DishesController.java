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
import org.juangalicia.bean.Service;
import org.juangalicia.bean.Services_has_Dishes;
import org.juangalicia.db.Conexion;
import org.juangalicia.main.Principal;

public class Services_has_DishesController implements Initializable{
    private Principal principalStage;
    
    private enum operations{
        SAVE, DELETE, UPDATE, NONE
    }
    
    private operations typeOfOperation = operations.NONE;
    private ObservableList<Services_has_Dishes> servicesHasDishesList;
    private ObservableList<Service> serviceList;
    private ObservableList<Dish> dishList;
    
    @FXML
    private AnchorPane ServiceHasDishPane;
    @FXML
    private JFXTextField txtSearchServiceHasDish;
    @FXML
    private JFXButton btnCreate;
    @FXML
    private JFXButton btnCancel;
    @FXML 
    private JFXComboBox cmbCodeService;
    @FXML
    private JFXComboBox cmbCodeDish;
    @FXML
    private ImageView imgCreate;
    @FXML 
    private ImageView imgCancel;
    @FXML
    private TableColumn colServices_ServiceId;
    @FXML 
    private TableColumn colCodeService;
    @FXML 
    private TableColumn colCodeDish; 
    @FXML
    private TableView tblServicesHasDishes;
    @FXML 
    private JFXTextField txtServices_ServiceId;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        loadData();
        cmbCodeDish.setItems(getDish());
        cmbCodeService.setItems(getService());
    }
    
    public void loadData(){
        tblServicesHasDishes.setItems(getServicesHasDishes());
        colServices_ServiceId.setCellValueFactory(new PropertyValueFactory<Services_has_Dishes, Integer>("Services_codeService"));
        colCodeDish.setCellValueFactory(new PropertyValueFactory<Services_has_Dishes, Integer>("codeDish"));
        colCodeService.setCellValueFactory(new PropertyValueFactory<Services_has_Dishes, Integer>("codeService"));
    }
    
    public void SelectElement(){
        txtServices_ServiceId.setText(String.valueOf((((Services_has_Dishes)tblServicesHasDishes.getSelectionModel().getSelectedItem()).getServices_codeService())));
        cmbCodeDish.getSelectionModel().select(searchDish(((Services_has_Dishes)tblServicesHasDishes.getSelectionModel().getSelectedItem()).getCodeDish()));
        cmbCodeService.getSelectionModel().select(searchService(((Services_has_Dishes)tblServicesHasDishes.getSelectionModel().getSelectedItem()).getCodeService()));
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
    
    public ObservableList<Services_has_Dishes> getServicesHasDishes(){
        ArrayList<Services_has_Dishes> list = new ArrayList<Services_has_Dishes>();
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall("call sp_ReadServices_has_Dishes()");
            ResultSet result = procedure.executeQuery();
            while(result.next()){
                list.add(new Services_has_Dishes(result.getInt("Services_codeService"), result.getInt("codeDish"), result.getInt("codeService")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return servicesHasDishesList = FXCollections.observableArrayList(list);
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
    
    public ObservableList <Service> getService(){
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
                btnCreate.setText("Create Service has Dish");
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
                btnCreate.setText("Create Service has Dish");
                btnCancel.setText(" ");
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgCancel.setImage(null);
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }

    public void save(){
        Services_has_Dishes register = new Services_has_Dishes();
        register.setServices_codeService(Integer.parseInt(txtServices_ServiceId.getText()));
        register.setCodeDish(((Dish)cmbCodeDish.getSelectionModel().getSelectedItem()).getCodeDish());
        register.setCodeService(((Service)cmbCodeService.getSelectionModel().getSelectedItem()).getCodeCompany());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion().prepareCall(("call sp_CreateService_has_Dish(?,?,?)"));
            procedure.setInt(1, register.getServices_codeService());
            procedure.setInt(2, register.getCodeDish());
            procedure.setInt(3, register.getCodeService());
            procedure.execute();
            servicesHasDishesList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ServiceHasDishSearch() {

        FilteredList<Services_has_Dishes> filter = new FilteredList<>(servicesHasDishesList, e -> true);

        txtSearchServiceHasDish.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate((Services_has_Dishes predicateSHD) -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (String.valueOf(predicateSHD.getServices_codeService()).contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateSHD.getCodeDish()).toLowerCase().contains(searchKey)) {
                    return true;
                } else if (String.valueOf(predicateSHD.getCodeService()).toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Services_has_Dishes> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(tblServicesHasDishes.comparatorProperty());
        tblServicesHasDishes.setItems(sortList);
    }
    
    public void minimize() {
        Stage stage = (Stage) ServiceHasDishPane.getScene().getWindow();
        stage.setIconified(true);
    }
    
    public void close() {
        System.exit(0);
    }   
    
    public void lockControls(){
        txtServices_ServiceId.setEditable(false);
        cmbCodeDish.setDisable(true);
        cmbCodeService.setDisable(true);
    }
    
    public void unlockControls(){
        txtServices_ServiceId.setEditable(true);
        cmbCodeDish.setDisable(false);
        cmbCodeService.setDisable(false);
    }
    
    public void clearControls(){
        txtServices_ServiceId.clear();
        cmbCodeDish.setValue(null);
        cmbCodeService.setValue(null);
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
