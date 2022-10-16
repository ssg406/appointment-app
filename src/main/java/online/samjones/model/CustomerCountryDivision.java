package online.samjones.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * CustomerCountryDivision objects represent the result of an SQL JOIN operation
 * on the Customer, Country, and FirstLevelDivision tables and model data necessary for
 * the customers table view.
 */
public class CustomerCountryDivision {
    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final StringProperty address;
    private final StringProperty postalCode;
    private final StringProperty phone;
    private final IntegerProperty divisionId;
    private final StringProperty division;
    private final IntegerProperty countryId;
    private final StringProperty country;

    public CustomerCountryDivision(){
        this.customerId = new SimpleIntegerProperty();
        this.customerName = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.postalCode = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.divisionId = new SimpleIntegerProperty();
        this.division = new SimpleStringProperty();
        this.countryId = new SimpleIntegerProperty();
        this.country = new SimpleStringProperty();
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

    public int getDivisionId() {
        return divisionId.get();
    }

    public IntegerProperty divisionIdProperty() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId.set(divisionId);
    }

    public String getDivision() {
        return division.get();
    }

    public StringProperty divisionProperty() {
        return division;
    }

    public void setDivision(String division) {
        this.division.set(division);
    }

    public int getCountryId() {
        return countryId.get();
    }

    public IntegerProperty countryIdProperty() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId.set(countryId);
    }

    public String getCountry() {
        return country.get();
    }

    public StringProperty countryProperty() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    @Override
    public String toString() {
        return this.getCustomerName() + " "
                + this.getAddress() + " "
                + this.getPostalCode() + " "
                + this.getDivision() + " "
                + this.getCountry() + " "
                + this.getPhone();
    }
}

