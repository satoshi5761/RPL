package prlbo.project.rpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.image.Image;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));
        try {
            Parent root = fxml_load.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/Asset/To-Do-List.png")));
            primaryStage.setTitle("JADWALIN");
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