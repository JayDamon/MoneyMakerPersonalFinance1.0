package com.moneymaker.modules.accountmanager;

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
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by jaynd on 2/22/2016.
 */
public class NewAccountController implements Initializable {

    @FXML
    private TextField accountNameField, startingBalanceField;
    @FXML
    private Button btnActAddAccount, btnExit;
    @FXML
    private ComboBox<String> cmbAccType;
    @FXML
    private CheckBox checkBoxPrimaryAccount, checkBoxInCashFlow;

    private SQLAccount accSQL = new SQLAccount();

    @FXML
    private void showAccountTypes() throws SQLException {
        ObservableList<String> accountTypeListString = FXCollections.observableList(accSQL.listAccountTypes());
        cmbAccType.getItems().clear();
        cmbAccType.setItems(accountTypeListString);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            showAccountTypes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void addAccount() {

        String accType = cmbAccType.getSelectionModel().getSelectedItem();
        String accountName = accountNameField.getText();
        String startingBalance = startingBalanceField.getText();
        int primary = 0;
        int inCashFlow = 0;
        if (checkBoxPrimaryAccount.isSelected()) {
            primary = 1;
        }
        if (checkBoxInCashFlow.isSelected()) {
            inCashFlow = 1;
        }

        accSQL.addAccount(accType, accountName, startingBalance, primary, inCashFlow);

        Stage stage = (Stage)btnActAddAccount.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void exitWindow() {
        Stage stage = (Stage)btnExit.getScene().getWindow();
        stage.close();
    }
}
