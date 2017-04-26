package com.moneymaker.modules.transactionmanager.recurringtransactions;

import com.moneymaker.modules.accountmanager.SQLAccount;
import com.moneymaker.modules.budgetmanager.SQLBudget;
import com.moneymaker.utilities.SQLMethods;
import com.moneymaker.modules.transactionmanager.transactions.SQLTransaction;
import com.moneymaker.utilities.AutoCompleteComboBoxListener;
import com.moneymaker.utilities.FormatDate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Created by Jay Damon on 9/18/2016.
 */
public class UpdateRecurringTransactionController implements Initializable {

    @FXML
    TextField txtName, txtAmount;

    @FXML
    ComboBox<String> cmbAccounts;
    @FXML
    ComboBox<String> cmbBudget;
    @FXML
    ComboBox<String> cmbFrequency;
    @FXML
    ComboBox<String> cmbOccurrence;
    @FXML
    ComboBox<String> cmbType;

    @FXML
    DatePicker datePickerStartDate, datePickerEndDate;

    @FXML
    Button btnExit;

    private String transactionID;

    private SQLAccount sqlAccount = new SQLAccount();
    private SQLBudget sqlBudget = new SQLBudget();
    private SQLTransaction sqlTransaction = new SQLTransaction();
    private SQLMethods sqlMethods = new SQLMethods();

    private String whiteBackgroundStyle = "-fx-control-inner-background: white";
    private String redBackgroundStyle = "-fx-control-inner-background: red";

    public void initialize(URL url, ResourceBundle rs) {
        new AutoCompleteComboBoxListener(cmbAccounts);
        new AutoCompleteComboBoxListener(cmbBudget);
        new AutoCompleteComboBoxListener(cmbFrequency);
        new AutoCompleteComboBoxListener(cmbOccurrence);
        new AutoCompleteComboBoxListener(cmbType);

        cmbAccounts.getItems().clear();
        cmbAccounts.getItems().addAll(sqlAccount.listAccounts());

        cmbBudget.getItems().clear();
        cmbBudget.getItems().addAll(sqlBudget.listActiveBudget());

        cmbFrequency.getItems().clear();
        cmbFrequency.getItems().addAll(sqlMethods.listFrequency());

        cmbOccurrence.getItems().clear();
        cmbOccurrence.getItems().addAll(sqlMethods.listOccurrence());

        cmbType.getItems().clear();
        cmbType.getItems().addAll(sqlMethods.listTranType());

        txtName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!txtName.getText().isEmpty()) {
                    txtName.setStyle(whiteBackgroundStyle);
                }
            }
        });

