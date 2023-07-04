package org.juangalicia.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.juangalicia.main.Principal;

import javafx.fxml.Initializable;

public class ProgrammerController implements Initializable {
    private Principal principalStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
