package online.samjones.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import online.samjones.util.Localizer;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controller for log viewer window
 */
public class LogViewerController{

    @FXML
    private TextArea accessLogTextArea;
    @FXML
    private Button backButton;


    /**
     * Initializes text areas with content from log files.
     */
    public void initialize() {

        //Localize
        backButton.setText(Localizer.getLocalizedMessage("closeButton"));

        //Write to text fields
        Path logFile = FileSystems.getDefault().getPath("scheduler_app.log");

        try(BufferedReader logReader = Files.newBufferedReader(logFile)){
            String line;
            StringBuilder textContents = new StringBuilder();

            //Read access log
            while((line = logReader.readLine()) != null){
                textContents.append(line).append("\n");
            }
            accessLogTextArea.setText(textContents.toString());
            textContents.setLength(0);


        } catch(IOException e){
            accessLogTextArea.setText(Localizer.getLocalizedMessage("noLogAccess"));
        }
    }

    /**
     * Closes window
     */
    @FXML
    void closeWindow(){
        Stage stage = (Stage) accessLogTextArea.getScene().getWindow();
        stage.close();
    }
}
