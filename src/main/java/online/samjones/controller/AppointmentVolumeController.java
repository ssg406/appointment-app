package online.samjones.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import online.samjones.database.AppointmentDAO;
import online.samjones.model.Appointment;
import online.samjones.util.Localizer;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;

/**
 * Controller for appointment volume by month report view
 */
public class AppointmentVolumeController{

    @FXML
    private NumberAxis numberAppointmentsAxis;
    @FXML
    private BarChart<String, Number> appointmentVolumeChart;
    private XYChart.Series<String, Number> appointmentChartData;
    @FXML
    private CategoryAxis monthAxis;
    @FXML
    private Label headerLabel;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    /**
     * Initializes chart with data from database
     * Lambda on line 53 in forEach statement
     * Lambdas originally on line 58 and 59 replaced with method references
     */

    public void initialize() {

        //Localize
        numberAppointmentsAxis.setLabel(Localizer.getLocalizedMessage("appointmentsTab"));
        monthAxis.setLabel(Localizer.getLocalizedMessage("month"));
        headerLabel.setText(Localizer.getLocalizedMessage("labelCustomerAppointmentsVolumeMonth"));

        //Add Months to category axis and set zero x-axis values
        ObservableList<String> months = FXCollections.observableArrayList("JANUARY", "FEBRUARY", "MARCH", "APRIL",
                "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
        appointmentChartData = new XYChart.Series<>(FXCollections.observableArrayList());
        months.forEach(month -> appointmentChartData.getData().add(new XYChart.Data<>(month, 0)));

        //Create y-axis data series from database by creating map from stream
        Map<Month, Long> map = appointmentDAO.getAll().stream()
                .map(Appointment::getStart)
                .collect(groupingBy(LocalDateTime::getMonth, Collectors.counting()));
        for(Month key : map.keySet()){
            appointmentChartData.getData().add(
                    new XYChart.Data<>(key.name(), map.get(key))
            );
        }

        //Set axis to display whole numbers only
        numberAppointmentsAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                if (number.intValue() != number.doubleValue()) {
                    return "";
                }
                return Integer.toString(number.intValue());
            }

            @Override
            public Number fromString(String s) {
                Number number = Double.parseDouble(s);
                return number.intValue();
            }
        });

        //Add data to chart
        appointmentVolumeChart.getData().add(appointmentChartData);
        appointmentVolumeChart.setLegendVisible(false);
    }
}