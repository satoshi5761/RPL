package prlbo.project.rpl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;

public class App extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxml_load;
            Parent root;
            fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));

            root = fxml_load.load();
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("JADWALIN");
            primaryStage.setWidth(725);
            primaryStage.setHeight(535);
            primaryStage.setResizable(false);
            primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/Asset/To-Do-List.png")));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setRoot(String fxml, String title, boolean isResizeable)
            throws IOException {
        primaryStage.getScene().setRoot(loadFXML(fxml));
        primaryStage.sizeToScene();
        primaryStage.setResizable(isResizeable);
        if (title != null) {
            primaryStage.setTitle(title);
        }
        primaryStage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class
                .getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        System.out.println("Running ... ");
        launch(args);
    }
}