package online.samjones.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import online.samjones.database.*;
import online.samjones.model.Appointment;
import online.samjones.model.CustomerCountryDivision;
import online.samjones.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static online.samjones.App.PACKAGE_STRING;

/**
 * Controller for main window view
 */
public class MainController{
    public static final String APPOINTMENT_FXML = "fxml/appointment.fxml";
    public static final String CUSTOMER_FXML = "fxml/customer.fxml";
    public static final String LOGIN_FXML = "fxml/login.fxml";
    public static final String APPOINTMENT_VOLUME_FXML = "fxml/appointmentvolume.fxml";
    public static final String APPOINTMENT_CONTACT_FXML = "fxml/appointmentbycontact.fxml";
    public static final String APPOINTMENT_MONTH_TYPE = "fxml/appointmentbymonthtype.fxml";
    public static final String LOG_VIEWER_FXML = "fxml/logviewer.fxml";
    public static final String ABOUT_DIALOG_FXML = "fxml/aboutdialog.fxml";
    @FXML
    private ToggleGroup timeFilterToggleGroup;
    @FXML
    private RadioButton allRadio;
    @FXML
    private RadioButton thisMonthRadio;
    @FXML
    private RadioButton thisWeekRadio;
    @FXML
    private TableView<Appointment> appointmentsTableView;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentContactColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitle;
    @FXML
    private TableColumn<Appointment, String> appointmentDescription;
    @FXML
    private TableColumn<Appointment, String> appointmentLocation;
    @FXML
    private TableColumn<Appointment, String> appointmentType;
    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerId;
    @FXML
    private TableColumn<Appointment, Integer> appointmentUserId;
    @FXML
    private TableView<CustomerCountryDivision> customersTableView;
    @FXML
    private Label loggedInAsLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Tab appointmentTab;
    @FXML
    private Tab customerTab;
    @FXML
    private Tab reportTab;
    @FXML
    private Button appointmentAddButton;
    @FXML
    private Button appointmentModifyButton;
    @FXML
    private Button appointmentDeleteButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button customerAddButton;
    @FXML
    private Button customerModifyButton;
    @FXML
    private Button customerDeleteButton;
    @FXML
    private Label appointmentsTypeMonthLabel;
    @FXML
    private Label appointmentsContactLabel;
    @FXML
    private Label appointmentsVolumeLabel;
    @FXML
    private Button viewReportButtonTop;
    @FXML
    private Button viewReportButtonMiddle;
    @FXML
    private Button viewReportButtonBottom;
    @FXML
    private MenuItem newAppointmentMenuItem;
    @FXML
    private MenuItem newCustomerMenuItem;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private MenuItem showLogsMenuItem;
    @FXML
    private Label allCustomersLabel;
    @FXML
    private Menu fileMenu;
    @FXML
    private Menu helpMenu;
    @FXML
    private Label filterAppointmentsLabel;
    @FXML
    private TableColumn<CustomerCountryDivision, String> customerAddressColumn;
    @FXML
    private TableColumn<CustomerCountryDivision, String> customerPostalCodeColumn;
    @FXML
    private TableColumn<CustomerCountryDivision, String> customerPhoneColumn;
    @FXML
    private TableColumn<CustomerCountryDivision, String> customerDivisionColumn;
    @FXML
    private TableColumn<CustomerCountryDivision, String> customerCountryColumn;
    @FXML
    private TableColumn<CustomerCountryDivision, String> customerNameColumn;
    @FXML
    private TextField appointmentSearchField;
    @FXML
    private TextField customerSearchField;
    private Predicate<Appointment> allAppointments;
    private Predicate<Appointment> thisWeekAppointments;
    private Predicate<Appointment> thisMonthAppointments;
    private FilteredList<Appointment> appointmentFilteredList;
    private FilteredList<CustomerCountryDivision> customerFilteredList;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ContactDAO contactDAO = new ContactDAO();
    private final UserDAO userDAO = new UserDAO();
    private final CustomerCountryDivisionDAO customerCountryDivisionDAO = new CustomerCountryDivisionDAO();
    private final Logger logger = LogManager.getLogger(MainController.class);

