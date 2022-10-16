package online.samjones.util;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

import static online.samjones.App.PACKAGE_STRING;

/**
 * Utility class that provides generic and preset alert functionality. Preset alerts provide
 * localized messages.
 */
public abstract class Alerts {

    /**
     * Shows an alert based on the alert type and given parameter.
     * @param alertCase The alert type given as a type of the enumeration AlertCase
     * @param alertParam The string containing the alert text or other parameter if a preset alert.
     * @return Returns true if the user selects "OK" on a confirmation alert or if the alert required no confirmation.
     */
    public static boolean showAlert(AlertCase alertCase, String alertParam){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result;
        final Image applicationIcon = new Image(
                Alerts.class.getResourceAsStream(PACKAGE_STRING + "img/calendar.png"));

        //Create and size alert graphics
        final ImageView errorIcon = new ImageView(new Image(
                Alerts.class.getResourceAsStream(PACKAGE_STRING + "img/error.png")));
        errorIcon.setFitHeight(48);
        errorIcon.setFitWidth(48);


        final ImageView deleteIcon = new ImageView(new Image(
                Alerts.class.getResourceAsStream(PACKAGE_STRING + "img/delete.png")));
        deleteIcon.setFitHeight(48);
        deleteIcon.setFitWidth(48);

        final ImageView infoIcon = new ImageView(new Image(
                Alerts.class.getResourceAsStream(PACKAGE_STRING + "img/info.png")));
        infoIcon.setFitHeight(48);
        infoIcon.setFitWidth(48);

        //Set dialog stylesheet and icon
        DialogPane dialogPane = alert.getDialogPane();
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.initStyle(StageStyle.UNIFIED);
        dialogStage.getIcons().add(applicationIcon);


        switch(alertCase){
            case USERNAME -> {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle(Localizer.getLocalizedMessage("usernameErrorTitle"));
                alert.setHeaderText(Localizer.getLocalizedMessage("usernameError", alertParam));
                alert.getDialogPane().setGraphic(errorIcon);
                alert.showAndWait();
                return true;
            }
            case PASSWORD -> {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle(Localizer.getLocalizedMessage("passwordErrorTitle"));
                alert.setHeaderText(Localizer.getLocalizedMessage("passwordError", alertParam));
                alert.getDialogPane().setGraphic(errorIcon);
                alert.showAndWait();
                return true;
            }
            case EXIT -> {
                alert.setAlertType(Alert.AlertType.CONFIRMATION);
                alert.setTitle(Localizer.getLocalizedMessage("confirmQuitTitle"));
                alert.setHeaderText(Localizer.getLocalizedMessage("confirmQuit"));
                alert.getDialogPane().setGraphic(infoIcon);
                result = alert.showAndWait();
                return result.isPresent() && result.get() == ButtonType.OK;

            }
            case DELETE -> {
                alert.setAlertType(Alert.AlertType.CONFIRMATION);
                alert.setTitle(Localizer.getLocalizedMessage("confirmDeleteTitle"));
                String alertText = Localizer.getLocalizedMessage("confirmDelete") + " " + alertParam + "?";
                alert.setHeaderText(alertText);
                alert.getDialogPane().setGraphic(deleteIcon);
                result = alert.showAndWait();
                return result.isPresent() && result.get() == ButtonType.OK;
            }
            case BLANK_FIELD -> {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle(Localizer.getLocalizedMessage("fieldRequiredTitle"));
                alert.setHeaderText(Localizer.getLocalizedMessage("fieldRequired", alertParam));
                alert.getDialogPane().setGraphic(errorIcon);
                alert.showAndWait();
            }
            case NO_SELECTION -> {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle(Localizer.getLocalizedMessage("noSelectionTitle"));
                alert.setHeaderText(Localizer.getLocalizedMessage("noSelection", alertParam));
                alert.getDialogPane().setGraphic(errorIcon);
                alert.showAndWait();
            }
            case GENERIC -> {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(alertParam);
                alert.getDialogPane().setGraphic(errorIcon);
                alert.showAndWait();
            }
            case APPOINTMENT_ALERT -> {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle(Localizer.getLocalizedMessage("upcomingAppointmentsTitle"));
                alert.setHeaderText(Localizer.getLocalizedMessage("upcomingAppointments") + alertParam);
                alert.getDialogPane().setGraphic(infoIcon);
                alert.showAndWait();
            }
            case INFORMATION -> {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(alertParam);
                alert.getDialogPane().setGraphic(infoIcon);
                alert.showAndWait();
            }
            case DELETE_EXISTING -> {
                alert.setAlertType(Alert.AlertType.CONFIRMATION);
                alert.setTitle(Localizer.getLocalizedMessage("deleteExistingAppointmentsTitle"));
                alert.setHeaderText(alertParam);
                alert.getDialogPane().setGraphic(deleteIcon);
                result = alert.showAndWait();
                return result.isPresent() && result.get() == ButtonType.OK;
            }
            default -> throw new IllegalArgumentException("Invalid alert type");
        }
        return false;
    }


    /**
     * Calls showAlert with empty string parameter. Overloaded method allows preset alerts to be called that do not
     * accept a string parameter.
     * @param alertCase type of alert given as AlertCase enumeration
     * @return Returns true if user selected OK or if alert required no confirmation
     */
    public static boolean showAlert(AlertCase alertCase){
        return showAlert(alertCase, "");
    }
}