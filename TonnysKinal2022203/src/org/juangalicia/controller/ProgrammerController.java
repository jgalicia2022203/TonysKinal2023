package org.juangalicia.controller;

import java.awt.Desktop;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;

import org.juangalicia.main.Principal;

import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ProgrammerController implements Initializable {
    private Principal principalStage;

    
    @FXML 
    private AnchorPane programmerPane;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    
    public void linkedin(){
        if (java.awt.Desktop.isDesktopSupported()){
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    java.net.URI uri = new java.net.URI("https://www.linkedin.com/in/juan-galicia-a20191281/");
                    desktop.browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void github(){
        if (java.awt.Desktop.isDesktopSupported()){
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    java.net.URI uri = new java.net.URI("https://github.com/Tonelito");
                    desktop.browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }    
    
    public void close(){
        principalStage.principalWindow();
    }
    
    public void minimize() {
        Stage stage = (Stage) programmerPane.getScene().getWindow();
        stage.setIconified(true);
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
