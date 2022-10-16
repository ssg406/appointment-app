package online.samjones.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import online.samjones.database.*;
import online.samjones.model.Appointment;
import online.samjones.model.Contact;
import online.samjones.model.Customer;
import online.samjones.model.User;
import online.samjones.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for appointment form view
 */
public class AppointmentController{

    @FXML
    private Label appointmentIDLabel;
    @FXML
    private Label appointmentWindowHeading;
    @FXML
    private ComboBox<Contact> contactComboBox;
    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField typeTextField;
    @FXML
    private ComboBox<LocalTime> startTimeComboBox;
    @FXML
    private ComboBox<LocalTime> endTimeComboBox;
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private ComboBox<User> userComboBox;
    @FXML
    private Label appointmentContactLabel;
    @FXML
    private Label appointmentCustomerLabel;
    @FXML
    private Label appointmentDateLabel;
    @FXML
    private Label appointmentDescriptionLabel;
    @FXML
    private Label appointmentEndLabel;
    @FXML
    private Label appointmentLocationLabel;
    @FXML
    private Label appointmentStartLabel;
    @FXML
    private Label appointmentTitleLabel;
    @FXML
    private Label appointmentTypeLabel;
    @FXML
    private Label appointmentUserLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;
    private Appointment selectedAppointment;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ContactDAO contactDAO = new ContactDAO();
    private final UserDAO userDAO = new UserDAO();
    private final Logger logger = LogManager.getLogger(AppointmentController.class);


