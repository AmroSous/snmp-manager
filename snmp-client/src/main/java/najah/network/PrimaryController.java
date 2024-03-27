package najah.network;

import najah.network.utils.ServerRequestException;
import najah.network.utils.FXOperations;
import najah.network.interfaces.IDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import najah.network.records.ARP;
import najah.network.records.SnmpStatistics;

/**
 * 
 * UI Controller for manager main page 
 * 
 * @author Network2 Group
 */
public class PrimaryController {
    
    /**
     * 
     * Page data, URL's, attributes and constants
     * private attributes related to the controller. 
     */
    private String adminName = "";
    private String adminId = "";
    
    private String agentIp = "127.0.0.1";
    private String communityR = "public";
    private String communityRW = "publicRW";
    
    // Injected by FXML loader using setDataService() method
    private IDataService dataService;
    
    private static final String[] URLS = {
        "http://localhost:80/snmp-manager/page1.php",
        "http://localhost:80/snmp-manager/page2.php",
        "http://localhost:80/snmp-manager/page3.php",
        "http://localhost:80/snmp-manager/page4_get.php"
    };
    
    private final HashMap<String, TextField> sysGroup = new HashMap<>();
    
    /**
     * 
     * init Method to insert all System Group objects in a HashMap 
     * this will simplify the process of filling fields.
     */
    private void initSysGroup() {
        sysGroup.put("sysContact", this.sysContactField);
        sysGroup.put("sysName", this.sysNameField);
        sysGroup.put("sysLocation", this.sysLocationField);
        sysGroup.put("sysDescr", this.sysDescrField);
        sysGroup.put("sysObjectID", this.sysObjectIDField);
        sysGroup.put("sysUpTime", this.sysUpTimeField);
    } 
    
    /**
     * 
     * FXML Elements for primary.fxml file.
     */
    @FXML 
    private Label adminNameLabel; 
    
    @FXML 
    private Label adminIdLabel; 
    
    @FXML 
    private TextField agentIpField; 
    
    @FXML 
    private TextField communityRField; 
    
    @FXML 
    private TextField communityRWField; 
    
    @FXML 
    private Button editButton; 
    
    @FXML 
    private Button applyButton;
    
    @FXML 
    private TextFlow loggerArea; 
    
    @FXML 
    private ScrollPane logScrollPane;
    
    @FXML 
    private TableView tcpTable;
    
    @FXML 
    private TableView arpTable;
    
    @FXML
    private TableView snmpStatisticsTable;
    
    @FXML 
    private TextField sysContactField; 
    
    @FXML 
    private TextField sysNameField;
    
    @FXML 
    private TextField sysLocationField;
    
    @FXML 
    private TextField sysDescrField;
    
    @FXML 
    private TextField sysObjectIDField;
    
    @FXML 
    private TextField sysUpTimeField;
    
    /**
     * 
     * fXML Listeners for buttons and user actions.
     */
    
    @FXML 
    private void editButtonPressed() {
        setEditMode(true);
    }
    
    @FXML
    private void applyButtonPressed() {
        setEditMode(false);
        this.agentIp = this.agentIpField.getText().trim();
        this.communityR = this.communityRField.getText().trim();
        this.communityRW = this.communityRWField.getText().trim();
        writeLog(Color.GREEN, "Changes applied.");
    }
    
    @FXML 
    private void signoutButtonPressed() {
        try {
            App.setRoot("login");
        } catch (IOException ex) {
            writeLog(Color.RED, "Fail in open Login page.");
        }
    }
    
