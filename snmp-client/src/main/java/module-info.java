module najah.network {
    requires javafx.controls;
    requires javafx.fxml;

    opens najah.network to javafx.fxml;
    exports najah.network;
    requires com.fasterxml.jackson.databind;
    
    // Opens package to Jackson
    opens najah.network.records to com.fasterxml.jackson.databind;
}
