package online.samjones.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Appointment object class
 */
public class Appointment implements Comparable<Appointment> {

    private final IntegerProperty appointmentId;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty location;
    private final StringProperty type;
    private LocalDateTime start;
    private LocalDateTime end;
    private final IntegerProperty customerId;
    private final IntegerProperty userId;
    private final IntegerProperty contactId;

    public Appointment(){
        this.appointmentId = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.type = new SimpleStringProperty();
        this.customerId = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
        this.contactId = new SimpleIntegerProperty();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(!(obj instanceof Appointment appointment)){
            return false;
        }
        return Objects.equals(appointment.getStart(), this.getStart())
                && Objects.equals(appointment.getEnd(), this.getEnd())
                && Objects.equals(appointment.getAppointmentId(), this.getAppointmentId())
                && Objects.equals(appointment.getTitle(), this.getTitle())
                && Objects.equals(appointment.getDescription(), this.getDescription())
                && Objects.equals(appointment.getLocation(), this.getLocation())
                && Objects.equals(appointment.getType(), this.getType())
                && Objects.equals(appointment.getContactId(), this.getContactId())
                && Objects.equals(appointment.getCustomerId(), this.getCustomerId())
                && Objects.equals(appointment.getUserId(), this.getUserId());
    }

    @Override
    public int compareTo(Appointment o) {
        return o.getStart().compareTo(this.start);
    }

    public int getAppointmentId() {
        return appointmentId.get();
    }

    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getCustomerId() {
        return customerId.get();
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public int getContactId() {
        return contactId.get();
    }

    public IntegerProperty contactIdProperty() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId.set(contactId);
    }

    @Override
    public String toString() {
        return this.getTitle() + " "
                + this.getDescription() + " "
                + this.getType() + " "
                + this.getLocation();
    }
}