    @FXML 
    private void getTCPTablePressed() {
        new Thread(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("agentIp", this.agentIp);
            params.put("communityR", this.communityR);
     
            String response = getData(URLS[1], params);
            
            List<Map<String, Object>> data = null;
            ObjectMapper mapper = new ObjectMapper();
            try {
                data = mapper.readValue(
                        response, new TypeReference<List<Map<String, Object>>>(){});
            } catch (IOException ex) {
                writeLog(Color.RED, "Exception while parsing json data.");
            }
            
            if (response != null) fillTable(this.tcpTable, data);
        }).start();
    }
    
    @FXML 
    private void getARPTablePressed() {
        new Thread(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("agentIp", this.agentIp);
            params.put("communityR", this.communityR);
            String response = getData(URLS[2], params);
            
            ARP arp = null;
            try {
                arp = FXOperations.fromJson(response, ARP.class);
            } catch (IOException ex) {
                writeLog(Color.RED, "Exception while parsing json data.");
            }
            
            if (response != null) fillTable(this.arpTable, arp.arpEntries());
        }).start();
    }
    
    @FXML 
    private void getSNMPStatisticsPressed() {
        new Thread(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("agentIp", this.agentIp);
            params.put("communityR", this.communityR);
            String response = getData(URLS[3], params);
            
            SnmpStatistics ss = null;
            try {
                ss = FXOperations.fromJson(response, SnmpStatistics.class);
            } catch (IOException ex) {
                writeLog(Color.RED, "Exception while parsing json data.");
            }
            
            if (response != null) fillTable(this.snmpStatisticsTable, ss.snmpStatistics());
        }).start();
    }
    
    @FXML 
    private void getSystemGroupPressed() {
        new Thread(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("agentIp", this.agentIp);
            params.put("communityR", this.communityR);
            String response = getData(URLS[0], params);
            if (sysGroup.isEmpty()) initSysGroup();
            if (response != null) fillSystemGroupInfo(response);
        }).start();
    }
    
    @FXML 
    private void postSysNamePressed() {
        new Thread(() -> {
            String newName = this.sysNameField.getText();
            postSysObject("sysName", newName);
        }).start();
    }
    
    @FXML 
    private void postSysLocationPressed() {
        new Thread(() -> {
            String newLocation = this.sysLocationField.getText();
            postSysObject("sysLocation", newLocation);
        }).start();
    }
    
    @FXML 
    private void postSysContactPressed() {
        new Thread(() -> {
            String newContact = this.sysContactField.getText();
            postSysObject("sysContact", newContact);
        }).start();
    }
    
    /**
     * utility method that put/release edit mode for 
     * agent information.
     * 
     * @param flag true for edit, false for normal
     */
    private void setEditMode(boolean flag) {
        this.agentIpField.setDisable(!flag);
        this.communityRField.setDisable(!flag);
        this.communityRWField.setDisable(!flag);
        this.editButton.setDisable(flag);
        this.applyButton.setDisable(!flag);
    }
    
    /**
     * setter method sets admin account information,
     * taken from Login Page. 
     * 
     * @param data 
     */
    public void setData(Map<String, String> data) {
        this.adminName = data.get("adminName");
        this.adminId = data.get("adminId");
        this.adminNameLabel.setText(this.adminName);
        this.adminIdLabel.setText(this.adminId);
    }
     
    /**
     * setter method for IDataService model that manipulates requests,
     * injected when loading primary.fxml page.
     * 
     * @param service 
     */
    public void setDataService(IDataService service) {
        this.dataService = service;
    }
    
    /**
     * multi-threaded method that write a log, into logArea,
     * called by a thread, this methods manipulates UI on FX-thread.
     * 
     * @param p color of log data
     * @param msg data to log
     */
    private synchronized void writeLog(Paint p, String msg) {
        Text comm = new Text("> ");
        Text text = new Text(msg + "\n");
        String style = "-fx-font-family: simsun; -fx-font-size: 16;";
        text.setFill(p);
        text.setStyle(style);
        comm.setFill(Color.BLACK);
        comm.setStyle(style);

        // Run updates on the JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            loggerArea.getChildren().addAll(comm, text);
            logScrollPane.setVvalue(1.0); // scroll down automatically.
        });
    }
    
    /**
     * utility method that fills TableView by data,
     * this method is thread-safe and manipulates UI in FX-thread.
     * 
     * @param table
     * @param data 
     */
    private synchronized void fillTable(TableView table, List<Map<String, Object>> data) {
        javafx.application.Platform.runLater(() -> {
            try {
                FXOperations.fillTable(table, data);
                writeLog(Color.GREEN, table.getId() + " Table filled successfully.");
            } catch (Exception ex) {
                writeLog(Color.RED, "Fail in filling " + table.getId() + " table with data.");
            }
        });
    }
    
    /**
     * utility method to send request to URL with query parameters to get data.
     * 
     * @param url
     * @param params
     * @return the response
     */
    private synchronized String getData(String url, Map<String, String> params) {
        String response = null;
        try {
            writeLog(Color.BLUE, "Sending Request ...");
            response = dataService.getRequest(url, params);
        } catch(SocketTimeoutException e) {
            writeLog(Color.RED, "Request Timeout.");
        } catch (ServerRequestException | IOException ex) {
            writeLog(Color.RED, "Fail in requesting data from server.");
        }
        return response;
    }

    /**
     * utility method to send post request to server, to change sysGroup object.
     * 
     * @param key
     * @param value 
     */
    private synchronized void postSysObject(String key, String value) {
        // put agent info 
        Map<String, String> info = new HashMap<>();
        info.put(key, value);
        info.put("agentIp", this.agentIp);
        info.put("communityRW", this.communityRW);
        String response = null;
        try {
            writeLog(Color.BLUE, "Sending updated info ...");
            response = dataService.postRequest(URLS[0], info);
        } catch (SocketTimeoutException ex) {
            writeLog(Color.RED, "Request Timed out.");
        } catch (ServerRequestException | IOException ex) {
            writeLog(Color.RED, "[ERROR] while posting data to server.");
        }
        if (response != null) {
            try {
                Map<String, String> data = FXOperations.fromJson(response, Map.class);
                if (data.get("status").equals("OK")) {
                    writeLog(Color.GREEN, key + " updated successfull.");
                } else {
                    writeLog(Color.RED, data.get("message"));
                }
            } catch (IOException ex) {
                writeLog(Color.RED, "[ERROR] while parsing json data.");
            }
        }
    }
    
    /**
     * utility method to fill System Group Fields with data, parsed from JSON.
     * 
     * @param response JSON data.
     */
    private void fillSystemGroupInfo(String response) {
        javafx.application.Platform.runLater(() -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                // Parse the JSON string into List of Maps
                Map<String, Object> data = mapper.readValue(response, new TypeReference<Map<String, Object>>(){});
                sysGroup.forEach((k, v) -> {
                    v.setText(FXOperations.removeDataType((String)data.get(k)));
                    if (v.getText().startsWith("\"")) v.setText(v.getText().substring(1));
                });
                writeLog(Color.GREEN, "System Group Filled Successfully.");
            } catch (JsonProcessingException ex) {
                writeLog(Color.RED, "Exception in parsing system group json data.");
            }
        });
    }
}
