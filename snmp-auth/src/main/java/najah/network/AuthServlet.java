package najah.network;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Network2 Group
 */
public class AuthServlet extends HttpServlet {
    
    /**
     * Database implementation injected
     */
    private IDatabase database;
    
    /**
     * Logger 
     */
    private static final Logger logger = LogManager.getLogger(AuthServlet.class);
    
    /**
     * inject database object. 
     * 
     * @throws ServletException 
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.database = (IDatabase) getServletContext().getAttribute("db");
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            String name = (String) request.getParameter("username");
            String password = (String) request.getParameter("password");
            // validate auth cradentials (name, password)
            boolean isAuth = database.isValidUserByName(name, password);
            out.println(isAuth ? "OK" : "NO");
        } catch(Exception e) {
            logger.error("Exception: ", e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Validate Authentication Cradentials {name, password} for user.";
    }// </editor-fold>

}
