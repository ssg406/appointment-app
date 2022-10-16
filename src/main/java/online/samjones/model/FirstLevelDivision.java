package online.samjones.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * First Level Division object class
 */
public class FirstLevelDivision {

    private final IntegerProperty divisionID;
    private final StringProperty division;
    private final IntegerProperty countryID;

    public FirstLevelDivision(){
        this.division = new SimpleStringProperty();
        this.divisionID = new SimpleIntegerProperty();
        this.countryID = new SimpleIntegerProperty();
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

    public String getDivision() {
        return division.get();
    }

    public StringProperty divisionProperty() {
        return division;
    }

    public void setDivision(String division) {
        this.division.set(division);
    }

    public int getCountryID() {
        return countryID.get();
    }

    public IntegerProperty countryIDProperty() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID.set(countryID);
    }

    @Override
    public String toString() {
        return division.get();
    }
}
