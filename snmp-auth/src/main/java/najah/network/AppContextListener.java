package najah.network;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Network2 Group
 */
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        IDatabase db;
        try {
            db = new FileDatabase("users.json");
            // IDatabase db = new MySqlDatabase();
            // Store it in the ServletContext
            servletContextEvent.getServletContext().setAttribute("db", db);
        } catch (IOException ex) {
            Logger.getLogger(AppContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
