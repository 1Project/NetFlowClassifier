package me.stoliarov.finalero.mainGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by VladS on 12/12/2015.
 */
public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "GUI.fxml"
                )
        );

        final Parent root = (Parent) loader.load();
        final ControllerFX controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Система классификации сетевого трафика методами машинного обучения");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
