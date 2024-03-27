package najah.network;

import najah.network.interfaces.IDataService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import najah.network.utils.ServerRequestException;

/**
 * Login Page Controller.
 * 
 * @author Network2 Group
 */
public class LoginController {
    
    private String adminName = "Amro";
    private String adminId = "12028268";
    private boolean isVerified1 = false;
    private boolean isVerified2 = false;
    private final static String servletURL = "http://localhost:8080/snmp-auth/auth-servlet";
    private final static String jspURL = "http://localhost:8080/snmp-auth/auth-jsp";
    
    // IDataService object, Injected from the constructor.
    private IDataService dataService;
    
    public void setDataService(IDataService dataService) {
        this.dataService = dataService;
    }
    
    @FXML 
    private TextField adminNameField;
    
    @FXML
    private TextField adminIdField;
    
    @FXML
    private PasswordField adminPasswordField;
    
    @FXML 
    private Circle verifiedCircle1;
    
    @FXML 
    private Circle verifiedCircle2;
    
    @FXML 
    private Label verifiedLabel1;
    
    @FXML 
    private Label verifiedLabel2;
    
    @FXML 
    private Button enterButton;
    
    @FXML 
    private Label messageLabel;
    
    @FXML 
    private void verify1() {
        // get user inputs and validate it.
        String username = this.adminNameField.getText();
        String password = this.adminPasswordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            writeMessage(Color.RED, "Empty field not allowed.");
            return;
        }
        
        String response = null;
        
        try {
            // send (name, password) to servlet server.
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            response = dataService.getRequest(servletURL, data);
        } catch (ServerRequestException ex) {
            writeMessage(Color.RED, ex.getMessage() + ", status: " + ex.getStatus());
        } catch (IOException ex) {
            writeMessage(Color.RED, "Fail in requesting the server.");
        }

        
        boolean res = response != null && response.trim().equals("OK");
                
        this.isVerified1 = res;
        setVerified1(res);
        
        if (res) {
            this.adminName = username;
            writeMessage(Color.GREEN, "verification 1 completed successfully.");
        } else {
            writeMessage(Color.RED, "verification 1 failed.");
        }
        
        checkEnterEnable();
    }
    
    @FXML 
    private void verify2() {
        // get user inputs and validate it.
        String id = this.adminIdField.getText();
        String password = this.adminPasswordField.getText();
        
        if (id.isEmpty() || password.isEmpty()) {
            writeMessage(Color.RED, "Invalid id or/and password");
            return;
        }
        
        String response = null;
        
        try {
            // send (name, password) to servlet server.
            Map<String, String> data = new HashMap<>();
            data.put("id", id);
            data.put("password", password);
            response = dataService.getRequest(jspURL, data);
        } catch (ServerRequestException ex) {
            writeMessage(Color.RED, ex.getMessage() + ", status: " + ex.getStatus());
        } catch (IOException ex) {
            writeMessage(Color.RED, "Fail in requesting the server.");
        }
        
        boolean res = response != null && response.trim().equals("OK");
        
        this.isVerified2 = res;
        setVerified2(res);
        
        if (res) {
            this.adminId = id;
            writeMessage(Color.GREEN, "verification 2completed successfully.");
        } else {
            writeMessage(Color.RED, "verification 2 failed.");
        }
        
        checkEnterEnable();
    }
    
    @FXML 
    private void enter() {
        if (!isVerified1 || !isVerified2) return;
        try {
            clearAll();
            Map<String, String> data = new HashMap<>();
            data.put("adminName", this.adminName);
            data.put("adminId", this.adminId);
            App.setRoot("primary", data);
        } catch (IOException ex) {
            writeMessage(Color.RED, "[ERROR] Fall entering Manager");
        }
    }
    
    private void setVerified1(boolean flag) {
        this.verifiedCircle1.setFill(flag ? Color.GREEN : Color.RED);
        this.verifiedLabel1.setTextFill(flag ? Color.GREEN : Color.RED);
    }
    
    private void setVerified2(boolean flag) {
        this.verifiedCircle2.setFill(flag ? Color.GREEN : Color.RED);
        this.verifiedLabel2.setTextFill(flag ? Color.GREEN : Color.RED);
    }
    
    private void writeMessage(Paint p, String msg) {
        this.messageLabel.setText(msg);
        this.messageLabel.setTextFill(p);
    }
    
    private void clearAll() {
        this.adminIdField.setText("12028268");
        this.adminNameField.setText("Amro");
        this.adminPasswordField.setText("password_123");
        setVerified1(false);
        setVerified2(false);
        writeMessage(Color.GREEN, "");
        this.enterButton.setDisable(true);
    }

    private void checkEnterEnable() {
        if (isVerified1 && isVerified2) {
            this.enterButton.setDisable(false);
            writeMessage(Color.GREEN, "Two method verifiction completed. You can now enter you account.");
        } else {
            this.enterButton.setDisable(true);
        }
    }
}