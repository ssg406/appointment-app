package online.samjones.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import online.samjones.database.AppointmentDAO;
import online.samjones.model.Appointment;
import online.samjones.util.Localizer;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Controller for appointment by month and type view
 */
public class AppointmentsByMonthAndTypeController{

    @FXML
    public PieChart appointmentPieChart;
    @FXML
    public Label pieChartDataLabel;
    @FXML
    private Label noChartDataLabel;
    @FXML
    private ComboBox<String> appointmentMonthComboBox;
    @FXML
    private Label headerLabel;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();



    /**
     * Initializes combo box and chart
     * Lambda on line 50 to add listener to combo box, line 53 to process stream
     */
    public void initialize() {

        //Localize
        headerLabel.setText(Localizer.getLocalizedMessage("labelCustomerAppointmentsTypeMonth"));
        appointmentMonthComboBox.setPromptText(Localizer.getLocalizedMessage("month"));

        //Initialize values of month selection combo box
        ObservableList<String> months = FXCollections.observableArrayList("JANUARY", "FEBRUARY", "MARCH", "APRIL",
                "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
        appointmentMonthComboBox.setItems(months);

        //Add combo box listener
        appointmentMonthComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {

            //Generate a map from data
            Map<String, Long> map = appointmentDAO.getAll().stream()
                    .filter(appointment -> appointment.getStart().getMonth().toString().equals(newValue))
                    .collect(groupingBy(Appointment::getType, Collectors.counting()));

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            //Populate the chart with data if it exists, populate label with data
            if(map.size() > 0){
                noChartDataLabel.setText("");
                StringBuilder dataLabelText = new StringBuilder();
                for(String key : map.keySet()){
                    pieChartData.add(
                            new PieChart.Data(key, map.get(key))
                    );
                    dataLabelText.append(key).append(": ").append(map.get(key)).append("     ");
                }
                pieChartDataLabel.setText(dataLabelText.toString());
            } else {
                pieChartDataLabel.setText("");
                noChartDataLabel.setText(Localizer.getLocalizedMessage("noDataLabel"));
            }
            appointmentPieChart.getData().setAll(pieChartData);
        });
        appointmentPieChart.setLegendVisible(false);

    }
}
