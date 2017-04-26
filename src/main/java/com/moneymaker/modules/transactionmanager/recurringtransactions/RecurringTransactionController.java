package com.moneymaker.modules.transactionmanager.recurringtransactions;

import com.moneymaker.modules.transactionmanager.transactions.SQLTransaction;
import com.moneymaker.utilities.FormatDollarAmount;
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
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by Jay Damon on 9/14/2016.
 */
public class RecurringTransactionController implements Initializable {

    @FXML
    TableView<RecurringTransaction> tblRecurringTransactions;

    @FXML
    Button btnNewEntry, btnUpdateEntry;

    private SQLTransaction sqlTransaction = new SQLTransaction();
    private FormatDollarAmount formatDollarAmount = new FormatDollarAmount();

    public void initialize(URL url, ResourceBundle rs) {

        tblRecurringTransactions.setOnKeyPressed(event -> {
            ObservableList<RecurringTransaction> recurringTransactions = tblRecurringTransactions.getSelectionModel().getSelectedItems();
            if(event.getCode() == KeyCode.DELETE) {
                deleteRecurringTransactions(recurringTransactions);
            }
        });

        tblRecurringTransactions.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                AnchorPane pane = new AnchorPane();
                ContextMenu contextMenu = launchContextMenu();
                tblRecurringTransactions.setContextMenu(contextMenu);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
            }
            tblRecurringTransactions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            event.consume();
        });
        showRecurringTransactions();
    }

    private ContextMenu launchContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New");
        MenuItem update = new MenuItem("Update");
        MenuItem delete = new MenuItem("Delete");

        addNew.setOnAction(event -> {
            addNewRecurringTransactions();
            event.consume();
        });

        update.setOnAction(event -> updateRecurringTransaction());

        delete.setOnAction(event -> deleteRecurringTransactions(tblRecurringTransactions.getSelectionModel().getSelectedItems()));

        contextMenu.getItems().addAll(addNew, update, delete);
        return contextMenu;
    }

    private void showRecurringTransactions() {
        ObservableList<RecurringTransaction> recurringTransactions = tblRecurringTransactions.getItems();
        recurringTransactions.clear();
        recurringTransactions.addAll(sqlTransaction.viewRecurringTransactions());
        tblRecurringTransactions.sort();

        Collections.sort(recurringTransactions, (t1, t2) -> t1.getRecurringTransactionName().compareTo(t2.getRecurringTransactionName()));
    }

    @FXML
    protected void addNewRecurringTransactions() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/transactions/recurringtransactions/newRecurringTransactionWindow.fxml"));

        try {
            AnchorPane newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle("New Recurring Transaction");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnNewEntry.getScene().getWindow());
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);
            stage.showAndWait();

            showRecurringTransactions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void buttonClickDelete() {
        ObservableList<RecurringTransaction> recurringTransactions = tblRecurringTransactions.getSelectionModel().getSelectedItems();

        deleteRecurringTransactions(recurringTransactions);
    }

    private void deleteRecurringTransactions(ObservableList<RecurringTransaction> recurringTransactions) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Selected Transactions");
        alert.setContentText("Are you sure you want to delete the selected recurring transacions? " +
                "This will permanently delete them.");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {

                for (RecurringTransaction rt : recurringTransactions) {
                    String recurringTransactionID = rt.getRecurringTransactionID();
                    sqlTransaction.deleteRecurringTransaction(recurringTransactionID);
                }
            }
        }
        showRecurringTransactions();
    }

    @FXML
    protected void updateRecurringTransaction() {
        ObservableList<RecurringTransaction> selectedTransactions = tblRecurringTransactions.getSelectionModel().getSelectedItems();
        int selectedCount = selectedTransactions.size();
        if (selectedCount == 1) {
            for (RecurringTransaction rt : selectedTransactions) {
                String transactionID = rt.getRecurringTransactionID();
                String transactionName = rt.getRecurringTransactionName();
                String accountName = rt.getRecurringTransactionAccount();
                String budgetName = rt.getRecurringTransactionBudget();
                String frequencyType = rt.getRecurringTransactionFrequency();
                String occurrence = rt.getRecurringTransactionOccurrence();
                String type = rt.getRecurringTransactionType();
                String startDate = rt.getRecurringTransactionStartDate();
                String endDate = rt.getRecurringTransactionEndDate();
                String amount = rt.getRecurringTransactionAmount();

                amount = formatDollarAmount.CleanDollarAmountsForSQL(amount);

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/transactions/recurringtransactions/updateRecurringTransactionWindow.fxml"));
                try  {
                    AnchorPane newWindow = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle(transactionName);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(btnUpdateEntry.getScene().getWindow());
                    Scene scene = new Scene(newWindow);
                    stage.setScene(scene);
                    UpdateRecurringTransactionController updateRecurringTransactionController = loader.getController();
                    updateRecurringTransactionController.fillDatePickers(endDate, startDate);
                    updateRecurringTransactionController.fillComboAndTextBoxes(transactionID, transactionName, accountName, budgetName, frequencyType, occurrence, type, amount);
                    stage.showAndWait();

                    showRecurringTransactions();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
