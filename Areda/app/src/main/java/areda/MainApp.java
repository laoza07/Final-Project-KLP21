package areda;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        StackPane root = new StackPane();
        root.getChildren().add(new Label("Areda Careers"));

        Scene scene = new Scene(root, 950, 650);

        primaryStage.setTitle("Areda Careers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}