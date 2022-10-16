package online.samjones.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import online.samjones.database.AppointmentDAO;
import online.samjones.database.ContactDAO;
import online.samjones.model.Appointment;
import online.samjones.model.Contact;
import online.samjones.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

/**
 * Controller for appointments by context report view.
 */
public class AppointmentByContactController{

    @FXML
    private ComboBox<Contact> contactComboBox;
    @FXML
    private TextArea reportTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button closeButton;
    @FXML
    private Label headerLabel;
    private final ContactDAO contactDAO = new ContactDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final Logger logger = LogManager.getLogger(AppointmentByContactController.class);

    /**
     * Initializes contact combo box and sets listener to update text area.
     * Lambda used to add listener to combo box on line 56.
     */
    public void initialize() {

        //Localize
        contactComboBox.setPromptText(Localizer.getLocalizedMessage("selectContactPrompt"));
        headerLabel.setText(Localizer.getLocalizedMessage("labelCustomerAppointmentsContact"));
        saveButton.setText(Localizer.getLocalizedMessage("saveButton"));
        closeButton.setText(Localizer.getLocalizedMessage("closeButton"));

        //Initialize contact combo box
        contactComboBox.setItems(FXCollections.observableArrayList(contactDAO.getAll()));

        //Listener creates report and populates text area when contact is selected
        contactComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            int id = newValue.getContactID();
            List<Appointment> contactAppointments = appointmentDAO.getAll().stream()
                    .filter(appointment -> appointment.getContactId() == id).toList();
            StringBuilder sb = new StringBuilder();
            for(Appointment appointment : contactAppointments){
                sb.append(Localizer.getLocalizedMessage("appointmentDetailsMessage"))
                        .append(appointment.getAppointmentId()).append("\n");
                sb.append(Localizer.getLocalizedMessage("appointmentTitle"))
                        .append(": ").append(appointment.getTitle()).append("\n");
                sb.append(Localizer.getLocalizedMessage("appointmentDescription"))
                        .append(": ").append(appointment.getDescription()).append("\n");
                sb.append(Localizer.getLocalizedMessage("appointmentLocation"))
                        .append(": ").append(appointment.getLocation()).append("\n");
                sb.append(Localizer.getLocalizedMessage("appointmentType"))
                        .append(": ").append(appointment.getType()).append("\n");
                sb.append(appointment.getStart().format(TimeUtility.DATE_ONLY_FORMATTER)).append("\n");
                sb.append(appointment.getStart().format(TimeUtility.TIME_ONLY_FORMATTER));
                sb.append(" - ");
                sb.append(appointment.getEnd().format(TimeUtility.TIME_ONLY_FORMATTER)).append("\n");
                sb.append(Localizer.getLocalizedMessage("appointmentUser"))
                        .append(": ").append(appointment.getUserId());
                sb.append("\n").append(Localizer.getLocalizedMessage("appointmentCustomer"))
                        .append(": ").append(appointment.getCustomerId());
                sb.append("\n\n");
            }
            reportTextArea.setText(sb.toString());
        });

    }

    /**
     * Closes window
     */
    @FXML
    void closeWindow(){
        Stage stage = (Stage) reportTextArea.getScene().getWindow();
        stage.close();
    }

    /**
     * Saves contents of text area to file.
     */
    @FXML
    void handleSaveToFile(){

        //Open file chooser that allows user to select where to save report file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Save");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TEXT", "*.txt"),
                new FileChooser.ExtensionFilter("ALL FILES", "*.*")
        );
        File saveFile = fileChooser.showSaveDialog(reportTextArea.getScene().getWindow());

        //Will be null if user clicked cancel
        if(saveFile == null){
            return;
        }

        //Create list of text and iterator
        ObservableList<CharSequence> paragraph = reportTextArea.getParagraphs();
        Iterator<CharSequence> iter = paragraph.iterator();

        //Iterate over text area and write to file
        try(BufferedWriter bw = Files.newBufferedWriter(saveFile.toPath(), StandardOpenOption.CREATE)){
            while(iter.hasNext()){
                CharSequence sequence = iter.next();
                bw.append(sequence);
                bw.newLine();
            }
            bw.flush();
        } catch(IOException e){
            logger.error("Unable to write contact report to file.", e);
            Alerts.showAlert(AlertCase.GENERIC, "Could not write file.");
        }

    }
}
