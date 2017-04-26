package com.moneymaker;

import com.moneymaker.main.UsernameData;
import com.moneymaker.main.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private UsernameData usernameData = new UsernameData();

    @Override
    public void start(Stage stage) throws Exception{
//        usernameData.setCredentials("test", "test");
//        usernameData.clearCredentials();
//        usernameData.clearAutoLogin();
        if (usernameData.getAutoLogin() == null || usernameData.getUsername() == null || usernameData.getPassword() == null) {
            openLoginWindow();
        } else {
            usernameData.setSessionCredentials(usernameData.getUsername(),usernameData.getPassword());
            openMainWindow();
        }
    }

    @FXML
    private void openLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main/loginWindow.fxml"));
        try {
            AnchorPane primaryStage = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            Scene scene = new Scene(primaryStage);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openMainWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main/mainWindow.fxml"));
        try {
            AnchorPane primaryStage = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Budget");
            Scene scene = new Scene(primaryStage);
            stage.setScene(scene);
            MainWindowController mainWindow = loader.getController();
            mainWindow.setUserName(usernameData.getUsername());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
