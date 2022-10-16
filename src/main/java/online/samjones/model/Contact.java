package online.samjones.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Contact object class
 */
public class Contact {


    private final IntegerProperty contactID;
    private final StringProperty contactName;
    private final StringProperty email;

    public Contact() {
        this.contactID = new SimpleIntegerProperty();
        this.contactName = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
    }

    public int getContactID() {
        return contactID.get();
    }

    public IntegerProperty contactIDProperty() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID.set(contactID);
    }

    public String getContactName() {
        return contactName.get();
    }

    public StringProperty contactNameProperty() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName.set(contactName);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    @Override
    public String toString() {
        return contactName.get();
    }
}

