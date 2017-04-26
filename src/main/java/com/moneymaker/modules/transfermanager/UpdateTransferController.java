package com.moneymaker.modules.transfermanager;

import com.moneymaker.modules.budgetmanager.Budget;
import com.moneymaker.modules.transactionmanager.transactions.Transaction;
import com.moneymaker.modules.transactionmanager.transactions.NewTransactionController;
import com.moneymaker.modules.accountmanager.SQLAccount;
import com.moneymaker.modules.transactionmanager.transactions.SQLTransaction;
import com.moneymaker.utilities.AutoCompleteComboBoxListener;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by Jay Damon on 10/20/2016.
 */
public class UpdateTransferController implements Initializable {

    @FXML AnchorPane anchorPaneUpdateTransfer;

    @FXML
    private Label labelFromDate, labelFromDescription, labelFromAmount, labelToDate, labelToDescription, labelToAmount;

    @FXML
    private VBox vBoxFromTransaction, vBoxToTransaction;

    @FXML
    private ComboBox<String> comboBoxFromAccount, comboBoxToAccount, comboBoxTransferType;

    @FXML
    private TableView<Transaction> tableViewFromAccount, tableViewToAccount;

    @FXML
    private Button buttonExit;

    private String toTransactionID, fromTransactionID, transferID;

    private SQLTransfer sqlTransfer = new SQLTransfer();
    private SQLAccount sqlAccount = new SQLAccount();
    private SQLTransaction sqlTransaction = new SQLTransaction();

    private String redBackgroundStyle = "-fx-control-inner-background: red";
    private String whiteBackgroundStyle = "-fx-control-inner-background: white";
    private String labelRedBackgroundStyle = "-fx-background-color: red";
    private String labelWhiteBackgroundStyle = "-fx-background-color: white";

