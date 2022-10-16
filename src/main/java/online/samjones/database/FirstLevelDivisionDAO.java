package online.samjones.database;

import online.samjones.model.FirstLevelDivision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FirstLevelDivisionDAO implements EntityDAO<FirstLevelDivision> {

    private final Logger logger = LogManager.getLogger(FirstLevelDivisionDAO.class);

    /**
     * Gets all first level divisions from database
     * @return list of first level divisions
     */
    @Override
    public List<FirstLevelDivision> getAll() {
        List<FirstLevelDivision> firstLevelDivisions  = new ArrayList<>();
        String query = "SELECT * FROM first_level_divisions";

        try (Statement statement = DBConnection.getInstance().getConnection().createStatement();
             ResultSet results = statement.executeQuery(query)){
            while(results.next()){
                firstLevelDivisions.add(generateFirstLevelDivision(results));
            }
            return firstLevelDivisions;
        } catch (SQLException e) {
            logger.error("Unable to get all first level divisions", e);
            return null;
        }
    }

    /**
     * Gets single first level division by ID
     * @param id of first level division
     * @return optional containing first level division if found
     */
    @Override
    public Optional<FirstLevelDivision> getOne(int id) {
        String query = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            if (results.next()){
                return Optional.of(generateFirstLevelDivision(results));
            }
        } catch(SQLException e){
            logger.error("Unable to retrieve first level division", e);
        }
        return Optional.empty();
    }

    /**
     * Generates first level division objects from ResultSet.
     * @param results ResultSet created from SQL query
     * @return First level division object created from ResultSet data
     * @throws SQLException throws SQL exception if there is an error processing ResultSet
     */
    private FirstLevelDivision generateFirstLevelDivision(ResultSet results) throws SQLException {
        FirstLevelDivision division = new FirstLevelDivision();
        division.setDivisionID(results.getInt("Division_ID"));
        division.setDivision(results.getString("Division"));
        division.setCountryID(results.getInt("Country_ID"));
        return division;
    }

    @Override
    public boolean add(FirstLevelDivision entity) {
        //Not Implemented
        return false;
    }

    @Override
    public boolean update(FirstLevelDivision entity) {
        //Not Implemented
        return false;
    }

    @Override
    public boolean delete(int id) {
        //Not Implemented
        return false;
    }
}
