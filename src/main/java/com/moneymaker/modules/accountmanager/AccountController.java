package com.moneymaker.modules.accountmanager;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by jaynd on 3/27/2016.
 */
public class AccountController implements Initializable {

    @FXML
    private TableView<Account> tblAccount;

    @FXML
    private Button btnNewEntry;

    private SQLAccount accSQL = new SQLAccount();

    public void initialize(URL url, ResourceBundle rb) {
        tblAccount.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                AnchorPane pane = new AnchorPane();
                ContextMenu contextMenu = contextMenu();
                tblAccount.setContextMenu(contextMenu);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
            }
            tblAccount.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            event.consume();
        });

        tblAccount.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deleteAccount();
            }
            event.consume();
        });

        showAccounts();
    }

    public void showAccounts() {

        ObservableList<Account> accounts = tblAccount.getItems();
        accounts.clear();

        accounts.addAll(accSQL.viewAccounts());

    }

    @FXML
    private void deleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Selected Accoun(s)");
        alert.setContentText("Are you sure you want to delete the selected transaction(s)? " +
                "Account(s) will be permanently deleted.");

        Optional<ButtonType> result = alert.showAndWait();
        ObservableList<Account> accounts = tblAccount.getSelectionModel().getSelectedItems();

        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                for (Account a : accounts) {
                    if (a != null) {
                        int accID = Integer.parseInt(a.getAccountID());
                        accSQL.deleteAccount(accID);
                    }
                }
            }
        }
        showAccounts();
    }

    @FXML
    private void newAccount() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/inputwindows/newAccount.fxml"));
        try {
            AnchorPane newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle(("New Account"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnNewEntry.getScene().getWindow());
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        showAccounts();
    }

    @FXML
    private void updateAccount() {
        ObservableList<Account> accounts = tblAccount.getSelectionModel().getSelectedItems();

        if (accounts.size() == 1) {
            for (Account a : accounts) {
                int accountID = Integer.parseInt(a.getAccountID());
                String accountName = a.getAccountName();
                if (accountName.startsWith("* ")) {
                    accountName = accountName.replace("* ","");
                }
                String accountType = a.getAccountType();
                String accountStartingBalance = a.getAccountStartingBalance();
                int accountIsPrimary = Integer.parseInt(a.getIsPrimaryAccount());
                int accountIsInCashFlow = Integer.parseInt(a.getInCashFlow());

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/inputwindows/updateAccount.fxml"));
                try {
                    AnchorPane newWindow = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle(("Update " + accountName));
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(btnNewEntry.getScene().getWindow());
                    Scene scene = new Scene(newWindow);
                    stage.setScene(scene);
                    UpdateAccountController updateAccountController = loader.getController();
                    updateAccountController.fillFields(accountID, accountName, accountType, accountStartingBalance, accountIsPrimary, accountIsInCashFlow);
                    stage.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        showAccounts();
    }

    private ContextMenu contextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem update = new MenuItem("Update");
        MenuItem newAccount = new MenuItem("New");
        MenuItem delete = new MenuItem("Delete");

        update.setOnAction(event -> {
            updateAccount();
            event.consume();
        });

        newAccount.setOnAction(event -> {
            newAccount();
            event.consume();
        });

        delete.setOnAction(event -> {
            deleteAccount();
            event.consume();
        });

        contextMenu.getItems().addAll(update, newAccount, delete);
        return contextMenu;
    }
}