    /**
     * Initializes appointment and customer tables with data and checks for user appointments
     * Lambdas used to define predicates and add toggle group listener
     */
    public void initialize(){

        localizeView();
        updateTableData();
        initTableViews();
        initSearchFields();

        //Check for upcoming appointments and display alert
        String appointmentString = checkForUserAppointments();
        if (appointmentString == null) {
            Alerts.showAlert(AlertCase.INFORMATION, Localizer.getLocalizedMessage("noUpcomingAppointments"));
        } else {
            Alerts.showAlert(AlertCase.APPOINTMENT_ALERT, appointmentString);
        }


        //Define predicates to filter appointment table
        allAppointments = appointment -> true;
        thisWeekAppointments = appointment -> appointment.getStart().isBefore(
                LocalDateTime.now().plusDays(7)) && appointment.getStart().isAfter(LocalDateTime.now());
        thisMonthAppointments = appointment -> appointment.getStart().isBefore(
                LocalDateTime.now().plusMonths(1)) && appointment.getStart().isAfter(LocalDateTime.now());

        //Listener for appointment time filter radio buttons
        timeFilterToggleGroup.selectedToggleProperty().addListener(((observableValue, toggle, t1) -> {
            if(allRadio.isSelected()){
                appointmentFilteredList.setPredicate(allAppointments);
            } else if (thisWeekRadio.isSelected()){
                appointmentFilteredList.setPredicate(thisWeekAppointments);
            }else if (thisMonthRadio.isSelected()){
                appointmentFilteredList.setPredicate(thisMonthAppointments);
            } else {
                appointmentFilteredList.setPredicate(allAppointments);
            }
        }));
    }

