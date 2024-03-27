package najah.network;

import najah.network.models.DataService;
import najah.network.interfaces.IDataService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login", null), 1198, 707);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        setRoot(fxml, null);
    }
    
    static void setRoot(String fxml, Map<String, String> data) throws IOException {
        scene.setRoot(loadFXML(fxml, data));
    }

    private static Parent loadFXML(String fxml, Map<String, String> data) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent p = fxmlLoader.load();
        IDataService service = new DataService();
        
        if (fxml.equals("login")) {
            LoginController controller = (LoginController) fxmlLoader.getController();
            controller.setDataService(service);
        } else if (fxml.equals("primary")) {
            PrimaryController controller = (PrimaryController) fxmlLoader.getController();
            controller.setDataService(service);
            controller.setData(data);
        }
        
        return p;
    }

    public static void main(String[] args) {
        launch();
    }
}