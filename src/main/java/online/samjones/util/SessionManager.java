package online.samjones.util;

import online.samjones.model.Appointment;
import online.samjones.model.CustomerCountryDivision;
import online.samjones.model.User;

/**
 * Utility class manages user session and passes limited data between controllers
 */
public class SessionManager {

    private User currentUser;
    private boolean modifyingAppointment;
    private boolean modifyingCustomer;
    private Appointment selectedAppointment;
    private CustomerCountryDivision selectedCustomerCountryDivision;
    private static SessionManager INSTANCE = null;

    private SessionManager(){}

    public static SessionManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    /**
     * Retrieves the currently logged in user.
     * @return Returns the User that is currently logged in
     */
    public User getCurrentUser(){
        return currentUser;
    }

    /**
     * Records the currently logged in user.
     * @param user User object to set as currently logged in
     */
    public void setCurrentUser(User user){
        currentUser = user;
    }

    /**
     * Notifies appointment form controller if the user is modifying an appointment.
     * @return Returns true if appointment is selected and modify button was pushed.
     */
    public boolean isModifyingAppointment(){ return modifyingAppointment; }

    /**
     * Notifies customer form controller if the user is modifying a customer.
     * @return Returns true if a customer is selected and modify button was pushed.
     */
    public boolean isModifyingCustomer(){ return modifyingCustomer; }

    /**
     * @return currently selected appointment in table
     */
    public Appointment getSelectedAppointment() { return selectedAppointment; }

    /**
     * Retrieves the customer object that has been selected in the table view.
     * @return currently selected object in customers table
     */
    public CustomerCountryDivision getSelectedCustomerCountryDivision() { return selectedCustomerCountryDivision; }

    /**
     * Records if the user is currently modifying an appointment.
     * @param modifyingAppointment True if an appointment is selected to be modified
     */
    public void setModifyingAppointment(boolean modifyingAppointment) {
        this.modifyingAppointment = modifyingAppointment;
    }

    /**
     * Records if the user is currently modifying a customer.
     * @param modifyingCustomer True if a customer is selected to be modified
     */
    public void setModifyingCustomer(boolean modifyingCustomer) {
        this.modifyingCustomer = modifyingCustomer;
    }

    /**
     * Records the selected appointment object in the table view
     * @param selectedAppointment The appointment currently selected in the table view
     */
    public void setSelectedAppointment(Appointment selectedAppointment) {
        this.selectedAppointment = selectedAppointment;
    }

    /**
     * Records the currently selected customer in the table view
     * @param selectedCustomerCountryDivision The selected customer in the table view
     */
    public void setSelectedCustomerCountryDivision(CustomerCountryDivision selectedCustomerCountryDivision) {
        this.selectedCustomerCountryDivision = selectedCustomerCountryDivision;
    }

    /**
     * Clears selected appointment/customer and sets modifying flags to false. Performed at logout and after
     * forms are closed.
     */
    public void cleanUp(){
        selectedAppointment = null;
        selectedCustomerCountryDivision = null;
        modifyingAppointment = false;
        modifyingCustomer = false;
    }

}

