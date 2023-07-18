package org.juangalicia.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.juangalicia.bean.Company;
import org.juangalicia.db.Conexion; 
import org.juangalicia.main.Principal;
import javafx.collections.FXCollections; 
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; 
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.juangalicia.report.GenerateReport;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;


public class CompanyController implements Initializable {
    private enum operations {
        CREATE, SAVE, DELETE, UPDATE, CANCEL, NONE
    };
    
    private operations typeOfOperation = operations.NONE;
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
    @FXML
    private JFXButton btnRead;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnGeneralReport;
    @FXML
    private ImageView imgCreate;
    @FXML
    private ImageView imgRead;
    @FXML
    private ImageView imgUpdate;
    @FXML
    private ImageView imgDelete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
    }
 
    public void loadData() {
        tblCompanys.setItems(getCompany());
        colCompanyId.setCellValueFactory(new PropertyValueFactory<Company, Integer>("codeCompany"));
        colCompanyName.setCellValueFactory(new PropertyValueFactory<Company, String>("nameCompany"));
        colCompanyAdress.setCellValueFactory(new PropertyValueFactory<Company, String>("adress"));
        colCompanyPhone.setCellValueFactory(new PropertyValueFactory<Company, String>("phone")); 
    }

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

    public void create() {
        switch (typeOfOperation) {
            case NONE:
                clearControls();
                unlockControls();
                btnCreate.setText("Save");
                btnUpdate.setText("Cancel");
                btnDelete.setDisable(true);
                btnRead.setDisable(true);
                btnGeneralReport.setDisable(true);
                imgCreate.setImage(new Image("/org/juangalicia/image/save.png"));
                imgUpdate.setImage(new Image("/org/juangalicia/image/cancel.png"));
                typeOfOperation = operations.SAVE;
                loadData();
                break;

            case SAVE:
                save();
                clearControls();
                lockControls();
                btnCreate.setText("Create Company");
                btnUpdate.setText("Update Company");
                btnDelete.setDisable(false);
                btnRead.setDisable(false);
                btnGeneralReport.setDisable(false);
                imgCreate.setImage(new Image("/org/juangalicia/image/create.png"));
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                typeOfOperation = operations.NONE;
                loadData();
                break;
        }
    }

    public void save() {
        Company register = new Company();
        register.setNameCompany(txtCompanyName.getText());
        register.setAdress(txtCompanyAdress.getText());
        register.setPhone(txtCompanyPhone.getText());
        try {
            PreparedStatement procedure = Conexion.getInsance().getConexion()
                    .prepareCall("call sp_CreateCompany(?,?,?)");
            procedure.setString(1, register.getNameCompany());
            procedure.setString(2, register.getAdress());
            procedure.setString(3, register.getPhone());
            procedure.execute();
            companyList.add(register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void edit() {
        switch (typeOfOperation) {
            case NONE:
                if (tblCompanys.getSelectionModel().getSelectedItem() != null) {
                    btnCreate.setDisable(true);
                    btnRead.setDisable(true);
                    btnGeneralReport.setDisable(true);
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
                btnCreate.setText("Create Company");
                btnUpdate.setText("Update Company");
                btnDelete.setDisable(false);
                btnRead.setDisable(false);
                btnGeneralReport.setDisable(false);
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
                btnGeneralReport.setDisable(false);
                btnUpdate.setText("Update Company");
                btnDelete.setText("Delete Company");
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
                    .prepareCall("call sp_UpdateCompany(?,?,?,?)");
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

    public void delete() {
        switch (typeOfOperation) {
            case UPDATE:
                update();
                clearControls();
                lockControls();
                btnCreate.setDisable(false);
                btnRead.setDisable(false);
                btnGeneralReport.setDisable(false);
                btnUpdate.setText("Update Company");
                btnDelete.setText("Delete Company");
                imgUpdate.setImage(new Image("/org/juangalicia/image/update.png"));
                imgDelete.setImage(new Image("/org/juangalicia/image/delete.png"));
                loadData();
                typeOfOperation = operations.NONE;
                break;

            default:
                if (tblCompanys.getSelectionModel().getSelectedItem() != null) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Delete Company");
                    alert.setHeaderText("Are you sure of deleting this register? You are gonna delete a foreign key");
                    alert.setContentText("Choose your option.");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        try {
                            PreparedStatement procedure = Conexion.getInsance().getConexion()
                                    .prepareCall("call sp_DeleteCompany(?)");
                            procedure.setInt(1,
                                    ((Company) tblCompanys.getSelectionModel().getSelectedItem()).getCodeCompany());
                            procedure.execute();
                            companyList.remove(tblCompanys.getSelectionModel().getSelectedItem());
                            clearControls();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("You should select an element");
                    alert.showAndWait();
                }
        }
    }

    public void selectElement() {
        txtCompanyId.setText(
                String.valueOf(((Company) tblCompanys.getSelectionModel().getSelectedItem()).getCodeCompany()));
        txtCompanyName.setText(((Company) tblCompanys.getSelectionModel().getSelectedItem()).getNameCompany());
        txtCompanyAdress.setText(((Company) tblCompanys.getSelectionModel().getSelectedItem()).getAdress());
        txtCompanyPhone.setText(((Company) tblCompanys.getSelectionModel().getSelectedItem()).getPhone());
    }

    public void generateReport() {
        switch (typeOfOperation) {
            case NONE:
                printReport();
                break;
        }
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
        Map parametros = new HashMap();
        int codEmpresa = Integer.valueOf(((Company)tblCompanys.getSelectionModel().getSelectedItem()).getCodeCompany());
        parametros.put("codEmpresa", codEmpresa);
        
        
        File fichero2 = new File("src/org/juangalicia/report");
        String path2 = fichero2.getAbsolutePath();
        parametros.put("SUBREPORT_DIR", path2);
        GenerateReport.showReport("generalReport.jasper", "General Report", parametros);
    }
    
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
       
    public void minimize() {
        Stage stage = (Stage) companyPane.getScene().getWindow();
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

    public void lockControls() {
        txtCompanyId.setEditable(false);
        txtCompanyName.setEditable(false);
        txtCompanyAdress.setEditable(false);
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

    public Principal getPrincipalStage() {
        return principalStage;
    }

    public void setPrincipalStage(Principal principalStage) {
        this.principalStage = principalStage;
    }

    public void menuPrincipal() {
        principalStage.principalWindow();
    } 

    public void budgetWindow() {
        principalStage.budgetWindow();
    }
}