    /**
     * Initializes form fields and combo boxes and creates cell factory for date picker.
     * Lambdas used line 130-135 to set combo box values.
     */
    public void initialize() {

        localizeForm();

        //Generate list of times for combo boxes
        ObservableList<LocalTime> appointmentTimes = FXCollections.observableArrayList();
        //Get local business open and close hours
        int open = TimeUtility.getLocalBusinessOpenHour();
        int close = TimeUtility.getLocalBusinessCloseHour();

        //Generate LocalTime object for every 5 minutes during business hours
        for(int i = open; i < close; i++){
            //minutes 0-59 in increments of 5
            for (int j = 0; j < 60; j += 5) {
                appointmentTimes.add(LocalTime.of(i,j));
            }
        }
        startTimeComboBox.setItems(appointmentTimes);
        endTimeComboBox.setItems(appointmentTimes);
        contactComboBox.setItems(FXCollections.observableArrayList(contactDAO.getAll()).sorted());
        customerComboBox.setItems(FXCollections.observableArrayList(customerDAO.getAll()).sorted());
        userComboBox.setItems(FXCollections.observableArrayList(userDAO.getAll()).sorted());

        //Disable dates before today

        dateDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate localDate, boolean empty) {
                super.updateItem(localDate, empty);
                setDisable(empty || localDate.isBefore(LocalDate.now()));
            }
        });

        //Populate form fields with values if modifying appointment
        if(SessionManager.getInstance().isModifyingAppointment()){

            selectedAppointment = SessionManager.getInstance().getSelectedAppointment();
            if(selectedAppointment == null){
                Alerts.showAlert(AlertCase.GENERIC, Localizer.getLocalizedMessage("errorEditingAppointment"));
                logger.error("Failed to edit appointment");
                return;
            }
            appointmentIDLabel.setText("ID: " +
                    selectedAppointment.getAppointmentId());
            titleTextField.setText(selectedAppointment.getTitle());
            descriptionTextField.setText(selectedAppointment.getDescription());
            locationTextField.setText(selectedAppointment.getLocation());
            typeTextField.setText(selectedAppointment.getType());
            dateDatePicker.setValue(selectedAppointment.getStart().toLocalDate());
            startTimeComboBox.setValue(selectedAppointment.getStart().toLocalTime());
            endTimeComboBox.setValue(selectedAppointment.getEnd().toLocalTime());

            userDAO.getOne(selectedAppointment.getUserId()).ifPresent(
                    user -> userComboBox.setValue(user));
            contactDAO.getOne(selectedAppointment.getContactId()).ifPresent(
                    contact -> contactComboBox.setValue(contact));
            customerDAO.getOne(selectedAppointment.getCustomerId()).ifPresent(
                    customer -> customerComboBox.setValue(customer));
        }

    }

    private void localizeForm(){
        if(SessionManager.getInstance().isModifyingAppointment()){
            appointmentWindowHeading.setText(Localizer.getLocalizedMessage("modifyAppointmentHeader"));
        } else{
            appointmentWindowHeading.setText(Localizer.getLocalizedMessage("addAppointmentHeader"));
        }

        appointmentTitleLabel.setText(Localizer.getLocalizedMessage("appointmentTitle"));
        appointmentDescriptionLabel.setText(Localizer.getLocalizedMessage("appointmentDescription"));
        appointmentLocationLabel.setText(Localizer.getLocalizedMessage("appointmentLocation"));
        appointmentTypeLabel.setText(Localizer.getLocalizedMessage("appointmentType"));
        appointmentContactLabel.setText(Localizer.getLocalizedMessage("appointmentContact"));
        appointmentDateLabel.setText(Localizer.getLocalizedMessage("appointmentDate"));
        appointmentStartLabel.setText(Localizer.getLocalizedMessage("appointmentStart"));
        appointmentEndLabel.setText(Localizer.getLocalizedMessage("appointmentEnd"));
        appointmentCustomerLabel.setText(Localizer.getLocalizedMessage("appointmentCustomer"));
        appointmentUserLabel.setText(Localizer.getLocalizedMessage("appointmentUser"));
        submitButton.setText(Localizer.getLocalizedMessage("submitButton"));
        cancelButton.setText(Localizer.getLocalizedMessage("cancelButton"));
    }

    /**
     * Fires window close request triggering event listener in main controller that will refresh tables
     * and clean up session manager
     */
    @FXML
    void closeWindow() {
        Window window = appointmentIDLabel.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Event handler for submit button that calls non-UI associated method to collect and validate data
     */
    @FXML
    void handleSubmitAppointment() {

        collectAndValidateForm();
    }

    /**
     * Processes form results to check for valid data and business logic.
     * Lambdas used line 236 onwards to process stream.
     * Lambdas used line 259 onwards to process stream.
     */
    void collectAndValidateForm(){

        Appointment appointment = new Appointment();

        //For each field, check if blank then set value in appointment object
        String title = titleTextField.getText().trim();
        if(title.isEmpty()){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "Title");
            return;
        }
        appointment.setTitle(title);

        String description = descriptionTextField.getText().trim();
        if(description.isEmpty()){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "Description");
            return;
        }
        appointment.setDescription(description);

        String type = typeTextField.getText().trim();
        if(type.isEmpty()){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "Type");
            return;
        }
        appointment.setType(type);

        String location = locationTextField.getText().trim();
        if(location.isEmpty()){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "Location");
            return;
        }
        appointment.setLocation(location);

        LocalDate date = dateDatePicker.getValue();
        if(date == null){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "Date");
            return;
        }

        LocalTime appointmentStartTime = startTimeComboBox.getSelectionModel().getSelectedItem();
        if( appointmentStartTime == null){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "Start Time");
            return;
        }

        LocalTime appointmentEndTime = endTimeComboBox.getSelectionModel().getSelectedItem();
        if(appointmentEndTime == null){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "End Time");
            return;
        }

        //Create start and end times to use in validation
        LocalDateTime start = LocalDateTime.of(date, appointmentStartTime);
        LocalDateTime end = LocalDateTime.of(date, appointmentEndTime);

        //Validate appointment start/end logic
        if(!(start.isBefore(end))){
            Alerts.showAlert(AlertCase.GENERIC, Localizer.getLocalizedMessage("apptStartBeforeEnd"));
            return;
        }


        //Set start and end if all checks passed
        appointment.setStart(start);
        appointment.setEnd(end);

        //Associate customer from combo box with appointment
        Customer customer = customerComboBox.getSelectionModel().getSelectedItem();
        if (customer == null){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "customer");
            return;
        }
        appointment.setCustomerId(customer.getCustomerID());

        //Check for conflicting appointments
        List<Appointment> customerAppointmentConflicts = new ArrayList<>();
        appointmentDAO.getAll().stream()
                .filter(appt -> appt.getCustomerId() == customer.getCustomerID())
                .forEach(existing -> {
                    if((existing.getStart().isAfter(start) || existing.getStart().isEqual(start))
                            && existing.getStart().isBefore(end)){
                        customerAppointmentConflicts.add(existing);
                    } else if(existing.getEnd().isAfter(start) &&
                            (existing.getEnd().isBefore(end) || existing.getEnd().isEqual(end))){
                        customerAppointmentConflicts.add(existing);
                    } else if((existing.getStart().isBefore(start) || existing.getStart().isEqual(start)) &&
                            (existing.getEnd().isAfter(end) || existing.getEnd().isEqual(end))){
                        customerAppointmentConflicts.add(existing);
                    }
                });
        //Remove the current appointment from the conflict list if modifying
        if(SessionManager.getInstance().isModifyingAppointment()){
            customerAppointmentConflicts.remove(selectedAppointment);
        }

        //Display the conflicting appointments and stop processing the form
        if(customerAppointmentConflicts.size() > 0){
            StringBuilder sb = new StringBuilder(Localizer.getLocalizedMessage("scheduleConflictMessage"));
            customerAppointmentConflicts.forEach(conflict -> {
                sb.append("\n").append(conflict.getTitle()).append(" on ");
                sb.append(conflict.getStart().format(TimeUtility.DATE_ONLY_FORMATTER)).append(" from ");
                sb.append(conflict.getStart().format(TimeUtility.TIME_ONLY_FORMATTER)).append(" to ");
                sb.append(conflict.getEnd().format(TimeUtility.TIME_ONLY_FORMATTER));
            });
            Alerts.showAlert(AlertCase.GENERIC, sb.toString());
            return;
        }

        //Finish setting Appointment object fields
        Contact contact = contactComboBox.getSelectionModel().getSelectedItem();
        if(contact == null){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "contact");
            return;
        }
        appointment.setContactId(contact.getContactID());

        User user = userComboBox.getSelectionModel().getSelectedItem();
        if(user == null){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "user");
            return;
        }
        appointment.setUserId(user.getUserID());

        //Add to database
        if (!SessionManager.getInstance().isModifyingAppointment()) {

            if(!appointmentDAO.add(appointment)){
                Alerts.showAlert(AlertCase.GENERIC, Localizer.getLocalizedMessage("errorAddingAppointment"));
                logger.error("Unable to add appointment to database. Please check database connectivity");
            }
        } else {
            appointment.setAppointmentId(selectedAppointment.getAppointmentId());
            if(!appointmentDAO.update(appointment)){
                Alerts.showAlert(AlertCase.GENERIC, Localizer.getLocalizedMessage("errorUpdatingAppointment"));
                logger.error(String.format("Unable to update appointment ID %d", selectedAppointment.getAppointmentId()));
            }
        }
        closeWindow();
    }
    @FXML
    void handleKeyPressed(KeyEvent keyEvent){
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            collectAndValidateForm();
        }
    }


}
