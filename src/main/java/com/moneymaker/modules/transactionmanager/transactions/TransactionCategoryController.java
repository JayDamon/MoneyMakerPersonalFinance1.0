package com.moneymaker.modules.transactionmanager.transactions;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jay Damon on 9/10/2016.
 */
public class TransactionCategoryController implements Initializable {

    @FXML
    private VBox vboxTransactionCategory;

    @FXML
    private TableView<Transaction> tblUncategorizedTransactions;

    @FXML
    Button btnFinish;

    private String budgetID;

    SQLTransaction transactionSQL = new SQLTransaction();

    public void initialize(URL location, ResourceBundle rs) {
        tblUncategorizedTransactions.setOnMouseClicked(event -> tblUncategorizedTransactions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE));
    }

    public void setBudgetID(String budgetID) {
        this.budgetID = budgetID;
    }

    public void addUncategorizedTransactions(ObservableList<Transaction> uncategorizedTransactions, String budgetName) {
        ObservableList<Transaction> data = tblUncategorizedTransactions.getItems();
        data.clear();

        for (Transaction t : uncategorizedTransactions) {
            String transactionID = t.getTransactionID();
            String transactionAccount = t.getTransactionAccount();
            String transactionBudget = t.getTransactionBudget();
            String transactionCategory = t.getTransactionCategory();
            String transactionRecurring = t.getTransactionRecurring();
            String transactionDate = t.getTransactionDate();
            String transactionDescription = t.getTransactionDescription();
            String transactionAmount = t.getTransactionAmount();
            String transactionTimeStamp = t.getTransactionTimeStamp();

            data.add(new Transaction(transactionID, transactionAccount, transactionBudget, transactionCategory,
                    transactionRecurring, transactionDate, transactionDescription, transactionAmount, transactionTimeStamp));
        }

        tblUncategorizedTransactions.setOnDragDetected(event -> {
            Dragboard db = tblUncategorizedTransactions.startDragAndDrop(TransferMode.COPY);

            String transactionID = tblUncategorizedTransactions.getSelectionModel().getSelectedItem().getTransactionID();

            ClipboardContent content = new ClipboardContent();
            content.putString(transactionID);
            db.setContent(content);
            event.consume();
        });

        ObservableList<String> transactionCategories = transactionSQL.listTransactionCategory(budgetName);

        for (String s : transactionCategories) {
            Label newLabel = new Label(s);
            newLabel.setStyle("-fx-border-color: BLACK;");
            newLabel.setPrefSize(243, 71);
            newLabel.setMinHeight(30);
            newLabel.setText(s);

            newLabel.addEventHandler(
                    DragEvent.DRAG_OVER,
                    event -> {
                        event.acceptTransferModes(TransferMode.COPY);
                        newLabel.setStyle("-fx-border-color: #3399ff;");
                        event.consume();
                    }
            );

            newLabel.addEventHandler(
                    DragEvent.DRAG_EXITED,
                    event -> {
                        newLabel.setStyle("-fx-border-color: BLACK;");
                        event.consume();
                    }
            );

            newLabel.addEventHandler(
                    DragEvent.DRAG_DROPPED,
                    event -> {
                        if(event.getTransferMode() == TransferMode.COPY && event.getDragboard().hasString()) {

                            ObservableList<Transaction> selectedTransactions = tblUncategorizedTransactions.getSelectionModel().getSelectedItems();
                            String transactionCategory = newLabel.getText();
                            for (Transaction t : selectedTransactions) {
                                String transactionID = t.getTransactionID();
                                transactionSQL.updateTransactionAddCategory(transactionCategory, transactionID, budgetID);
                            }
                            tblUncategorizedTransactions.getItems().removeAll(selectedTransactions);

                            event.setDropCompleted(true);
                        }
                        event.consume();
            });

            vboxTransactionCategory.getChildren().add(newLabel);
        }

    }

    @FXML
    protected void exitWindow() {
        Stage stage = (Stage)btnFinish.getScene().getWindow();
        stage.close();
    }

}
