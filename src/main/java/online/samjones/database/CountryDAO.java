package online.samjones.database;

import online.samjones.model.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountryDAO implements EntityDAO<Country> {

    private final Logger logger = LogManager.getLogger(CountryDAO.class);

    /**
     * Gets all countries from database
     * @return list of countries
     */
    @Override
    public List<Country> getAll() {
        List<Country> countries  = new ArrayList<>();
        String query = "SELECT * FROM countries";

        try (Statement statement = DBConnection.getInstance().getConnection().createStatement();
             ResultSet results = statement.executeQuery(query)){
            while(results.next()){
                countries.add(generateCountry(results));
            }
            return countries;
        } catch (SQLException e) {
            logger.error("Unable to retrieve countries from database", e);
            return null;
        }
    }
    /**
     * Gets single country from database
     * @param id country ID
     * @return optional containing country if found
     */
    @Override
    public Optional<Country> getOne(int id) {
        String query = "SELECT * FROM countries WHERE Country_ID = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                return Optional.of(generateCountry(results));
            }
        } catch(SQLException e){
            logger.error("Unable to retrieve country from database", e);
        }
        return Optional.empty();
    }

    /**
     * Generates country object from ResultSet
     * @param results database result set
     * @return country object
     * @throws SQLException SQL exceptions are thrown
     */
    private Country generateCountry(ResultSet results) throws SQLException{
        Country country = new Country();
        country.setCountryID(results.getInt("Country_ID"));
        country.setCountry(results.getString("Country"));
        return country;
    }

    @Override
    public boolean add(Country entity) {
        //Not Implemented
        return false;
    }

    @Override
    public boolean update(Country entity) {
        //Not Implemented
        return false;
    }

    @Override
    public boolean delete(int id) {
        //Not Implemented
        return false;
    }
}
