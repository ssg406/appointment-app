package online.samjones.database;

import online.samjones.model.Appointment;
import online.samjones.model.Contact;
import online.samjones.model.Customer;
import online.samjones.model.User;
import online.samjones.util.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentDAO implements EntityDAO<Appointment> {
    private final Logger logger = LogManager.getLogger(AppointmentDAO.class);

    /**
     * Retrieves all appointments from database.
     * @return List of all appointments that exist in the database
     */
    @Override
    public List<Appointment> getAll() {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments";

        try (Statement statement = DBConnection.getInstance().getConnection().createStatement();
             ResultSet results = statement.executeQuery(query)){
            while(results.next()){
                appointments.add(generateAppointment(results));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all appointments from database", e);
        }
        return appointments;
    }

    /**
     * Deletes appointment by ID
     * @param id ID of appointment to delete
     * @return returns true if delete is successful
     */
    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM appointments WHERE Appointment_ID = ?";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected == 1){
                return true;
            }
        }catch(SQLException e){
            logger.error("Error deleting appointment from database", e);
            return false;
        }
        return false;
    }

    /**
     * Updates appointment ID
     * @param appointment appointment object containing new data
     * @return Returns true if update is successful
     */
    @Override
    public boolean update(Appointment appointment) {
        String query = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, " +
                "Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ?, Last_Update = ?, Last_Updated_By = ? WHERE Appointment_ID = ?";



        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){

            statement.setString(1, appointment.getTitle());
            statement.setString(2, appointment.getDescription());
            statement.setString(3, appointment.getLocation());
            statement.setString(4, appointment.getType());
            statement.setTimestamp(5, Timestamp.valueOf(appointment.getStart()));
            statement.setTimestamp(6, Timestamp.valueOf(appointment.getEnd()));
            statement.setInt(7, appointment.getCustomerId());
            statement.setInt(8, appointment.getUserId());
            statement.setInt(9, appointment.getContactId());
            statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(11, SessionManager.getInstance().getCurrentUser().toString());
            statement.setInt(12, appointment.getAppointmentId());

            int affectedRows = statement.executeUpdate();
            if(affectedRows != 1){
                return false;
            } else {
                return true;
            }
        }catch(SQLException e){
            logger.error("Error updating appointment in database", e);
        }
        return false;
    }

    /**
     * Adds appointment object to database
     * @param appointment appointment object containing new appointment data
     * @return Returns true if insertion is successful
     */
    @Override
    public boolean add(Appointment appointment) {
        String query = "INSERT INTO appointments (Title, Description, Location, " +
                "Type, Start, End, Customer_ID, User_ID, Contact_ID, Create_Date, Last_Update, Created_By, Last_Updated_By) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)){
            statement.setString(1, appointment.getTitle());
            statement.setString(2, appointment.getDescription());
            statement.setString(3, appointment.getLocation());
            statement.setString(4, appointment.getType());
            statement.setTimestamp(5, Timestamp.valueOf(appointment.getStart()));
            statement.setTimestamp(6, Timestamp.valueOf(appointment.getEnd()));
            statement.setInt(7, appointment.getCustomerId());
            statement.setInt(8, appointment.getUserId());
            statement.setInt(9, appointment.getContactId());
            statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(12, SessionManager.getInstance().getCurrentUser().toString());
            statement.setString(13, SessionManager.getInstance().getCurrentUser().toString());

            int affectedRows = statement.executeUpdate();
            if(affectedRows != 1){
                logger.error("Error adding appointment");
                return false;
            } else{
                return true;
            }
        }catch(SQLException e){
            logger.error("Error adding appointment to database", e);
            return false;
        }
    }

    @Override
    public Optional<Appointment> getOne(int id) {
        //Method not implemented
        return Optional.empty();
    }

    /**
     * Returns appointment object created from ResultSet
     * @param databaseResults ResultSet generated from database query
     * @return Appointment object created by processing ResultSet
     * @throws SQLException if database issue occurs, handled by caller
     */
    private Appointment generateAppointment(ResultSet databaseResults) throws SQLException{

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(databaseResults.getInt("Appointment_ID"));
        appointment.setTitle(databaseResults.getString("Title"));
        appointment.setDescription(databaseResults.getString("Description"));
        appointment.setLocation(databaseResults.getString("Location"));
        appointment.setType(databaseResults.getString("Type"));
        appointment.setStart(databaseResults.getTimestamp("Start").toLocalDateTime());
        appointment.setEnd(databaseResults.getTimestamp("End").toLocalDateTime());
        appointment.setCustomerId(databaseResults.getInt("Customer_ID"));
        appointment.setUserId(databaseResults.getInt("User_ID"));
        appointment.setContactId(databaseResults.getInt("Contact_ID"));

        return appointment;
    }
}
