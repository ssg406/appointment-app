package online.samjones.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import online.samjones.database.*;
import online.samjones.model.Country;
import online.samjones.model.Customer;
import online.samjones.model.FirstLevelDivision;
import online.samjones.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the customer form view
 */
public class CustomerController {

    @FXML
    private TextField addressTextField;
    @FXML
    private ComboBox<Country> countryComboBox;
    @FXML
    private Label customerIDLabel;
    @FXML
    private TextField customerNameTextField;
    @FXML
    private Label customerWindowHeading;
    @FXML
    private ComboBox<FirstLevelDivision> divisionComboBox;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField postalCodeTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private Label customerCountryLabel;
    @FXML
    private Label customerDivisionLabel;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerPhoneLabel;
    @FXML
    private Label customerPostalCodeLabel;
    @FXML
    private Button submitButton;
    private Customer selectedCustomer;
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CustomerCountryDivisionDAO customerCountryDivisionDAO = new CustomerCountryDivisionDAO();
    private final FirstLevelDivisionDAO firstLevelDivisionDAO = new FirstLevelDivisionDAO();
    private final CountryDAO countryDAO = new CountryDAO();
    private final Logger logger = LogManager.getLogger(CustomerController.class);


    /**
     * Initializes form combo boxes and fields with values if modifying customer.
     * Lambda on line 80 to add combo box listener
     * Lambdas on 90, 98, 101 to process optionals and set combo boxes
     */

    public void initialize() {

        localizeForm();

        //Set combo box items
        countryComboBox.setItems(FXCollections.observableArrayList(
                countryDAO.getAll()
        ));
        divisionComboBox.setItems(FXCollections.observableArrayList(
                firstLevelDivisionDAO.getAll()
        ));

        //Set listener that updates division box when country is changed
        countryComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            //set division box with list of division by country ID from newValue.getCountryID();
            int id = newValue.getCountryID();
            divisionComboBox.setItems(FXCollections.observableArrayList(
                    Objects.requireNonNull(firstLevelDivisionDAO.getAll()).stream()
                            .filter(fld -> fld.getCountryID() == id).toList()
            ));
        });

        //check if modifying customer and then initialize fields accordingly
        if(SessionManager.getInstance().isModifyingCustomer()){
            customerDAO.getOne(SessionManager.getInstance().getSelectedCustomerCountryDivision().getCustomerId())
                    .ifPresent(customer -> selectedCustomer = customer);

            customerIDLabel.setText("ID: " + selectedCustomer.getCustomerID());
            customerNameTextField.setText(selectedCustomer.getCustomerName());
            addressTextField.setText(selectedCustomer.getAddress());
            postalCodeTextField.setText(selectedCustomer.getPostalCode());
            phoneNumberTextField.setText(selectedCustomer.getPhone());
            firstLevelDivisionDAO.getOne(
                    selectedCustomer.getDivisionID()).ifPresent(fld -> {
                divisionComboBox.setValue(fld);
                countryDAO.getOne(fld.getCountryID())
                        .ifPresent(c -> countryComboBox.setValue(c));
            });

        }
    }

    /**
     * Localizes form labels to French or English.
     */
    private void localizeForm(){
        if(SessionManager.getInstance().isModifyingCustomer()){
            customerWindowHeading.setText(Localizer.getLocalizedMessage("modifyCustomerHeader"));
        } else {
            customerWindowHeading.setText(Localizer.getLocalizedMessage("addCustomerHeader"));
        }
        customerNameLabel.setText(Localizer.getLocalizedMessage("customerName"));
        customerAddressLabel.setText(Localizer.getLocalizedMessage("customerAddress"));
        customerPostalCodeLabel.setText(Localizer.getLocalizedMessage("customerPostalCode"));
        customerPhoneLabel.setText(Localizer.getLocalizedMessage("customerPhone"));
        customerCountryLabel.setText(Localizer.getLocalizedMessage("customerCountry"));
        customerDivisionLabel.setText(Localizer.getLocalizedMessage("customerDivision"));
        submitButton.setText(Localizer.getLocalizedMessage("submitButton"));
        cancelButton.setText(Localizer.getLocalizedMessage("cancelButton"));
    }

    /**
     * Closes window and fires window close event to trigger listener in main controller
     * that updates tables and cleans up session manager.
     */
    @FXML
    void closeWindow() {
        Window window = addressTextField.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Calls non-UI associated method to process form.
     */
    @FXML
    void handleSubmitCustomer() {
        processForm();
    }

    /**
     * Collects form fields and validates
     */
    private void processForm(){

        Customer customer = new Customer();

        //Check for empty fields and set corresponding object field
        String customerName = customerNameTextField.getText().trim();
        if(customerName.isEmpty()){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "customer name");
            return;
        }
        customer.setCustomerName(customerName);

        String customerAddress = addressTextField.getText().trim();
        if(customerAddress.isEmpty()){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "address");
            return;
        }
        customer.setAddress(customerAddress);

        String postalCode = postalCodeTextField.getText().trim();
        if(postalCode.isEmpty()){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "postal code");
            return;
        }
        customer.setPostalCode(postalCode);

        String phoneNumber = phoneNumberTextField.getText().trim();
        if(phoneNumber.isEmpty()){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "phone number");
            return;
        }
        customer.setPhone(phoneNumber);

        FirstLevelDivision firstLevelDivision = divisionComboBox.getSelectionModel().getSelectedItem();
        if(firstLevelDivision == null){
            Alerts.showAlert(AlertCase.BLANK_FIELD, "division");
            return;
        }
        customer.setDivisionID(firstLevelDivision.getDivisionID());

        if(SessionManager.getInstance().isModifyingCustomer()){
            customer.setCustomerID(SessionManager.getInstance().getSelectedCustomerCountryDivision().getCustomerId());
            if(!customerDAO.update(customer)){
                Alerts.showAlert(AlertCase.GENERIC, Localizer.getLocalizedMessage("unableModifyCustomer"));
                logger.error(String.format("Could not modify customer ID %d", customer.getCustomerID()));
                return;
            }
        } else {
            if(!customerDAO.add(customer)){
                Alerts.showAlert(AlertCase.GENERIC, Localizer.getLocalizedMessage("unableAddCustomer"));
                logger.error(String.format("Unable to add customer %s to database", customer.getCustomerName()));
                return;
            }
        }
        closeWindow();
    }

    /**
     * Handles key press events and checks for ENTER key to process form
     * @param keyEvent Key event containing which key the user pressed
     */
    @FXML
    void handleKeyPressed(KeyEvent keyEvent){

        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            processForm();
        }
    }
}