    public void initialize (URL url, ResourceBundle rs) {

        new AutoCompleteComboBoxListener(comboBoxFromAccount);
        new AutoCompleteComboBoxListener(comboBoxToAccount);
        new AutoCompleteComboBoxListener(comboBoxTransferType);

        tableViewFromAccount.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY && tableViewFromAccount.getSelectionModel().getSelectedItems().size() == 1) {
                AnchorPane pane = new AnchorPane();
                ContextMenu contextMenu = contextMenuFromTransaction();
                tableViewFromAccount.setContextMenu(contextMenu);
                contextMenu.show(pane, event.getSceneX(), event.getSceneY());
            }
            event.consume();
        });

        tableViewToAccount.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY && tableViewToAccount.getSelectionModel().getSelectedItems().size() == 1) {
                AnchorPane pane = new AnchorPane();
                ContextMenu contextMenu = contextMenuToTransaction();
                tableViewToAccount.setContextMenu(contextMenu);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });

        comboBoxFromAccount.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                fillFromTransactionTable();
            }
            if (comboBoxFromAccount.getSelectionModel().getSelectedIndex() != -1) {
                comboBoxFromAccount.setStyle(whiteBackgroundStyle);
            }
        });

        comboBoxToAccount.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                fillToTransactionTable();
            }
            if (comboBoxToAccount.getSelectionModel().getSelectedIndex() != -1) {
                comboBoxToAccount.setStyle(whiteBackgroundStyle);
            }
        });

        comboBoxTransferType.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (comboBoxTransferType.getSelectionModel().getSelectedIndex() != -1) {
                comboBoxTransferType.setStyle(whiteBackgroundStyle);
            }
        });

        tableViewFromAccount.setOnDragDetected(event -> {
            Dragboard dragboard = tableViewFromAccount.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("From");
            dragboard.setContent(content);
            event.consume();
        });

        vBoxFromTransaction.addEventHandler(
                DragEvent.DRAG_OVER,
                event -> {
                    event.acceptTransferModes(TransferMode.COPY);
                    Dragboard dragboard = event.getDragboard();
                    if (dragboard.hasString()) {
                        if (dragboard.getString().equals("From")) {
                            vBoxFromTransaction.setStyle("-fx-border-color: #7999D7;");
                        }
                    }
                }
        );

        vBoxFromTransaction.addEventHandler(
                DragEvent.DRAG_DROPPED,
                event -> {
                    event.acceptTransferModes(TransferMode.COPY);
                    Dragboard dragboard = event.getDragboard();
                    if (dragboard.hasString()) {
                        if(dragboard.getString().equals("From")) {
                            Transaction transaction = tableViewFromAccount.getSelectionModel().getSelectedItem();
                            String transactionID = transaction.getTransactionID();
                            String date = transaction.getTransactionDate();
                            String description = transaction.getTransactionDescription();
                            String amount = transaction.getTransactionAmount();
                            addFromTransaction(transactionID, date, description, amount);
                        }
                    }
                    vBoxFromTransaction.setStyle("-fx-border-color: white;");
                    event.consume();
                }
        );

        vBoxFromTransaction.addEventHandler(
                DragEvent.DRAG_EXITED,
                event -> {
                    event.acceptTransferModes(TransferMode.COPY);
                    vBoxFromTransaction.setStyle("-fx-border-color: white;");
                    event.consume();
                }
        );

        tableViewToAccount.setOnDragDetected(event -> {
            Dragboard dragboard = tableViewToAccount.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("To");
            dragboard.setContent(content);
            event.consume();
        });

        vBoxToTransaction.addEventHandler(
                DragEvent.DRAG_OVER,
                event -> {
                    event.acceptTransferModes(TransferMode.COPY);
                    Dragboard dragboard = event.getDragboard();
                    if (dragboard.hasString()) {
                        if (dragboard.getString().equals("To")) {
                            vBoxToTransaction.setStyle("-fx-border-color: #7999D7;");
                        }
                    }
                }
        );

        vBoxToTransaction.addEventHandler(
                DragEvent.DRAG_DROPPED,
                event -> {
                    event.acceptTransferModes(TransferMode.COPY);
                    Dragboard dragboard = event.getDragboard();
                    if (dragboard.hasString()) {
                        if(dragboard.getString().equals("To")) {
                            Transaction transaction = tableViewToAccount.getSelectionModel().getSelectedItem();
                            String transactionID = transaction.getTransactionID();
                            String date = transaction.getTransactionDate();
                            String description = transaction.getTransactionDescription();
                            String amount = transaction.getTransactionAmount();
                            addToTransaction(transactionID, date, description, amount);
                        }
                    }
                    vBoxToTransaction.setStyle("-fx-border-color: white;");
                    event.consume();
                }
        );

        vBoxToTransaction.addEventHandler(
                DragEvent.DRAG_EXITED,
                event -> {
                    event.acceptTransferModes(TransferMode.COPY);
                    vBoxToTransaction.setStyle("-fx-border-color: white;");
                    event.consume();
                }
        );
    }

    private void fillFromTransactionTable() {
        String account = comboBoxFromAccount.getSelectionModel().getSelectedItem();
        tableViewFromAccount.getItems().clear();
        tableViewFromAccount.setItems(sqlTransfer.viewTransferTransactions(account));
    }

    private void fillToTransactionTable() {
        String account = comboBoxToAccount.getSelectionModel().getSelectedItem();
        tableViewToAccount.getItems().clear();
        tableViewToAccount.setItems(sqlTransfer.viewTransferTransactions(account));
    }

    public void setTransferID(String transferID) {
        this.transferID = transferID;
    }

    public void addFromTransaction(String transactionID, String date, String description, String amount) {
        labelFromDate.setText(date);
        labelFromDescription.setText(description);
        labelFromAmount.setText(amount);
        this.fromTransactionID = transactionID;

        labelFromDate.setStyle(labelWhiteBackgroundStyle);
        labelFromDescription.setStyle(labelWhiteBackgroundStyle);
        labelFromAmount.setStyle(labelWhiteBackgroundStyle);
    }

    public void addToTransaction(String transactionID, String date, String description, String amount) {
        labelToDate.setText(date);
        labelToDescription.setText(description);
        labelToAmount.setText(amount);
        this.toTransactionID = transactionID;

        labelToDate.setStyle(labelWhiteBackgroundStyle);
        labelToDescription.setStyle(labelWhiteBackgroundStyle);
        labelToAmount.setStyle(labelWhiteBackgroundStyle);
    }

    public void fillComboBoxes(String transferType, String fromAccount, String toAccount) {
        comboBoxTransferType.getItems().clear();
        comboBoxTransferType.getItems().addAll(sqlTransfer.listTransferCategories());
        comboBoxFromAccount.getItems().clear();
        comboBoxFromAccount.getItems().addAll(sqlAccount.listAccounts());
        comboBoxToAccount.getItems().clear();
        comboBoxToAccount.getItems().addAll(sqlAccount.listAccounts());

        if (transferType != null) {
            comboBoxTransferType.setValue(transferType);
        }

        if (fromAccount != null) {
            comboBoxFromAccount.setValue(fromAccount);
            tableViewFromAccount.getItems().clear();
            tableViewFromAccount.setItems(sqlTransfer.viewTransferTransactions(fromAccount));
        }

        if (toAccount != null) {
            comboBoxToAccount.setValue(toAccount);
            tableViewToAccount.getItems().clear();
            tableViewToAccount.setItems(sqlTransfer.viewTransferTransactions(toAccount));
        }

    }

    @FXML
    private void submitTransfer() {
        boolean requiredFieldsAreEmpty = false;
        if (comboBoxTransferType.getSelectionModel().getSelectedIndex() == -1) {
            comboBoxTransferType.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        }

        if (labelToAmount.getText().equals("") || labelToDate.getText().equals("")) {
            labelToAmount.setStyle(labelRedBackgroundStyle);
            labelToDate.setStyle(labelRedBackgroundStyle);
            labelToDescription.setStyle(labelRedBackgroundStyle);
            requiredFieldsAreEmpty = true;
        } else if (labelToDescription.getText().equals("") && comboBoxToAccount.getSelectionModel().getSelectedIndex() != -1) {
            labelToDescription.setText("Transfer to " + comboBoxToAccount.getSelectionModel().getSelectedItem());
        }

        if (labelFromAmount.getText().equals("") || labelToDate.getText().equals("")) {
            labelFromAmount.setStyle(labelRedBackgroundStyle);
            labelFromDate.setStyle(labelRedBackgroundStyle);
            labelFromDescription.setStyle(labelRedBackgroundStyle);
            requiredFieldsAreEmpty = true;
        } else if (labelFromDescription.getText().equals("") && comboBoxFromAccount.getSelectionModel().getSelectedIndex() != -1) {
            labelFromDescription.setText("Transfer to " + comboBoxFromAccount.getSelectionModel().getSelectedItem());
        }

        if (comboBoxFromAccount.getSelectionModel().getSelectedIndex() == -1) {
            comboBoxFromAccount.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        }

        if (comboBoxToAccount.getSelectionModel().getSelectedIndex() == -1) {
            comboBoxToAccount.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        }

        if (!requiredFieldsAreEmpty) {

            SQLTransfer sqlTransfer = new SQLTransfer();
            FormatDollarAmount formatDollarAmount = new FormatDollarAmount();
            FormatDate formatDate = new FormatDate();
            String transferDate = labelFromDate.getText();
            String transferAmount = labelFromAmount.getText();
            transferAmount = formatDollarAmount.CleanDollarAmountsForSQL(transferAmount);

            transferDate = formatDate.formatFromTableDate(transferDate);
            String transferType = comboBoxTransferType.getSelectionModel().getSelectedItem();
            sqlTransaction.updateTransactionCategory(this.fromTransactionID, new Budget().TRANSFER, transferType);
            sqlTransaction.updateTransactionCategory(this.toTransactionID, new Budget().TRANSFER, transferType);
            if (transferAmount.contains("-")) {
                transferAmount = transferAmount.replace("-", "");
            }
            sqlTransfer.updateTransfer(this.transferID, transferDate, this.fromTransactionID, this.toTransactionID, transferAmount);

            exitWindow();
        }
    }

    @FXML
    private void exitWindow() {
        Stage stage = (Stage)anchorPaneUpdateTransfer.getScene().getWindow();
        stage.close();
    }

    private ContextMenu contextMenuToTransaction() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addTransaction = new MenuItem("Add Transaction");

        addTransaction.setOnAction(event -> {
            Transaction transaction = tableViewToAccount.getSelectionModel().getSelectedItem();
            String transactionID = transaction.getTransactionID();
            String date = transaction.getTransactionDate();
            String description = transaction.getTransactionDescription();
            String amount = transaction.getTransactionAmount();
            addToTransaction(transactionID, date, description, amount);
        });

        contextMenu.getItems().add(addTransaction);

        return contextMenu;
    }

    private ContextMenu contextMenuFromTransaction() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addTransaction = new MenuItem("Add Transaction");

        addTransaction.setOnAction(event -> {
            Transaction transaction = tableViewFromAccount.getSelectionModel().getSelectedItem();
            String transactionID = transaction.getTransactionID();
            String date = transaction.getTransactionDate();
            String description = transaction.getTransactionDescription();
            String amount = transaction.getTransactionAmount();
            addFromTransaction(transactionID, date, description, amount);
        });

        contextMenu.getItems().add(addTransaction);

        return contextMenu;
    }

    @FXML
    private void newFromTransaction() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/newTransaction.fxml"));
        AnchorPane newWindow;
        try {
            newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle("New From Transaction");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(anchorPaneUpdateTransfer.getScene().getWindow());
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);

            NewTransactionController newTransactionController = loader.getController();
            newTransactionController.addAccountToCombo(comboBoxFromAccount.getSelectionModel().getSelectedItem());
            newTransactionController.addBudgetToCombo(new Budget().TRANSFER);
            newTransactionController.transferType = -1;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fillFromTransactionTable();
    }

    @FXML
    private void newToTransaction() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/newTransaction.fxml"));
        AnchorPane newWindow;
        try {
            newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle("New To Transaction");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(anchorPaneUpdateTransfer.getScene().getWindow());
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);

            NewTransactionController newTransactionController = loader.getController();
            newTransactionController.addAccountToCombo(comboBoxToAccount.getSelectionModel().getSelectedItem());
            newTransactionController.addBudgetToCombo(new Budget().TRANSFER);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fillToTransactionTable();
    }
}
