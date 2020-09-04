package org.kurodev.bmpreader.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * @author kuro
 **/
public class Main extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/overlay.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        setBounds(primaryStage);
        stage = primaryStage;
        primaryStage.getScene().setOnKeyPressed(new HotkeyHandler(loader.getController()));
        primaryStage.show();
    }

    private void setBounds(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        primaryStage.setMaxHeight(screen.getBounds().getHeight());
        primaryStage.setMaxWidth(screen.getBounds().getWidth());
    }
}