    /**
     * Initializes search fields to set predicates for filtered lists in customer and appointment tables.
     */
    void initSearchFields() {
        appointmentSearchField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            appointmentFilteredList.setPredicate(appointment -> appointment.toString().toLowerCase().contains(newValue));
        }));

        customerSearchField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            customerFilteredList.setPredicate(customer -> customer.toString().toLowerCase().contains(newValue));
        }));
    }

    /**
     * Localizes labels to system language
     */
    private void localizeView(){

        //Menu Items
        fileMenu.setText(Localizer.getLocalizedMessage("fileMenu"));
        helpMenu.setText(Localizer.getLocalizedMessage("helpMenu"));
        newCustomerMenuItem.setText(Localizer.getLocalizedMessage("menuNewCustomer"));
        newAppointmentMenuItem.setText(Localizer.getLocalizedMessage("menuNewAppointment"));
        quitMenuItem.setText(Localizer.getLocalizedMessage("menuQuit"));
        aboutMenuItem.setText(Localizer.getLocalizedMessage("menuAbout"));
        showLogsMenuItem.setText(Localizer.getLocalizedMessage("menuShowLogs"));

        //Tab titles
        appointmentTab.setText(Localizer.getLocalizedMessage("appointmentsTab"));
        customerTab.setText(Localizer.getLocalizedMessage("customersTab"));
        reportTab.setText(Localizer.getLocalizedMessage("reportsTab"));

        //Radio button labels
        filterAppointmentsLabel.setText(Localizer.getLocalizedMessage("filterAppointmentsLabel"));
        thisWeekRadio.setText(Localizer.getLocalizedMessage("thisWeekRadio"));
        thisMonthRadio.setText(Localizer.getLocalizedMessage("thisMonthRadio"));
        allRadio.setText(Localizer.getLocalizedMessage("allRadio"));

        //Appointment buttons
        appointmentAddButton.setText(Localizer.getLocalizedMessage("buttonAdd"));
        appointmentModifyButton.setText(Localizer.getLocalizedMessage("buttonModify"));
        appointmentDeleteButton.setText(Localizer.getLocalizedMessage("buttonDelete"));

        //Customers tab header
        allCustomersLabel.setText(Localizer.getLocalizedMessage("labelAllCustomers"));

        //Customer buttons
        customerAddButton.setText(Localizer.getLocalizedMessage("buttonAdd"));
        customerModifyButton.setText(Localizer.getLocalizedMessage("buttonModify"));
        customerDeleteButton.setText(Localizer.getLocalizedMessage("buttonDelete"));

        //Logout button
        logoutButton.setText(Localizer.getLocalizedMessage("buttonLogout"));

        //View report buttons
        viewReportButtonTop.setText(Localizer.getLocalizedMessage("buttonViewReport"));
        viewReportButtonMiddle.setText(Localizer.getLocalizedMessage("buttonViewReport"));
        viewReportButtonBottom.setText(Localizer.getLocalizedMessage("buttonViewReport"));

        //Report titles
        appointmentsTypeMonthLabel.setText(Localizer.getLocalizedMessage("labelCustomerAppointmentsTypeMonth"));
        appointmentsContactLabel.setText(Localizer.getLocalizedMessage("labelCustomerAppointmentsContact"));
        appointmentsVolumeLabel.setText(Localizer.getLocalizedMessage("labelCustomerAppointmentsVolumeMonth"));

        //Set logged in as label
        loggedInAsLabel.setText(Localizer.getLocalizedMessage("labelLoggedInAs",
                SessionManager.getInstance().getCurrentUser().getUserName()));

        //Appointment Table Headers
        appointmentTitle.setText(Localizer.getLocalizedMessage("appointmentTitle"));
        appointmentDescription.setText(Localizer.getLocalizedMessage("appointmentDescription"));
        appointmentLocation.setText(Localizer.getLocalizedMessage("appointmentLocation"));
        appointmentType.setText(Localizer.getLocalizedMessage("appointmentType"));
        appointmentContactColumn.setText(Localizer.getLocalizedMessage("appointmentContact"));
        appointmentStartColumn.setText(Localizer.getLocalizedMessage("appointmentStart"));
        appointmentEndColumn.setText(Localizer.getLocalizedMessage("appointmentEnd"));
        appointmentUserId.setText(Localizer.getLocalizedMessage("appointmentUser"));
        appointmentCustomerId.setText(Localizer.getLocalizedMessage("appointmentCustomer"));

        //Customer Table Headers
        customerNameColumn.setText(Localizer.getLocalizedMessage("customerName"));
        customerAddressColumn.setText(Localizer.getLocalizedMessage("customerAddress"));
        customerPostalCodeColumn.setText(Localizer.getLocalizedMessage("customerPostalCode"));
        customerPhoneColumn.setText(Localizer.getLocalizedMessage("customerPhone"));
        customerCountryColumn.setText(Localizer.getLocalizedMessage("customerCountry"));
        customerDivisionColumn.setText(Localizer.getLocalizedMessage("customerDivision"));
    }



    /**
     * Handles key press events and checks for defined keyboard shortcuts
     * @param keyEvent fired key press event
     */
    @FXML
    private void handleKeyboardShortcuts(KeyEvent keyEvent) {

        final KeyCodeCombination newAppointmentKeys = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_ANY);
        final KeyCodeCombination newCustomerKeys = new KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_ANY);
        final KeyCodeCombination logoutKeys = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_ANY);

        if(newAppointmentKeys.match(keyEvent)){
            handleAddAppointment();
        } else if(newCustomerKeys.match(keyEvent)){
            handleAddCustomer();
        } else if(logoutKeys.match(keyEvent)){
            handleLogout();
        }

    }

    /**
     * Handles add appointment button
     */
    @FXML
    public void handleAddAppointment() {
        SessionManager.getInstance().setModifyingAppointment(false);
        launchAppointmentForm();
    }

    /**
     * Launches log viewer window
     */
    @FXML
    public void handleLaunchLogViewer(){
        Stage stage = windowLoader(LOG_VIEWER_FXML,
                "Application Logs", 800, 600);
        if(stage == null){
            return;
        }
        stage.initOwner(customersTableView.getScene().getWindow());
        stage.show();
    }

    /**
     * Handles add customer button
     */
    @FXML
    public void handleAddCustomer() {
        SessionManager.getInstance().setModifyingCustomer(false);
        launchCustomerForm();
    }

    /**
     * Handles delete appointment button. Checks for no selection
     */
    @FXML
    public void handleDeleteAppointment() {
        Appointment selectedItem = appointmentsTableView.getSelectionModel().getSelectedItem();

        //Check for no selection
        if(selectedItem == null){
            Alerts.showAlert(AlertCase.NO_SELECTION, "appointment");
            return;
        }
        //Confirm delete
        String appointmentString = "ID " +  selectedItem.getAppointmentId() + " " +
                selectedItem.getType() + " " + selectedItem.getTitle();
        if(!Alerts.showAlert(AlertCase.DELETE, appointmentString)){
            return;
        }
        //Check if database was able to delete
        if(!appointmentDAO.delete(selectedItem.getAppointmentId())){
            Alerts.showAlert(AlertCase.GENERIC, Localizer.getLocalizedMessage("unableDeleteAppointment"));
            logger.error(String.format(
                    "Unable to delete appointment ID: %d. Check database connection.", selectedItem.getAppointmentId()));
        }
        updateTableData();

    }

    /**
     * Handles delete customer button. Checks for no selection.
     */
    @FXML
    public void handleDeleteCustomer() {

        CustomerCountryDivision selectedItem = customersTableView.getSelectionModel().getSelectedItem();

        //Check for no selection
        if(selectedItem == null){
            Alerts.showAlert(AlertCase.NO_SELECTION, "customer");
            return;
        }
        //Check for no existing appointments
        if(!checkForNoCustomerAppointments(selectedItem)){
            return;
        }
        //Confirm delete
        if(!Alerts.showAlert(AlertCase.DELETE, selectedItem.getCustomerName())){
            return;
        }
        //Confirm database was able to delete
        if(!customerDAO.delete(selectedItem.getCustomerId())){
            Alerts.showAlert(AlertCase.GENERIC,Localizer.getLocalizedMessage("unableDeleteCustomer"));
            logger.error(String.format(
                    "Unable to delete customer ID: %d. Check database connection.", selectedItem.getCustomerId()));
        }
        updateTableData();

    }

    /**
     * Checks for existing customer appointments and deletes upon user request.
     * Lambda used in forEach statement
     * Lambda originally on line 393 simplified with method reference
     * @param selectedItem the selected customer object from the table view
     * @return returns true if selected customer has no appointments scheduled
     */
    public boolean checkForNoCustomerAppointments(CustomerCountryDivision selectedItem){
        //Create list of customer appointments
        List<Appointment> customerAppointments = appointmentDAO.getAll().stream()
                .filter(appointment -> appointment.getCustomerId() == selectedItem.getCustomerId())
                .toList();
        if(customerAppointments.isEmpty()){
            return true;
        } else {
            //Generate string of appointments to show user
            StringBuilder apptString = new StringBuilder();
            List<Integer> appointmentIds = new ArrayList<>();

            apptString.append(Localizer.getLocalizedMessage("listRemainingAppointments",
                    selectedItem.getCustomerName())).append("\n");
            customerAppointments.forEach(appointment -> {
                apptString.append(appointment.getTitle()).append("   ");
                apptString.append(appointment.getStart().format(TimeUtility.DATE_ONLY_FORMATTER)).append(" ");
                apptString.append(appointment.getStart().format(TimeUtility.TIME_ONLY_FORMATTER)).append(" - ");
                apptString.append(appointment.getEnd().format(TimeUtility.TIME_ONLY_FORMATTER));
                apptString.append("\n");
                appointmentIds.add(appointment.getAppointmentId());
            });

            //Display existing appointments in alert and off
            boolean confirmDelete = Alerts.showAlert(AlertCase.DELETE_EXISTING, apptString.toString());
            if(confirmDelete){
                appointmentIds.forEach(appointmentDAO::delete);
                return true;
            }
            return false;
        }
    }

    /**
     * Launches appointment volume by month view
     */
    @FXML
    public void handleGenerateAppointmentVolumeByMonthReport() {
        Stage stage = windowLoader(APPOINTMENT_VOLUME_FXML,
                Localizer.getLocalizedMessage("labelCustomerAppointmentsVolumeMonth"), 800, 600);
        if(stage == null){
            return;
        }
        stage.initOwner(customersTableView.getScene().getWindow());
        stage.show();
    }

    /**
     * Launches appointment by contact report view
     */
    @FXML
    public void handleGenerateAppointmentsByContactReport() {
        Stage stage = windowLoader(APPOINTMENT_CONTACT_FXML,
                Localizer.getLocalizedMessage("labelCustomerAppointmentsContact"), 800, 600);
        if(stage == null){
            return;
        }
        stage.initOwner(customersTableView.getScene().getWindow());
        stage.show();
    }

    /**
     * Shows window containing appointments by month report view
     */
    @FXML
    public void handleGenerateAppointmentsByMonthReport() {
        Stage stage = windowLoader(APPOINTMENT_MONTH_TYPE,
                Localizer.getLocalizedMessage("labelCustomerAppointmentsTypeMonth"), 800, 600);
        if(stage == null){
            return;
        }
        stage.initOwner(customersTableView.getScene().getWindow());
        stage.show();
    }

    /**
     * Sets SessionManager fields for modification and launches appointment form.
     */
    @FXML
    public void handleModifyAppointment() {

        Appointment selectedItem = appointmentsTableView.getSelectionModel().getSelectedItem();
        if(selectedItem == null){
            Alerts.showAlert(AlertCase.NO_SELECTION, "appointment");
        } else {
            SessionManager.getInstance().setModifyingAppointment(true);
            SessionManager.getInstance().setSelectedAppointment(selectedItem);
            launchAppointmentForm();
        }
    }

    /**
     * Sets SessionManager fields for modification and launches customer form.
     */
    @FXML
    public void handleModifyCustomer() {
        CustomerCountryDivision selectedItem = customersTableView.getSelectionModel().getSelectedItem();
        if(selectedItem == null){
            Alerts.showAlert(AlertCase.NO_SELECTION, "customer");
        } else {
            SessionManager.getInstance().setModifyingCustomer(true);
            SessionManager.getInstance().setSelectedCustomerCountryDivision(selectedItem);
            launchCustomerForm();
        }
    }

    /**
     * Cleans up session manager and returns to log in screen without exiting application
     */
    @FXML
    public void handleLogout(){

        //clean up session manager
        SessionManager.getInstance().cleanUp();
        SessionManager.getInstance().setCurrentUser(null);

        Stage stage = windowLoader(
                LOGIN_FXML, Localizer.getLocalizedMessage("applicationTitle"), 600, 400);
        if(stage == null){
            return;
        }
        Window window = customersTableView.getScene().getWindow();
        window.hide();
        stage.show();
    }


    /**
     * Launches modal add/modify appointment form window.
     * Lambda used to set behavior in window close request
     */
    private void launchAppointmentForm(){
        String windowTitle = SessionManager.getInstance().isModifyingAppointment() ?
                Localizer.getLocalizedMessage("modifyAppointmentHeader") :
                Localizer.getLocalizedMessage("addAppointmentHeader");
        Stage stage = windowLoader(APPOINTMENT_FXML, windowTitle, 650, 550);
        if(stage == null){
            return;
        }
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(customersTableView.getScene().getWindow());
        stage.show();
        //Handle user closing with [X] button
        stage.setOnCloseRequest(windowEvent -> {
            SessionManager.getInstance().cleanUp();
            updateTableData();
        });
    }

    /**
     * Launches modal add/modify customer form window.
     * Lambda used to set behavior on window close request
     */
    private void launchCustomerForm(){
        String windowTitle = SessionManager.getInstance().isModifyingCustomer() ?
                Localizer.getLocalizedMessage("modifyCustomerHeader") :
                Localizer.getLocalizedMessage("addCustomerHeader");
        Stage stage = windowLoader(CUSTOMER_FXML, windowTitle, 650, 500);
        if(stage == null){
            return;
        }
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(customersTableView.getScene().getWindow());
        stage.show();
        //Handle user closing with [X] button
        stage.setOnCloseRequest(windowEvent -> {
            SessionManager.getInstance().cleanUp();
            updateTableData();
        });
    }

    /**
     * Sets TableView Cell factories for appointment table.
     * Lambda used to set cell to display contact name instead of numeric ID
     */
    private void initTableViews(){

        //Cell Factories to format date and time columns, greys out times that are in the past
        appointmentStartColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime localDateTime, boolean empty) {
                super.updateItem(localDateTime, empty);
                if(empty){
                    setText("");
                } else {
                    if(localDateTime.isBefore(LocalDateTime.now())){
                        setTextFill(Color.GRAY);
                    }
                    setText(localDateTime.format(TimeUtility.TABLE_FORMATTER));
                }
            }
        });
        appointmentEndColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime localDateTime, boolean empty) {
                super.updateItem(localDateTime, empty);
                if(empty){
                    setText("");
                } else {
                    if(localDateTime.isBefore(LocalDateTime.now())){
                        setTextFill(Color.GRAY);
                    }
                    setText(localDateTime.format(TimeUtility.TABLE_FORMATTER));
                }
            }
        });
        //Cell factory that displays contact name instead of contact ID
        appointmentContactColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer integer, boolean empty) {
                super.updateItem(integer, empty);
                if(empty){
                    setText("");
                } else {
                    contactDAO.getOne(integer).ifPresent(contact -> setText(contact.getContactName()));
                }
            }
        });

        appointmentCustomerId.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer integer, boolean empty) {
                super.updateItem(integer, empty);
                if(empty) {
                    setText("");
                } else {
                    customerDAO.getOne(integer).ifPresent(customer -> setText(customer.getCustomerName()));
                }
            }
        });

        appointmentUserId.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer integer, boolean empty) {
                super.updateItem(integer, empty);
                if(empty){
                    setText("");
                } else {
                    userDAO.getOne(integer).ifPresent(user -> setText(user.getUserName()));
                }
            }
        });

    }

    /**
     * Method updates TableViews in background threads in case of large numbers of database records.
     * Lambda used to set progress bar behavior.
     */
    public void updateTableData() {

        //Create task
        Task<ObservableList<Appointment>> appointmentsTask = new Task<>() {
            @Override
            protected ObservableList<Appointment> call() {
                //Wrap appointment data in filtered list and add "all" predicate
                appointmentFilteredList = new FilteredList<>(FXCollections.observableArrayList(
                        appointmentDAO.getAll()), allAppointments);

                //Wrap filtered list in sorted list and bind table's comparator property to sorted list's
                //to enable column header sorting on click
                SortedList<Appointment> appointmentSortedList = new SortedList<>(appointmentFilteredList, Comparator.comparing(Appointment::getStart));
                appointmentSortedList.comparatorProperty().bind(appointmentsTableView.comparatorProperty());

                return appointmentSortedList;
            }
        };

        //Bind table view items with value of task
        appointmentsTableView.itemsProperty().bind(appointmentsTask.valueProperty());

        //Progress bar shows loading progress and is hidden on success of task
        progressBar.progressProperty().bind(appointmentsTask.progressProperty());
        progressBar.setVisible(true);

        appointmentsTask.setOnRunning(e -> appointmentsTableView.setPlaceholder(new Label("Loading Data...")));
        appointmentsTask.setOnSucceeded(e -> progressBar.setVisible(false));

        new Thread(appointmentsTask).start();

        //Customers table task
        Task<ObservableList<CustomerCountryDivision>> customersTask = new Task<>() {
            @Override
            protected ObservableList<CustomerCountryDivision> call() {

                customerFilteredList = new FilteredList<>(FXCollections.observableArrayList(
                        customerCountryDivisionDAO.getAll()
                ), customer -> true);

                //Wrap in sorted list and bind comparator property to table view's to enable sorting
                //on column header click
                SortedList<CustomerCountryDivision> customerSortedList = new SortedList<>(
                        customerFilteredList, Comparator.comparing(CustomerCountryDivision::getCustomerName));
                customerSortedList.comparatorProperty().bind(customersTableView.comparatorProperty());
                return customerSortedList;
            }
        };

        //Bind customers table items with customer's task value
        customersTableView.itemsProperty().bind(customersTask.valueProperty());
        customersTask.setOnRunning(e -> customersTableView.setPlaceholder(new Label("Loading Data...")));
        new Thread(customersTask).start();

    }

    /**
     * Searches database for appointments belonging to the current user that are within
     * the next 15 minutes. Displays alert with appointment information.
     * Lambdas used in stream operations and in forEach statement.
     */
    private String checkForUserAppointments(){

        //Generate list of appointments for the current user that are within 15 minutes
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> userAppointments = appointmentDAO.getAll().stream()
                .filter(appointment -> appointment.getUserId() == SessionManager.getInstance().getCurrentUser().getUserID())
                .filter(appointment -> appointment.getStart().isBefore(now.plusMinutes(15))
                        && appointment.getStart().isAfter(now))
                .toList();

        //No appointments coming up
        if(userAppointments.isEmpty()){
            return null;
        }
        //Create string of upcoming appointments if they are in the list
        StringBuilder sb = new StringBuilder("\n");
        userAppointments
                .forEach(appointment -> {
                    sb.append("ID: ").append(appointment.getAppointmentId());
                    sb.append(" ").append(appointment.getTitle());
                    sb.append(" ").append(appointment.getStart().format(TimeUtility.DATE_ONLY_FORMATTER));
                    sb.append(" ");
                    sb.append(appointment.getStart().format(TimeUtility.TIME_ONLY_FORMATTER)).append(" ");
                    sb.append(Localizer.getZone());
                    sb.append("\n");
                });
        return sb.toString();


    }

    /**
     * Loads FXML file and returns stage object.
     * Utility method that saves code in various launcher methods.
     * @param fxml String containing FXML file location
     * @param title String containing title to add to window
     * @param width Window width in pixels
     * @param height Window height in pixels
     * @return Stage object created from FXML file and given title and dimensions
     */
    public Stage windowLoader(String fxml, String title, int width, int height){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PACKAGE_STRING + fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            Scene scene = new Scene(root, width, height);
            stage.getIcons().add(new Image(getClass().getResourceAsStream(PACKAGE_STRING + "img/calendar.png")));
            stage.setScene(scene);
            return stage;
        } catch (IOException e) {
            Alerts.showAlert(AlertCase.GENERIC, Localizer.getLocalizedMessage("cannotLoadWindow"));
            logger.error(String.format("Failed to load %s window.", title), e);
            return null;
        }
    }

    /**
     * Exits main window after confirmation
     */
    @FXML
    void handleExit(){
        boolean confirmed = Alerts.showAlert(AlertCase.EXIT);
        if(confirmed){
            Platform.exit();
        }
    }

    /**
     * Launches about dialog box
     */
    @FXML
    void handleShowAbout(){
        Stage stage = windowLoader(ABOUT_DIALOG_FXML, Localizer.getLocalizedMessage("menuAbout"), 600, 325);
        if(stage == null){
            return;
        }
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(customersTableView.getScene().getWindow());
        stage.show();
    }

    /**
     * Listens for delete when appointment table has focus
     * @param keyEvent The key event containing which key was pressed
     */
    @FXML
    void handleKeyPressedAppointment(KeyEvent keyEvent){
        if(keyEvent.getCode().equals(KeyCode.DELETE)){
            handleDeleteAppointment();
        }
    }

    /**
     * Listens for delete when customer table has focus
     * @param keyEvent The key event containing which key was pressed
     */
    @FXML
    void handleKeyPressedCustomer(KeyEvent keyEvent){
        if(keyEvent.getCode().equals(KeyCode.DELETE)){
            handleDeleteCustomer();
        }
    }

    /**
     *Clear appointment search field and restore table
     */
    @FXML
    void clearAppointmentSearch(){
        appointmentSearchField.clear();
        updateTableData();
    }

    /**
     * Clear customer search field and restore table
     */
    @FXML
    void clearCustomerSearch() {
        customerSearchField.clear();
        updateTableData();
    }

}

