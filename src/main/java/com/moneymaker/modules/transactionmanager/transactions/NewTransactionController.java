package com.moneymaker.modules.transactionmanager.transactions;

import com.moneymaker.modules.accountmanager.SQLAccount;
import com.moneymaker.modules.budgetmanager.SQLBudget;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by Jay Damon on 2/6/2016.
 */
public class NewTransactionController implements Initializable {

    @FXML
    private ComboBox<String> cmbAccount, cmbBudget;

    public int transferType = 0;

    private final SQLBudget budSQL = new SQLBudget();
    private final SQLAccount accSQL = new SQLAccount();
    private final SQLTransaction tranSQL = new SQLTransaction();

    @FXML private DatePicker transactionDatePicker;
    @FXML private TextField transactionDescriptionField;
    @FXML private TextField transactionAmountField;
    @FXML private Button btnExit;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
            listAccounts();
            listBudgets();
    }

    //Get list of accounts to fill "Accounts" combobox
    @FXML
    private void listAccounts() {
        ObservableList<String> accountListString = FXCollections.observableList(accSQL.listAccounts());
        cmbAccount.getItems().clear();
        cmbAccount.setItems(accountListString);
    }

    @FXML
    private void listBudgets() {
        ObservableList<String> budgetListString = FXCollections.observableList(budSQL.listActiveBudget());
        cmbBudget.getItems().clear();
        cmbBudget.setItems(budgetListString);
    }

    @FXML
    protected void addNewTransaction() throws SQLException {

        String account = cmbAccount.getSelectionModel().getSelectedItem();
        String budget = cmbBudget.getSelectionModel().getSelectedItem();
        String category = "NULL";
        String date = transactionDatePicker.getValue().toString();
        String description = transactionDescriptionField.getText();
        String amount = transactionAmountField.getText();

        if (transferType == -1) {
            amount = "-" + amount;
        }

        tranSQL.newTransaction(account, budget, category, date, description, amount);

        exitWindow();

    }

    @FXML
    protected void exitWindow() {
        Stage stage = (Stage)btnExit.getScene().getWindow();
        stage.close();
    }

    public void addAccountToCombo(String accountName) {
        cmbAccount.setValue(accountName);
    }

    public void addBudgetToCombo(String budgetName) {
        cmbBudget.setValue(budgetName);
    }
}


