package com.moneymaker.modules.transactionmanager.transactions;

import com.moneymaker.modules.accountmanager.SQLAccount;
import com.moneymaker.modules.budgetmanager.SQLBudget;
import com.moneymaker.modules.goalmanager.SQLGoal;
import com.moneymaker.utilities.AutoCompleteComboBoxListener;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import com.moneymaker.utilities.SQLMethods;
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
 * Created for MoneyMaker by Jay Damon on 9/18/2016.
 */
public class UpdateTransactionController implements Initializable {

    @FXML
    TextField txtDescription, txtAmount;

    @FXML
    ComboBox<String> cmbAccounts;
    @FXML
    ComboBox<String> cmbBudget;
    @FXML
    ComboBox<String> cmbType;
    @FXML
    ComboBox<String> cmbCategory;
    @FXML
    ComboBox<String> cmbRecurring;

    @FXML
    DatePicker datePickerStartDate;

    @FXML
    Button btnExit;

    private String transactionID;

    private SQLAccount sqlAccount = new SQLAccount();
    private SQLBudget sqlBudget = new SQLBudget();
    private SQLTransaction sqlTransaction = new SQLTransaction();
    private SQLMethods sqlMethods = new SQLMethods();
    private SQLGoal sqlGoal = new SQLGoal();

    private String whiteBackgroundStyle = "-fx-control-inner-background: white";

    public void initialize(URL url, ResourceBundle rs) {
        new AutoCompleteComboBoxListener(cmbAccounts);
        new AutoCompleteComboBoxListener(cmbBudget);
        new AutoCompleteComboBoxListener(cmbCategory);
        new AutoCompleteComboBoxListener(cmbType);
        new AutoCompleteComboBoxListener(cmbRecurring);

        cmbAccounts.getItems().clear();
        cmbAccounts.getItems().addAll(sqlAccount.listAccounts());

        cmbBudget.getItems().clear();
        cmbBudget.getItems().addAll(sqlBudget.listActiveBudget());

        cmbType.getItems().clear();
        cmbType.getItems().addAll(sqlMethods.listTranType());

        cmbRecurring.getItems().clear();
        cmbRecurring.getItems().addAll(sqlTransaction.listRecurringTransactions());
        cmbRecurring.getItems().add("Not Recurring");

        txtDescription.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!txtDescription.getText().isEmpty()) {
                    txtDescription.setStyle(whiteBackgroundStyle);
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

        cmbBudget.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (cmbBudget.getSelectionModel().getSelectedIndex() != -1) {
                    cmbBudget.setStyle(whiteBackgroundStyle);

                    String selectedBudget = cmbBudget.getSelectionModel().getSelectedItem();

                    if (selectedBudget.equals("Goal")) {
                        cmbCategory.getItems().clear();
                        cmbCategory.getItems().addAll(sqlGoal.listGoals());
                    } else {
                        cmbCategory.getItems().clear();
                        cmbCategory.getItems().addAll(sqlTransaction.listTransactionCategory(cmbBudget.getSelectionModel().getSelectedItem()));
                    }
                }
            }
        }));


        cmbCategory.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (cmbCategory.getSelectionModel().getSelectedIndex() != -1) {
                    cmbCategory.setStyle(whiteBackgroundStyle);
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

    public void fillComboAndTextBoxes(String transactionID, String description, String account, String budget, String category, String recurring, String amount) {

        String type;

        amount = new FormatDollarAmount().CleanDollarAmountsForSQL(amount);

        if (Float.parseFloat(amount) < 0) {
            type = new Transaction().EXPENSE;
            amount = amount.replace("-", "");
        } else {
            type = new Transaction().INCOME;
        }

        cmbCategory.getItems().clear();
        cmbCategory.getItems().addAll(sqlTransaction.listTransactionCategory(budget));

        this.transactionID = transactionID;
        txtDescription.setText(description);
        cmbAccounts.setValue(account);
        cmbBudget.setValue(budget);

        if (budget.equals("Goal")) {
            cmbCategory.getItems().clear();
            cmbCategory.getItems().addAll(sqlGoal.listGoals());
        }

        cmbCategory.setValue(category);
        cmbType.setValue(type);
        txtAmount.setText(amount);

        if (recurring != null) {
            cmbRecurring.setValue(recurring);
        }

    }

    public void fillDatePickers(String startDate) {

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
        String transactionDescription = txtDescription.getText();
        String amount = txtAmount.getText();

        int accountIndex = cmbAccounts.getSelectionModel().getSelectedIndex();
        int categoryIndex = cmbCategory.getSelectionModel().getSelectedIndex();
        int typeIndex = cmbType.getSelectionModel().getSelectedIndex();

        boolean requiredFieldsAreEmpty = false;

        String redBackgroundStyle = "-fx-control-inner-background: red";
        if (transactionDescription.isEmpty()) {
            txtDescription.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        } else {
            txtDescription.setStyle(whiteBackgroundStyle);
        }

        if (datePickerStartDate.getValue() == null) {
            datePickerStartDate.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        } else {
            datePickerStartDate.setStyle(whiteBackgroundStyle);
        }

        if (typeIndex == -1) {
            cmbType.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        } else {
            cmbType.setStyle(whiteBackgroundStyle);
        }

        if (accountIndex == -1) {
            cmbAccounts.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        } else {
            cmbAccounts.setStyle(whiteBackgroundStyle);
        }

        if (categoryIndex == -1) {
            cmbCategory.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        } else {
            cmbCategory.setStyle(whiteBackgroundStyle);
        }

        if (amount.isEmpty()) {
            txtAmount.setStyle(redBackgroundStyle);
            requiredFieldsAreEmpty = true;
        } else {
            txtAmount.setStyle(whiteBackgroundStyle);
        }

        if (!requiredFieldsAreEmpty) {
            String account = cmbAccounts.getSelectionModel().getSelectedItem();
            String category = cmbCategory.getSelectionModel().getSelectedItem();
            String type = cmbType.getSelectionModel().getSelectedItem();
            String startDate = datePickerStartDate.getValue().toString();

            String budget = "NULL";
            String recurringTransaction  = "NULL";

            if (cmbBudget.getValue() != null) {
                budget = cmbBudget.getSelectionModel().getSelectedItem();
            }
            if (cmbRecurring.getValue() != null) {
                recurringTransaction = cmbRecurring.getSelectionModel().getSelectedItem();
            }

            if (type.equals(new Transaction().EXPENSE)) {
                amount = "-" + amount;
            }

            sqlTransaction.updateTransaction(this.transactionID, transactionDescription, account, budget, category, recurringTransaction, startDate, amount);

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
