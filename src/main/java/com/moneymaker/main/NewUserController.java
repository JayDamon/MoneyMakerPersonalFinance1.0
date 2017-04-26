package com.moneymaker.main;

import com.moneymaker.modules.budgetmanager.BudgetWindowController;
import com.moneymaker.utilities.SQLAdmin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by Jay Damon on 4/2/2016.
 */
public class NewUserController implements Initializable {

    private String whiteFormat = "-fx-control-inner-background: white";
    private String redFormat = "-fx-control-inner-background: red";
    @FXML
    private CheckBox checkBoxSaveCredentials, checkBoxAutoLogin;

    public void initialize(URL url, ResourceBundle rs) {
        textFieldUserName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 15) {
                String limitedValue = newValue.substring(0, 15);
                usernameMatch.setText("Username can be no longer than 16 characters.");
                textFieldUserName.setText(limitedValue);
            }

            for (int i = 0; i < newValue.length(); i++) {
                char c = newValue.charAt(i);
                if (!Character.isLetterOrDigit(c)) {
                    usernameMatch.setText("Username must be alphanumeric (A-Z) or (0-9)");
                    textFieldUserName.setText(oldValue);
                    break;
                }
            }
            if (!newValue.isEmpty()) {
                textFieldUserName.setStyle(whiteFormat);
            }
        });

        textFieldPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 19) {
                String limitedValue = newValue.substring(0, 19);
                passwordMatch.setText("Username can be no longer than 20 characters.");
                textFieldPassword.setText(limitedValue);
            }

            for (int i = 0; i < newValue.length(); i++) {
                char c = newValue.charAt(i);
                if (!Character.isLetterOrDigit(c)) {
                    passwordMatch.setText("Username must be alphanumeric (A-Z) or (0-9)");
                    textFieldPassword.setText(oldValue);
                    break;
                }
            }
            if (!newValue.isEmpty()) {
                textFieldPassword.setStyle(whiteFormat);
            }
        });

        textFieldFirstName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                textFieldFirstName.setStyle(whiteFormat);
            }
        });

        textFieldLastName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                textFieldLastName.setStyle(whiteFormat);
            }
        });

        textFieldEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                textFieldEmail.setStyle(whiteFormat);
            }
        });


        textFieldPasswordConfirmation.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                String password = textFieldPassword.getText();
                if (newValue.equals(password)) {
                    passwordMatch.setText("");
                    textFieldPasswordConfirmation.setStyle(whiteFormat);
                } else {
                    passwordMatch.setText("Passwords do not match!");
                    textFieldPasswordConfirmation.setStyle(redFormat);
                }
            }
        });
    }

    private UsernameData usernameData = new UsernameData();

    @FXML
    private Label passwordMatch, usernameMatch;

    @FXML
    private TextField textFieldUserName, textFieldPassword, textFieldFirstName, textFieldLastName, textFieldEmail, textFieldPasswordConfirmation;

    @FXML
    protected void newUserCreation(ActionEvent event) {

            String usernameInput = textFieldUserName.getText();
            String firstNameInput = textFieldFirstName.getText();
            String lastNameInput = textFieldLastName.getText();
            String userEmailInput = textFieldEmail.getText().toLowerCase();
            String userPasswordInput = textFieldPassword.getText();
            String userPasswordConfirmationInput = textFieldPasswordConfirmation.getText();

        if (usernameInput.isEmpty()
                || firstNameInput.isEmpty()
                || lastNameInput.isEmpty()
                || userEmailInput.isEmpty()
                || userPasswordInput.isEmpty()
                || userPasswordConfirmationInput.isEmpty()) {


            passwordMatch.setText("You must fill in all fields");
            if (usernameInput.isEmpty()) {
                textFieldUserName.setStyle(redFormat);
            }
            if (firstNameInput.isEmpty()) {
                textFieldFirstName.setStyle(redFormat);
            }
            if (lastNameInput.isEmpty()) {
                textFieldLastName.setStyle(redFormat);
            }
            if (userEmailInput.isEmpty()) {
                textFieldEmail.setStyle(redFormat);
            }
            if (userPasswordInput.isEmpty()) {
                textFieldPassword.setStyle(redFormat);
            }
            if (userPasswordConfirmationInput.isEmpty()) {
                textFieldPasswordConfirmation.setStyle(redFormat);
            }
        } else {

            if (!userPasswordInput.equals(userPasswordConfirmationInput)) {
                passwordMatch.setText("Passwords do not match!");
            } else {
                SQLAdmin sqlAdmin = new SQLAdmin();
                boolean duplicatesFound = sqlAdmin.findDuplicateTransaction(usernameInput);
                if (duplicatesFound) {
                    usernameMatch.setText("This username is taken.  Please select another.");
                    textFieldUserName.setStyle(redFormat);
                } else {
                    usernameMatch.setText("");

                    if (checkBoxAutoLogin.isSelected()) {
                        usernameData.setAutoLogin(true);
                        usernameData.setUsername(usernameInput);
                        usernameData.setUsername(userPasswordInput);
                    }

                    if (checkBoxSaveCredentials.isSelected()) {
                        usernameData.setSaveCredentials(true);
                        usernameData.setUsername(usernameInput);
                        usernameData.clearPassword();
                    } else {
                        usernameData.clearUsername();
                        usernameData.clearPassword();
                    }

                    usernameData.setSessionCredentials(usernameInput, userPasswordInput);
                    String userSchema = usernameInput + userPasswordInput;
                    sqlAdmin.createNewUser(usernameInput, userPasswordInput, firstNameInput, lastNameInput, userEmailInput, userSchema);
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/budget/budgetTableWindow.fxml"));
                    try {
                        AnchorPane newWindow = loader.load();
                        Stage stage = new Stage();
                        stage.setTitle(("Budget Categories"));
                        Scene scene = new Scene(newWindow);
                        stage.setScene(scene);
                        BudgetWindowController budgetWindowController = loader.getController();
                        budgetWindowController.newUser = true;
                        budgetWindowController.setExitTextForNewUser();
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
