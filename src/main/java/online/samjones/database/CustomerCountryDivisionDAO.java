package online.samjones.database;

import online.samjones.model.CustomerCountryDivision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerCountryDivisionDAO implements EntityDAO<CustomerCountryDivision> {


    private final Logger logger = LogManager.getLogger(CustomerCountryDivisionDAO.class);

    /**
     * Returns all results from join on customer, country, and division tables
     * @return list of objects
     */
    @Override
    public List<CustomerCountryDivision> getAll() {
        String query = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, fld.division, " +
                "c.country FROM customers INNER JOIN first_level_divisions fld on " +
                "customers.Division_ID = fld.Division_ID INNER JOIN countries c on fld.Country_ID = c.Country_ID";

        List<CustomerCountryDivision> customerCountryDivisions = new ArrayList<>();

        try(Statement statement = DBConnection.getInstance().getConnection().createStatement();
            ResultSet results = statement.executeQuery(query)){

            while(results.next()){
                CustomerCountryDivision ccd = new CustomerCountryDivision();
                ccd.setCustomerId(results.getInt(1));
                ccd.setCustomerName(results.getString(2));
                ccd.setAddress(results.getString(3));
                ccd.setPostalCode(results.getString(4));
                ccd.setPhone(results.getString(5));
                ccd.setDivision(results.getString(6));
                ccd.setCountry(results.getString(7));
                customerCountryDivisions.add(ccd);
            }
        } catch(SQLException e){
            logger.error("Unable to get Customer Country Divisions from database", e);
        }
        return customerCountryDivisions;
    }

    @Override
    public boolean add(CustomerCountryDivision entity) {
        //Not Implemented
        return false;
    }

    @Override
    public Optional<CustomerCountryDivision> getOne(int id) {
        //Not Implemented
        return Optional.empty();
    }

    @Override
    public boolean update(CustomerCountryDivision entity) {
        //Not Implemented
        return false;
    }

    @Override
    public boolean delete(int id) {
        //Not Implemented
        return false;
    }
}
