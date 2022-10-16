package online.samjones.database;

import online.samjones.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements EntityDAO<User>{

    private final Logger logger = LogManager.getLogger(UserDAO.class);

    /**
     * Get all users from database
     * @return list of all users
     */
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try(Statement statement = DBConnection.getInstance().getConnection().createStatement();
            ResultSet results = statement.executeQuery(query)){

            while(results.next()){
                users.add(generateUser(results));
            }
            return users;
        } catch(SQLException e){
            logger.error("Unable to get all users from database", e);
            return null;
        }
    }

    /**
     * Gets single user by ID
     * @param id user ID
     * @return optional containing user if found
     */
    @Override
    public Optional<User> getOne(int id) {
        String query = "SELECT * FROM users WHERE User_ID = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            if (results.next()){
                return Optional.of(generateUser(results));
            }
        } catch(SQLException e){
            logger.error("Unable to get user from database.", e);
        }
        return Optional.empty();
    }

    /**
     * Get single user by username
     * @param username username string to search
     * @return optional containing user if found
     */
    public Optional<User> getOne(String username) {
        String query = "SELECT * FROM users WHERE User_Name = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();

            if (results.next()){
                return Optional.of(generateUser(results));
            }
        } catch(SQLException e){
            logger.error("Unable to get user from database", e);
        }
        return Optional.empty();
    }


    /**
     * Generates user object from ResultSet
     * @param results ResultSet generated from SQL query
     * @return user object created from ResultSet
     * @throws SQLException throws SQL exception if there is an error processing ResultSet
     */
    private static User generateUser(ResultSet results) throws SQLException {
        User user = new User();
        user.setUserID(results.getInt("User_ID"));
        user.setUserName(results.getString("User_Name"));
        user.setPassword(results.getString("Password"));
        return user;
    }

    @Override
    public boolean add(User entity) {
        //Not Implemented
        return false;
    }

    @Override
    public boolean update(User entity) {
        //Not Implemented
        return false;
    }

    @Override
    public boolean delete(int id) {
        //Not Implemented
        return false;
    }
}
