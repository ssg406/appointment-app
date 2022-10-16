package online.samjones.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Customer object class
 */
public class Customer {

    private final IntegerProperty customerID;
    private final StringProperty customerName;
    private final StringProperty address;
    private final StringProperty postalCode;
    private final StringProperty phone;
    private final IntegerProperty divisionID;

    public Customer() {
        this.customerID = new SimpleIntegerProperty();
        this.customerName = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.postalCode = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.divisionID = new SimpleIntegerProperty();
    }

    public int getCustomerID() {
        return customerID.get();
    }

    public IntegerProperty customerIDProperty() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public int getDivisionID() {
        return divisionID.get();
    }

    public IntegerProperty divisionIDProperty() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID.set(divisionID);
    }

    @Override
    public String toString() {
        return customerName.get();
    }
}