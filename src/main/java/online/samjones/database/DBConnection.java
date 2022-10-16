package online.samjones.database;

import javafx.application.Platform;
import online.samjones.util.AlertCase;
import online.samjones.util.Alerts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class that provides connection to database
 */
public class DBConnection {

    public static final String RDS_HOSTNAME = "ENTER HOST NAME";
    public static final String RDS_PORT = "3306";
    public static final String RDS_DB_NAME = "DATABASE NAME";
    public static final String RDS_USERNAME = "DB USER";
    public static final String RDS_PASSWORD = "DB PASSWORD";
    public static final String CONNECTION_TIME_ZONE_STRING = "?connectionTimeZone=UTC";
    public static final String CONNECTION_STRING = "jdbc:mysql://" + RDS_HOSTNAME + ":" + RDS_PORT +
            "/" + RDS_DB_NAME + CONNECTION_TIME_ZONE_STRING;
    private Connection connection;

    private static DBConnection INSTANCE = null;
    private final Logger logger = LogManager.getLogger(DBConnection.class);

    private DBConnection () {}

    public static DBConnection getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DBConnection();
        }
        return INSTANCE;
    }

    /**
     * Opens database connection and provides error message to user upon failure. To prevent unpredictable behavior,
     * the application will quit if no database access is available.
     */
    public void  openConnection() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING, RDS_USERNAME, RDS_PASSWORD);
            logger.info(String.format("Successfully connected to database at %s", RDS_HOSTNAME));
        } catch(SQLException e){
            logger.fatal(String.format("Could not connect to database %s at %s. Check the application can reach the internet.", RDS_DB_NAME, RDS_HOSTNAME), e);
            Platform.exit();
        }

    }

    /**
     * Closes database connection. Logs error and alerts user if the connection cannot be closed.
     */
    public void closeConnection(){
        try {
            connection.close();
            logger.info(String.format("Connection to database %s at host %s terminated", RDS_DB_NAME, RDS_HOSTNAME));
        } catch(SQLException e){
            logger.error(String.format("Unable to close connection to %s", RDS_HOSTNAME), e);
        }
    }

    /**
     * Provides the connection object to DAO implementations
     * @return Returns the database connection the class has created
     */
    public Connection getConnection(){
        return connection;
    }
}
