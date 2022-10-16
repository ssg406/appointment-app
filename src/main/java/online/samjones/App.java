package online.samjones;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import online.samjones.database.DBConnection;
import online.samjones.util.Localizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static final String PACKAGE_STRING = "/online/samjones/";
    private final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void init() throws Exception {
        DBConnection.getInstance().openConnection();
        super.init();
    }

    @Override
    public void stop() throws Exception {
        DBConnection.getInstance().closeConnection();
        super.stop();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/login.fxml"));
        stage.setTitle(Localizer.getLocalizedMessage("applicationTitle"));
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("img/calendar.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}