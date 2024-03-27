package najah.network.utils;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author Network2 Group
 */
public class FXOperations {
    
    /**
     * Converts a JSON string into an object of the specified class type.
     *
     * @param jsonData The JSON string to be deserialized.
     * @param clazz The class type into which the JSON is to be deserialized.
     * @param <T> The type parameter for the class.
     * @return An instance of the specified class type containing the data from the JSON string.
     * @throws IOException If an error occurs during deserialization.
     */
    public static <T> T fromJson(String jsonData, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonData, clazz);
    }
    
    public static void fillTable(TableView<Map<String, Object>> tableView,
            List<Map<String, Object>> data) throws Exception {

        if (!data.isEmpty()) {
            // Clear existing columns
            tableView.getColumns().clear();

            // Add an index column as the first column
            TableColumn<Map<String, Object>, Number> indexColumn = new TableColumn<>("#");
            indexColumn.setSortable(false);
            indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(
                    tableView.getItems().indexOf(column.getValue()) + 1));
            tableView.getColumns().add(indexColumn);

            // Use the first row to determine columns
            Map<String, Object> firstRow = data.get(0);
            for (String key : firstRow.keySet()) {
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(key);
                
                // Set up a custom cell value factory to handle splitting the value from the data type
                column.setCellValueFactory(cellData -> {
                    Map<String, Object> rowValues = cellData.getValue();
                    Object value = rowValues.get(key);
                    String stringValue = value == null ? "" : value.toString();
                    // Split the data type from the value, assuming format "TYPE: value"
                    String actualValue = removeDataType(stringValue);
                    return new SimpleStringProperty(actualValue);
                });
                
                tableView.getColumns().add(column);
            }
            
            // Set the items for the TableView
            ObservableList<Map<String, Object>> tableData = FXCollections.observableArrayList(data);
            tableView.setItems(tableData);
        }
    }
    
    public static String removeDataType(String data) {
        int indexOfColon = data.indexOf(':');
        String content;
        if (indexOfColon != -1) {
            content = data.substring(indexOfColon + 1).trim();
        } else {
            content = data;
        }
        return content;
    }
}
