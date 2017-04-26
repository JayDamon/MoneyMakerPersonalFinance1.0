package com.moneymaker.main;

import com.moneymaker.utilities.SQLAdmin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created for MoneyMaker by Jay Damon on 3/30/2016.
 */
public class LoginWindowController implements Initializable {

    @FXML
    private CheckBox checkBoxSaveCredentials, checkBoxAutoLogin;

    @FXML
    private PasswordField passwordFieldLogin;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private Label invalidField;

    private UsernameData usernameData = new UsernameData();

    public void initialize(URL url, ResourceBundle rs) {

        passwordFieldLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                passwordFieldLogin.setStyle("-fx-control-inner-background: white");
            }
        });

        textFieldUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textFieldUsername.setStyle("-fx-control-inner-background: white");
            }
        });

        if(usernameData.getSaveCredentials() != null || usernameData.getUsername() != null) {
            checkBoxSaveCredentials.setSelected(true);
            textFieldUsername.setText(usernameData.getUsername());
        }
    }

    @FXML
    public void loginAction(ActionEvent event) throws Exception {
        SQLAdmin sqlAdmin = new SQLAdmin();
        if (passwordFieldLogin.getText().isEmpty() || textFieldUsername.getText().isEmpty()) {
            if (passwordFieldLogin.getText().isEmpty()) {
                passwordFieldLogin.setStyle("-fx-control-inner-background: red");
            }
            if (textFieldUsername.getText().isEmpty()) {
                textFieldUsername.setStyle("-fx-control-inner-background: red");
            }
        } else {
            String userName = textFieldUsername.getText();
            String password = passwordFieldLogin.getText();

            boolean userExists = sqlAdmin.findUserExists(userName, password);

            if (userExists) {
                if (checkBoxSaveCredentials.isSelected()) {
                    usernameData.setUsername(userName);
                    usernameData.setSaveCredentials(true);
                    usernameData.clearPassword();
                } else {
                    usernameData.clearUsername();
                    usernameData.clearPassword();
                }

                if (checkBoxAutoLogin.isSelected()) {
                    usernameData.setAutoLogin(true);
                    usernameData.setUsername(userName);
                    usernameData.setPassword(password);
                }

                usernameData.setSessionCredentials(userName, password);

                ((Node) (event.getSource())).getScene().getWindow().hide();

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main/mainWindow.fxml"));
                AnchorPane primaryStage = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Budget");
                Scene scene = new Scene(primaryStage);
                stage.setScene(scene);
                MainWindowController mainWindow = loader.getController();
                mainWindow.setUserName(userName);
                stage.show();
            } else {
                invalidField.setText("Username or Password is invalid");
            }
        }
    }

    @FXML
    protected void newUser(ActionEvent event) throws Exception {
        ((Node) (event.getSource())).getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main/newUser.fxml"));
        AnchorPane primaryStage = loader.load();
        Stage stage = new Stage();
        stage.setTitle("New User Registration");
        Scene scene = new Scene(primaryStage);
        stage.setScene(scene);
        stage.show();

    }

}