//        TODO this doesnt really work for the comboboxes when the "AutoCompleteComboBoxListener" fires because it selects the value after the event handler fires
        cmbAccounts.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (cmbAccounts.getSelectionModel().getSelectedIndex() != -1) {
                    cmbAccounts.setStyle(whiteBackgroundStyle);
                }
            }
        }));

        cmbFrequency.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (cmbFrequency.getSelectionModel().getSelectedIndex() != -1) {
                    cmbFrequency.setStyle(whiteBackgroundStyle);
                }
            }
        }));

        cmbOccurrence.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (cmbOccurrence.getSelectionModel().getSelectedIndex() != -1) {
                    cmbOccurrence.setStyle(whiteBackgroundStyle);
                }
            }
        }));

        cmbType.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (cmbType.getSelectionModel().getSelectedIndex() != -1) {
                    cmbType.setStyle(whiteBackgroundStyle);
                }
            }
        }));

        datePickerStartDate.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (datePickerStartDate.getValue() != null) {
                    datePickerStartDate.setStyle(whiteBackgroundStyle);
                }
            }
        }));

    }

    public void fillComboAndTextBoxes(String transactionID, String name, String account, String budget, String frequency, String occurance, String type, String amount) {
        this.transactionID = transactionID;
        txtName.setText(name);
        cmbAccounts.setValue(account);
        cmbBudget.setValue(budget);
        cmbFrequency.setValue(frequency);
        cmbOccurrence.setValue(occurance);
        cmbType.setValue(type);
        txtAmount.setText(amount);

    }

    public void fillDatePickers(String endDate, String startDate) {
        if(endDate != null) {
            FormatDate formatDate = new FormatDate();
            Calendar calendarEndDate = formatDate.parseStringCalendar(formatDate.formatFromTableDate(endDate));

            int selectedEndYear = calendarEndDate.get(Calendar.YEAR);
            int selectedEndMonth = calendarEndDate.get(Calendar.MONTH) + 1;
            int selectedEndDay = calendarEndDate.get(Calendar.DAY_OF_MONTH);

            datePickerEndDate.setValue(LocalDate.of(selectedEndYear, selectedEndMonth, selectedEndDay));
        }

        if(startDate != null) {
            FormatDate formatDate = new FormatDate();
            Calendar calendarStartDate = formatDate.parseStringCalendar(formatDate.formatFromTableDate(startDate));

            int selectedStartYear = calendarStartDate.get(Calendar.YEAR);
            int selectedStartMonth = calendarStartDate.get(Calendar.MONTH) + 1;
            int selectedStartDay = calendarStartDate.get(Calendar.DAY_OF_MONTH);
            datePickerStartDate.setValue(LocalDate.of(selectedStartYear, selectedStartMonth, selectedStartDay));
        }
    }

    @FXML
    protected void buttonClickUpdateTransaction() {
        String transactionName = txtName.getText();
        String amount = txtAmount.getText();

        int accountIndex = cmbAccounts.getSelectionModel().getSelectedIndex();
        int frequencyIndex = cmbFrequency.getSelectionModel().getSelectedIndex();
        int occurrenceIndex = cmbOccurrence.getSelectionModel().getSelectedIndex();
        int typeIndex = cmbType.getSelectionModel().getSelectedIndex();

        boolean areRequiredFieldsEmpty = false;

        if (transactionName.isEmpty()) {
            txtName.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            txtName.setStyle(whiteBackgroundStyle);
        }

        if (datePickerStartDate.getValue() == null) {
            datePickerStartDate.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            datePickerStartDate.setStyle(whiteBackgroundStyle);
        }

        if (typeIndex == -1) {
            cmbType.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            cmbType.setStyle(whiteBackgroundStyle);
        }

        if (accountIndex == -1) {
            cmbAccounts.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            cmbAccounts.setStyle(whiteBackgroundStyle);
        }

        if (frequencyIndex == -1) {
            cmbFrequency.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            cmbFrequency.setStyle(whiteBackgroundStyle);
        }

        if (occurrenceIndex == -1) {
            cmbOccurrence.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            cmbOccurrence.setStyle(whiteBackgroundStyle);
        }

        if (amount.isEmpty()) {
            txtAmount.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            txtAmount.setStyle(whiteBackgroundStyle);
        }

        if (!areRequiredFieldsEmpty) {
            String account = cmbAccounts.getSelectionModel().getSelectedItem();
            String frequency = cmbFrequency.getSelectionModel().getSelectedItem();
            String occurrence = cmbOccurrence.getSelectionModel().getSelectedItem();
            String type = cmbType.getSelectionModel().getSelectedItem();
            String startDate = datePickerStartDate.getValue().toString();

            String endDate = "NULL";
            String budget = "NULL";

            if (cmbBudget.getValue() != null) {
                budget = cmbBudget.getSelectionModel().getSelectedItem();
            }

            if (datePickerEndDate.getValue() != null) {
                endDate = datePickerEndDate.getValue().toString();
            }

            sqlTransaction.updateRecurringTransaction(this.transactionID, transactionName, account, budget, frequency, occurrence, type, startDate, endDate, amount);

            Stage stage = (Stage) btnExit.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    protected void exitWindow() {
        Stage stage = (Stage)btnExit.getScene().getWindow();
        stage.close();
    }
}
