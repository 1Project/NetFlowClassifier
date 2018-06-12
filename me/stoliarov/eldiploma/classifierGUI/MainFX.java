package me.stoliarov.eldiploma.classifierGUI;
/**
 * Created by Владислав on 11.10.2015.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "MainFX.fxml"
                )
        );

        final Parent root = (Parent) loader.load();
        final ControllerFX controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Классификация траффика");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
