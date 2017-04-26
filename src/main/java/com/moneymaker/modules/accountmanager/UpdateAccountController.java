package com.moneymaker.modules.accountmanager;

import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by jaynd on 2/22/2016.
 */
public class UpdateAccountController implements Initializable {

    @FXML
    private TextField textFieldAccountName, textFieldStartingBalance;
    @FXML
    private Button btnActAddAccount, btnExit;
    @FXML
    private ComboBox<String> cmbAccType;
    @FXML
    private CheckBox checkBoxPrimaryAccount, checkBoxInCashFlow;

    private int accountID;

    private SQLAccount accSQL = new SQLAccount();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showAccountTypes();
    }

    public void fillFields(int accountID, String accountName, String accountType, String startingBalance, int primaryAccount, int isInCashFlow) {
        this.accountID = accountID;
        ObservableList<String> accountTypeListString = FXCollections.observableList(accSQL.listAccountTypes());
        cmbAccType.getItems().clear();
        cmbAccType.setItems(accountTypeListString);
        if (accountType != null) {
            cmbAccType.setValue(accountType);
        }
        textFieldAccountName.setText(accountName);
        textFieldStartingBalance.setText(startingBalance);

        if (primaryAccount == 1) {
            checkBoxPrimaryAccount.setSelected(true);
        }

        if (isInCashFlow == 1) {
            checkBoxInCashFlow.setSelected(true);
        }
    }

    @FXML
    private void showAccountTypes() {

    }

    @FXML
    protected void updateAccount() {

        String accType = cmbAccType.getSelectionModel().getSelectedItem();
        String accountName = textFieldAccountName.getText();
        String startingBalance = textFieldStartingBalance.getText();
        int primary = 0;
        int inCashFlow = 0;

        if (checkBoxPrimaryAccount.isSelected()) {
            primary = 1;
        }
        if (checkBoxInCashFlow.isSelected()) {
            inCashFlow = 1;
        }

        FormatDollarAmount formatDollarAmount = new FormatDollarAmount();
        startingBalance = formatDollarAmount.CleanDollarAmountsForSQL(startingBalance);

        accSQL.updateAccount(accType, accountName, startingBalance, primary, inCashFlow, this.accountID);

        Stage stage = (Stage)btnActAddAccount.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void exitWindow() {
        Stage stage = (Stage)btnExit.getScene().getWindow();
        stage.close();
    }
}
