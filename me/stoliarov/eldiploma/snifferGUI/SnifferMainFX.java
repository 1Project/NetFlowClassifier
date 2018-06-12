package me.stoliarov.eldiploma.snifferGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by VladS on 12/12/2015.
 */
public class SnifferMainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "SnifferMainFX.fxml"
                )
        );

        final Parent root = (Parent) loader.load();
        final SnifferControllerFX controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Захват трафика");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
