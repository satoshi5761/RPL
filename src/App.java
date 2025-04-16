    
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("Registration.fxml"));
        try {
            Parent root = fxml_load.load();
            primaryStage.setScene(new Scene(root));

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    public static void main(String[] args) {
        System.out.println("Running ... ");
        launch(args);
    }
}