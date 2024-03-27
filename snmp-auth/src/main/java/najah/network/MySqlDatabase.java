package najah.network;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Network2 Group
 */
public class MySqlDatabase implements IDatabase {
    
    private static final Logger logger = LogManager.getLogger(MySqlDatabase.class);
    private static final String URL = "jdbc:mysql://localhost:3306/snmp-manage";
    private static final String USERNAME = "snmp_admin";
    private static final String PASSWORD = "12345";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    @Override
    public boolean isValidUserByName(String username, String password) {
        boolean res = false;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT * FROM admins WHERE username = ? and password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(0, username);
            stmt.setString(1, password);
            ResultSet rs = stmt.executeQuery();
            res = rs.next();
        } catch (SQLException e) {
            logger.error("Exception: ", e);
        } catch (ClassNotFoundException ex) { 
            java.util.logging.Logger.getLogger(MySqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public boolean isValidUserById(int id, String password) {
        boolean res = false;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT * FROM admins WHERE id = ? and password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(0, id);
            stmt.setString(1, password);
            ResultSet rs = stmt.executeQuery();
            res = rs.next();
        } catch (SQLException e) {
            logger.error("Exception: ", e);
        } catch (ClassNotFoundException ex) { 
            java.util.logging.Logger.getLogger(MySqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
}
