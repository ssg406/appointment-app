package online.samjones.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Shows small window with text about the application.
 * Content is contained within FXML file.
 */
public class AboutDialogController{

    @FXML
    private VBox aboutVBox;


    public void initialize() {

    }

    @FXML
    void closeWindow(){
        Stage stage = (Stage) aboutVBox.getScene().getWindow();
        stage.close();
    }
}

