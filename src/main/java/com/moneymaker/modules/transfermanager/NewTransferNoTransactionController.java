package com.moneymaker.modules.transfermanager;

import com.moneymaker.modules.budgetmanager.Budget;
import com.moneymaker.modules.accountmanager.SQLAccount;
import com.moneymaker.modules.transactionmanager.transactions.SQLTransaction;
import com.moneymaker.utilities.AutoCompleteComboBoxListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by Jay Damon on 10/22/2016.
 */
public class NewTransferNoTransactionController implements Initializable {

    @FXML
    private ComboBox<String> comboBoxFromAccount, comboBoxToAccount,comboBoxTransferType;

    @FXML
    private AnchorPane anchorPaneNewTransfer;

    @FXML
    private DatePicker datePickerTransferDate;

    @FXML
    private TextField textFieldTransferAmount;

    private String redBackgroundStyle = "-fx-control-inner-background: red";
    private String whiteBackgroundStyle = "-fx-control-inner-background: white";

    public void initialize(URL url, ResourceBundle rs) {
        new AutoCompleteComboBoxListener(comboBoxTransferType);
        new AutoCompleteComboBoxListener(comboBoxFromAccount);
        new AutoCompleteComboBoxListener(comboBoxToAccount);

        fillComboBoxes();
    }

    private void fillComboBoxes() {
        SQLAccount sqlAccount = new SQLAccount();
        SQLTransfer sqlTransfer = new SQLTransfer();

        comboBoxTransferType.getItems().clear();
        comboBoxTransferType.getItems().addAll(sqlTransfer.listTransferCategories());
        comboBoxToAccount.getItems().clear();
        comboBoxToAccount.setItems(sqlAccount.listAccounts());
        comboBoxFromAccount.getItems().clear();
        comboBoxFromAccount.setItems(sqlAccount.listAccounts());

        comboBoxTransferType.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (comboBoxTransferType.getSelectionModel().getSelectedIndex() != -1) {
                comboBoxTransferType.setStyle(whiteBackgroundStyle);
            }
        });

        comboBoxToAccount.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (comboBoxToAccount.getSelectionModel().getSelectedIndex() != -1) {
                comboBoxToAccount.setStyle(whiteBackgroundStyle);
            }
        });

        comboBoxFromAccount.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (comboBoxFromAccount.getSelectionModel().getSelectedIndex() != -1) {
                comboBoxFromAccount.setStyle(whiteBackgroundStyle);
            }
        });

        datePickerTransferDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (datePickerTransferDate.getValue() != null) {
                datePickerTransferDate.setStyle(whiteBackgroundStyle);
            }
        });

        textFieldTransferAmount.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (textFieldTransferAmount.getText() != null) {
                textFieldTransferAmount.setStyle(whiteBackgroundStyle);
            }
        });
    }

    @FXML
    private void submitTransfer() {
        boolean requiredFieldsAreEmpty = false;
        if (comboBoxTransferType.getSelectionModel().getSelectedIndex() == -1) {
            comboBoxTransferType.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        }
        if (comboBoxFromAccount.getSelectionModel().getSelectedIndex() == -1) {
            comboBoxFromAccount.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        }
        if (comboBoxToAccount.getSelectionModel().getSelectedIndex() == -1) {
            comboBoxToAccount.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        }

        if (datePickerTransferDate.getValue() == null) {
            datePickerTransferDate.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        }

        if (textFieldTransferAmount.getText().equals("")) {
            textFieldTransferAmount.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        }

        if (!requiredFieldsAreEmpty) {

            SQLTransaction sqlTransaction = new SQLTransaction();
            SQLTransfer sqlTransfer = new SQLTransfer();
            String transferType = "\"" + comboBoxTransferType.getSelectionModel().getSelectedItem() + "\"";
            String fromAccount = comboBoxFromAccount.getSelectionModel().getSelectedItem();
            String toAccount = comboBoxToAccount.getSelectionModel().getSelectedItem();
            String transferDate = datePickerTransferDate.getValue().toString();
            String transferFromDescription = "Transfer From " + fromAccount;
            String transferToDescription = "Transfer To " + toAccount;
            String transferAmount = textFieldTransferAmount.getText();

            sqlTransaction.newTransaction(fromAccount, new Budget().TRANSFER, transferType, transferDate, transferFromDescription, "-" + transferAmount);
            sqlTransaction.newTransaction(toAccount, new Budget().TRANSFER, transferType, transferDate, transferToDescription, transferAmount);

            int fromTransactionID = sqlTransfer.getTransferTransactionID(fromAccount, transferDate, transferFromDescription, "-" + transferAmount);
            int toTransactionID = sqlTransfer.getTransferTransactionID(toAccount, transferDate, transferToDescription, transferAmount);

            sqlTransfer.newTransfer(transferDate, Integer.toString(fromTransactionID), Integer.toString(toTransactionID), transferAmount);

            exitWindow();
        }
    }

    @FXML
    private void exitWindow() {
        Stage stage = (Stage)anchorPaneNewTransfer.getScene().getWindow();
        stage.close();
    }
}
