package online.samjones.database;

import online.samjones.model.Customer;
import online.samjones.util.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAO implements EntityDAO<Customer>{

    private final Logger logger = LogManager.getLogger(CustomerDAO.class);
    /**
     * Returns all customers from database
     * @return list of all customers
     */
    @Override
    public List<Customer> getAll() {
        List<Customer> customers  = new ArrayList<>();
        String query = "SELECT * FROM customers";

        try (Statement statement = DBConnection.getInstance().getConnection().createStatement();
             ResultSet results = statement.executeQuery(query)){
            while(results.next()){
                customers.add(generateCustomer(results));
            }
            return customers;
        } catch (SQLException e) {
            logger.error("Unable to retrieve customers from database", e);
            return null;
        }
    }

    /**
     * Finds customer by ID
     * @param id ID of customer
     * @return optional containing customer if found
     */
    @Override
    public Optional<Customer> getOne(int id) {
        String query = "SELECT * FROM customers WHERE Customer_ID = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            if (results.next()){
                return Optional.of(generateCustomer(results));
            }
        } catch(SQLException e){
            logger.error("Unable to retrieve contact from database", e);
        }
        return Optional.empty();
    }

    /**
     * Deletes customer from database
     * @param id id of customer to delete
     * @return Returns true if delete is successful
     */
    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM customers WHERE Customer_ID = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected == 1){
                return true;
            }
        }catch(SQLException e){
            logger.error("Unable to delete customer from database", e);
            return false;
        }
        return false;
    }

    /**
     * Adds customer to database
     * @param customer customer object
     * @return Returns true if insertion was successful
     */
    @Override
    public boolean add(Customer customer) {
        String query = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID, Create_Date, " +
                "Created_By, Last_Update, Last_Updated_By) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setString(1, customer.getCustomerName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPostalCode());
            statement.setString(4, customer.getPhone());
            statement.setInt(5, customer.getDivisionID());
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(7, SessionManager.getInstance().getCurrentUser().toString());
            statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(9, SessionManager.getInstance().getCurrentUser().toString());


            int affectedRows = statement.executeUpdate();

            if(affectedRows != 1){
                return false;
            } else {
                return true;
            }
        }catch (SQLException e){
            logger.error("Error adding customer to database", e);
            return false;
        }
    }

    /**
     * Updates existing customer
     * @param customer customer object containing updated data
     * @return Returns true if update is successful
     */
    @Override
    public boolean update(Customer customer) {
        String query = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                "Division_ID = ?, Last_Update = ?, Last_Updated_By = ? WHERE Customer_ID = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setString(1, customer.getCustomerName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPostalCode());
            statement.setString(4, customer.getPhone());
            statement.setInt(5, customer.getDivisionID());
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(7, SessionManager.getInstance().getCurrentUser().toString());
            statement.setInt(8, customer.getCustomerID());

            int affectedRows = statement.executeUpdate();
            if(affectedRows != 1){
                return false;
            } else {
                return true;
            }
        }catch(SQLException e){
            logger.error("Unable to update customer in database", e);
        }
        return false;
    }

    /**
     * Creates customer object from ResultSet
     * @param results ResultSet generated from SQL query
     * @return customer object created from ResultSet data
     * @throws SQLException throws SQL exceptions if there is an error processing ResultSet
     */
    private Customer generateCustomer(ResultSet results) throws SQLException {

        Customer customer = new Customer();
        customer.setCustomerID(results.getInt("Customer_ID"));
        customer.setCustomerName(results.getString("Customer_Name"));
        customer.setAddress(results.getString("Address"));
        customer.setPostalCode(results.getString("Postal_Code"));
        customer.setPhone(results.getString("Phone"));
        customer.setDivisionID(results.getInt("Division_ID"));
        return customer;
    }
}
