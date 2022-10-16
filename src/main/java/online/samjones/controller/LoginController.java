package online.samjones.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import online.samjones.database.UserDAO;
import online.samjones.model.User;
import online.samjones.util.AlertCase;
import online.samjones.util.Alerts;
import online.samjones.util.Localizer;
import online.samjones.util.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.stream.Collectors;

import static online.samjones.App.PACKAGE_STRING;


/**
 * Controller for login form view
 */
public class LoginController{
    @FXML
    private Button forgotButton;
    @FXML
    private Label localeLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordTextfield;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField usernameTextfield;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label welcomeSubtext;
    @FXML
    private Label zoneLabel;
    @FXML
    private Label problems;
    private final Validator validator = new Validator();
    private final UserDAO userDAO = new UserDAO();
    private final Logger logger = LogManager.getLogger(LoginController.class);

    /**
     * Controller initializer
     * Initializes form view with localized messages
     */
    public void initialize() {
        welcomeLabel.setText(Localizer.getLocalizedMessage("welcomeLabel"));
        welcomeSubtext.setText(Localizer.getLocalizedMessage("welcomeSubtext"));
        zoneLabel.setText(Localizer.getZone());
        localeLabel.setText(Localizer.getLocale());
        usernameLabel.setText(Localizer.getLocalizedMessage("usernameLabel"));
        passwordLabel.setText(Localizer.getLocalizedMessage("passwordLabel"));
        loginButton.setText(Localizer.getLocalizedMessage("loginButton"));
        forgotButton.setText(Localizer.getLocalizedMessage("forgotButton"));

        //Set validators
        validator.createCheck()
                .dependsOn("username", usernameTextfield.textProperty())
                .withMethod(c -> {
                    String username = usernameTextfield.getText().trim();
                    if (username.isBlank()){
                        c.error("Please enter a username");
                    }
                })
                .decorates(usernameTextfield);
        validator.createCheck()
                .dependsOn("password", passwordTextfield.textProperty())
                .withMethod(c -> {
                    String password = passwordTextfield.getText().trim();
                    if (password.isBlank()) {
                        c.error("Please enter a password");
                    }
                })
                .decorates(passwordTextfield);

        //Create error text label area
        StringBinding errorsText = Bindings.createStringBinding(this::getErrorText, validator.validationResultProperty());
        problems.textProperty().bind(errorsText);

    }

    /**
     * Returns the input errors as strings
     * @return String of errors in form fields
     */
    private String getErrorText() {
        return validator.validationResultProperty().get().getMessages().stream()
                .map(ValidationMessage::getText)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Checks login form for blank fields and calls non-UI associated method to verify fields
     */
    @FXML
    public void handleLoginButton(){
        //Perform validation
        if (validator.validate()){
            verifyLogin(usernameTextfield.getText(), passwordTextfield.getText());
        }
    }

    /**
     * Shows message with information about forgotten password
     */
    @FXML
    public void handleForgotButton(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Localizer.getLocalizedMessage("forgotPasswordTitle"));
        alert.setHeaderText(Localizer.getLocalizedMessage("forgotPasswordText"));
        alert.showAndWait();
    }

    /**
     * Checks if user exists in database and verifies password and launches application if user is verified.
     * Lambdas on 111 and 121 to process optional and check credentials
     * @param username String containing the username that was entered
     * @param password String containing the password that was entered
     */
    private void verifyLogin(String username, String password) {

        userDAO.getOne(username).ifPresentOrElse(
                //User is present
                (user) -> {
                    if (user.getPassword().equals(password)) {
                        logger.info(String.format(
                                "User %s (ID: %d) logged in successfully", user.getUserName(), user.getUserID()));
                        launchApplication(user);

                    } else {
                        Alerts.showAlert(AlertCase.PASSWORD, username);
                        logger.warn(String.format(
                                "User %s (ID: %d) attempted login unsuccessfully", user.getUserName(), user.getUserID()));
                        clearForm();
                    }
                },
                //No user was found
                () -> {
                    Alerts.showAlert(AlertCase.USERNAME, username);
                    logger.warn(String.format(
                            "Unknown user attempted login unsuccessfully. Attempted username: %s", username));
                    clearForm();

                }
        );
    }

    /**
     * Loads application FXML file and launches window.
     * Lambda on line 153 used to set behavior on window close request.
     * @param user logged in user
     */
    private void launchApplication(User user){

        SessionManager.getInstance().setCurrentUser(user);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PACKAGE_STRING + "fxml/main.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e){
            logger.fatal("Unable to load application window", e);
            Alerts.showAlert(AlertCase.GENERIC, "The application needs to close due to an error.");
            Platform.exit();
            return;
        }

        Stage stage = new Stage();
        stage.setTitle(Localizer.getLocalizedMessage("applicationTitle"));
        Scene scene = new Scene(root, 1100, 800);
        scene.getStylesheets().add(
                getClass().getResource(PACKAGE_STRING + "css/application.css").toExternalForm());
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(PACKAGE_STRING + "img/calendar.png")));
        stage.show();

        //Confirm quit when [X] button is used to close window
        stage.setOnCloseRequest(windowEvent -> {
            if(Alerts.showAlert(AlertCase.EXIT)){
                Platform.exit();
            } else {
                windowEvent.consume();
            }
        });

        //Close login window after successfully opening application.
        closeWindow();
    }

    /**
     * Closes login window.
     */
    private void closeWindow(){
        Stage stage = (Stage) usernameTextfield.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles key press events and checks for 'Enter'.
     * @param keyEvent Key event containing the key which was pressed
     */
    @FXML
    void handleKeyPressed(KeyEvent keyEvent){
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            handleLoginButton();
        }
    }

    void clearForm() {
        usernameTextfield.clear();
        passwordTextfield.clear();
    }
}
