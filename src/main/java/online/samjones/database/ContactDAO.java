package online.samjones.database;

import online.samjones.model.Contact;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContactDAO implements EntityDAO<Contact>{

    private final Logger logger = LogManager.getLogger(ContactDAO.class);

    /**
     * Returns all contacts from the database
     * @return List of contacts
     */
    @Override
    public List<Contact> getAll() {
        List<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM contacts";

        try (Statement statement = DBConnection.getInstance().getConnection().createStatement();
             ResultSet results = statement.executeQuery(query)){
            while(results.next()){
                contacts.add(generateContact(results));
            }
            return contacts;
        } catch (SQLException e) {
            logger.error("Unable to retrieve contacts from database", e);
            return null;
        }
    }


    /**
     * Retrieves individual contact by ID
     * @param id contact ID
     * @return Optional of contact, empty if not found
     */
    @Override
    public Optional<Contact> getOne(int id) {
        String query = "SELECT  * FROM  contacts WHERE Contact_ID = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            if (results.next()){
                return Optional.of(generateContact(results));
            }
        } catch(SQLException e){
            logger.error("Unable to retrieve contact from database", e);
        }
        return Optional.empty();
    }

    /**
     * Generates contact from ResultSet
     * @param results ResultSet generated from database query
     * @return contact object generated from result set
     * @throws SQLException SQL exceptions thrown if there is an error with ResultSet
     */
    private Contact generateContact(ResultSet results) throws SQLException{
        Contact contact = new Contact();
        contact.setContactID(results.getInt("Contact_ID"));
        contact.setContactName(results.getString("Contact_Name"));
        contact.setEmail(results.getString("Email"));

        return contact;
    }

    @Override
    public boolean update(Contact entity) {
        //Not implemented
        return false;
    }

    @Override
    public boolean delete(int id) {
        //Not implemented
        return false;
    }

    @Override
    public boolean add(Contact entity) {
        //Not implemented
        return false;
    }
}
