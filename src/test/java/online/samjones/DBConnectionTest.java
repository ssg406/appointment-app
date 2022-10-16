package online.samjones;

import online.samjones.database.DBConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DBConnectionTest {

    @Test
    void connectionOpensSuccessfully() throws SQLException {
        DBConnection.getInstance().openConnection();
        Connection conn = DBConnection.getInstance().getConnection();
        assertTrue(conn.isValid(2));
        DBConnection.getInstance().closeConnection();
    }

    @Test
    void connectionClosesSuccessfully() throws SQLException {
        DBConnection.getInstance().openConnection();
        Connection conn = DBConnection.getInstance().getConnection();
        assertTrue(conn.isValid(2));
        DBConnection.getInstance().closeConnection();
        assertTrue(conn.isClosed());
    }
}
