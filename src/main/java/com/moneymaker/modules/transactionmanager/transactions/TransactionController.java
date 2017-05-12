package com.moneymaker.modules.transactionmanager.transactions;

import com.moneymaker.modules.budgetmanager.Budget;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created for MoneyMaker by jaynd on 3/27/2016.
 */
public class TransactionController implements Initializable {

    @FXML
    private TableView<Transaction> tblTransactions;

    @FXML
    private TableColumn<Budget, String> colAmount;

    @FXML
    private Button btnNewEntry, btnImportTransactions, buttonUpdateTransaction;

    private final SQLTransaction tranSQL = new SQLTransaction();

    private int visibleType = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tblTransactions.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                AnchorPane pane = new AnchorPane();
                ContextMenu contextMenu = launchContextMenu();
                tblTransactions.setContextMenu(contextMenu);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
            }
            tblTransactions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            event.consume();
        });

        tblTransactions.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                ObservableList<Transaction> transactions = tblTransactions.getSelectionModel().getSelectedItems();
                deleteTransaction(transactions);
                showType();
            }
        });
    }

    public void showTransactions() {
        visibleType = 0;
        ObservableList<Transaction> transactions = tblTransactions.getItems();
        transactions.clear();

        transactions.addAll(tranSQL.viewTransactions());

        Collections.sort(transactions, new Comparator<Transaction>() {
            DateFormat f = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
            @Override
            public int compare(Transaction t1, Transaction t2) {
                try {
                    return f.parse(t1.getTransactionDate()).compareTo(f.parse(t2.getTransactionDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });

        setCatStyle();
    }

    public void showExpenses() {
        visibleType = -1;
        ObservableList<Transaction> transactions = tblTransactions.getItems();
        transactions.clear();

        for (Transaction t : tranSQL.viewTransactions()) {
            String stringTransactionAmount = new FormatDollarAmount().CleanDollarAmountsForSQL(t.getTransactionAmount());
            Float transactionAmount = Float.parseFloat(stringTransactionAmount);

            if (transactionAmount <= 0 && !t.getTransactionBudget().equals(new Budget().TRANSFER)) {
                transactions.add(t);
            }
        }

        Collections.sort(transactions, new Comparator<Transaction>() {
            DateFormat f = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
            @Override
            public int compare(Transaction t1, Transaction t2) {
                try {
                    return f.parse(t1.getTransactionDate()).compareTo(f.parse(t2.getTransactionDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });

        setCatStyle();
    }

    public void showIncome() {
        visibleType = 1;
        ObservableList<Transaction> transactions = tblTransactions.getItems();
        transactions.clear();

        for (Transaction t : tranSQL.viewTransactions()) {
            String stringTransactionAmount = new FormatDollarAmount().CleanDollarAmountsForSQL(t.getTransactionAmount());
            Float transactionAmount = Float.parseFloat(stringTransactionAmount);

            if (transactionAmount > 0 && !t.getTransactionBudget().equals(new Budget().TRANSFER)) {
                transactions.add(t);
            }
        }

        Collections.sort(transactions, new Comparator<Transaction>() {
            DateFormat f = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
            @Override
            public int compare(Transaction t1, Transaction t2) {
                try {
                    return f.parse(t1.getTransactionDate()).compareTo(f.parse(t2.getTransactionDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });

        setCatStyle();
    }

    public void showActualRecurring() {
        ObservableList<Transaction> transactions = tblTransactions.getItems();
        transactions.clear();

        transactions.addAll(tranSQL.viewTransactions().stream().filter(t -> t.getTransactionRecurring() != null).collect(Collectors.toList()));

        Collections.sort(transactions, new Comparator<Transaction>() {
            DateFormat f = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
            @Override
            public int compare(Transaction t1, Transaction t2) {
                try {
                    return f.parse(t1.getTransactionDate()).compareTo(f.parse(t2.getTransactionDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });

        setCatStyle();
    }

    public void showIncomeTransferTransactions() {
        ObservableList<Transaction> transactions = tblTransactions.getItems();
        transactions.clear();

        for (Transaction t : tranSQL.viewTransactions()) {
            String stringTransactionAmount = new FormatDollarAmount().CleanDollarAmountsForSQL(t.getTransactionAmount());
            Float transactionAmount = Float.parseFloat(stringTransactionAmount);

            if (transactionAmount > 0 && t.getTransactionBudget().equals(new Budget().TRANSFER)) {
                transactions.add(t);
            }
        }

        Collections.sort(transactions, new Comparator<Transaction>() {
            DateFormat f = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
            @Override
            public int compare(Transaction t1, Transaction t2) {
                try {
                    return f.parse(t1.getTransactionDate()).compareTo(f.parse(t2.getTransactionDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });

        setCatStyle();
    }

    public void showExpenseTransferTransactions() {
        ObservableList<Transaction> transactions = tblTransactions.getItems();
        transactions.clear();

        for (Transaction t : tranSQL.viewTransactions()) {
            String stringTransactionAmount = new FormatDollarAmount().CleanDollarAmountsForSQL(t.getTransactionAmount());
            Float transactionAmount = Float.parseFloat(stringTransactionAmount);

            if (transactionAmount <= 0 && t.getTransactionBudget().equals(new Budget().TRANSFER)) {
                transactions.add(t);
            }
        }

        Collections.sort(transactions, new Comparator<Transaction>() {
            DateFormat f = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
            @Override
            public int compare(Transaction t1, Transaction t2) {
                try {
                    return f.parse(t1.getTransactionDate()).compareTo(f.parse(t2.getTransactionDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });

        setCatStyle();
    }

    private void setCatStyle() {

        colAmount.setCellFactory(column -> new TableCell<Budget, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null || !empty) {

                    FormatDollarAmount formatDollarAmount = new FormatDollarAmount();

                    String finalVal = formatDollarAmount.CleanDollarAmountsForSQL(item);

                    BigDecimal colAmount = new BigDecimal(finalVal);
                    int comp = colAmount.compareTo(BigDecimal.ZERO);

                    switch (comp) {
                        case -1:
                            setText(item);
                            setStyle("-fx-background-color: #ff0000; -fx-font-weight: bold; -fx-text-background-color: white;");
                            break;
                        case 1:
                            setText(item);
                            setStyle("-fx-background-color: #003300; -fx-font-weight: bold; -fx-text-background-color: white;");
                            break;
                        case 0:
                            setText(item);
                            setStyle("-fx-background-color: #ffff99; -fx-font-weight: bold; -fx-text-background-color: white;");
                            break;
                    }
                }
            }
        });
    }

    @FXML
    public void openImportWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/transactions/transactions/importTransactions.fxml"));
        try {
            AnchorPane newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle(("Import"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnImportTransactions.getScene().getWindow());
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showTransactions();
    }

    @FXML
    private void newTransaction() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/transactions/transactions/newTransaction.fxml"));
        AnchorPane newWindow;

        try {
            newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle("New Transaction");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnNewEntry.getScene().getWindow());
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showType();
    }

    private void setRecurringTransaction() {
        SQLTransaction sqlTransaction = new SQLTransaction();
        AnchorPane newWindow = new AnchorPane();
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10,10,10,10));
        hBox.setPadding(new Insets(10,10,10,10));
        newWindow.getChildren().add(hBox);
        Label label = new Label("Recurring Transaction");
        label.setPrefWidth(150);
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPrefWidth(200);
        comboBox.setItems(sqlTransaction.listRecurringTransactions());

        hBox.getChildren().addAll(label, comboBox);

        Button button = new Button("Submit");

        button.setOnAction(event -> {
            if (comboBox.getSelectionModel().getSelectedIndex() != -1) {

            }
        });

        vBox.getChildren().setAll(hBox, button);
        newWindow.getChildren().add(vBox);

        Stage stage = new Stage();
        stage.setTitle("Set Recurring Transaction");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnNewEntry.getScene().getWindow());
        Scene scene = new Scene(newWindow);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void buttonClickDeleteTransaction() {
        ObservableList<Transaction> transactions = tblTransactions.getSelectionModel().getSelectedItems();
        deleteTransaction(transactions);
        showType();
    }

    private void deleteTransaction(ObservableList<Transaction> transactions) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Selected Transactions");
        alert.setContentText("Are you sure you want to delete the selected transactions? " +
                "This will permanently delete them.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {

                for (Transaction t : transactions) {
                    if (t != null) {
                        int tranID = Integer.parseInt(t.getTransactionID());
                        tranSQL.deleteTransaction(tranID);
                    }
                }
            }
        }
        showType();
    }

    @FXML
    protected void updateTransaction() {
        ObservableList<Transaction> transactions = tblTransactions.getSelectionModel().getSelectedItems();

        if(transactions.size() == 1) {
            for (Transaction t : transactions) {
                String transactionID = t.getTransactionID();
                String transactionAccount = t.getTransactionAccount();
                String transactionBudget = t.getTransactionBudget();
                String transactionCategory = t.getTransactionCategory();
                String transactionRecurring = t.getTransactionRecurring();
                String transactionDate = t.getTransactionDate();
                String transactionDescription = t.getTransactionDescription();
                String transactionAmount = t.getTransactionAmount();

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/transactions/transactions/updateTransactionWindow.fxml"));
                try {
                    AnchorPane newWindow = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Update Transaction");
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(buttonUpdateTransaction.getScene().getWindow());
                    Scene scene = new Scene(newWindow);
                    stage.setScene(scene);

                    UpdateTransactionController updateTransactionController = loader.getController();
                    updateTransactionController.fillDatePickers(transactionDate);
                    updateTransactionController.fillComboAndTextBoxes(transactionID, transactionDescription, transactionAccount,
                                                                      transactionBudget, transactionCategory, transactionRecurring, transactionAmount);

                    stage.showAndWait();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        showType();
    }

    private ContextMenu launchContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem newTransaction = new MenuItem("New");
        MenuItem updateTransaction = new MenuItem("Update");
        MenuItem deleteTransaction = new MenuItem("Delete");
        MenuItem importTransactions = new MenuItem("Import");
        MenuItem setRecurringTransaction = new MenuItem("Set Recurring");

        newTransaction.setOnAction(event -> {
            newTransaction();
            event.consume();
        });

        updateTransaction.setOnAction(event -> {
            updateTransaction();
            event.consume();
        });

        deleteTransaction.setOnAction(event -> {
            buttonClickDeleteTransaction();
            event.consume();
        });

        importTransactions.setOnAction(event -> {
            openImportWindow();
            event.consume();
        });

        setRecurringTransaction.setOnAction(event -> {
            setRecurringTransaction();
            event.consume();
        });

        contextMenu.getItems().addAll(newTransaction, updateTransaction, deleteTransaction, importTransactions, setRecurringTransaction);

        return contextMenu;
    }

    private void showType() {
        switch (visibleType) {
            case -1:
                showExpenses();
                break;
            case 0:
                showTransactions();
                break;
            case 1:
                showIncome();
                break;
        }
    }

}
