package com.moneymaker.modules.budgetmanager;

import com.moneymaker.utilities.SQLMethods;
import com.moneymaker.utilities.AutoCompleteComboBoxListener;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;


/**
 * Created for MoneyMaker by jaynd on 6/27/2016.
 */
public class UpdateBudgetController implements Initializable{

    @FXML private ComboBox<String> comboBoxBudgetFrequency;

    @FXML private Button buttonExit, buttonAddAccount;

    @FXML private TextField textFieldStartingBalance;

    @FXML private Label budgetType;

    @FXML private DatePicker datePickerStartDate, datePickerEndDate;

    private final SQLBudget budSQL = new SQLBudget();
    private final SQLMethods sqlMethod = new SQLMethods();

    private String budgetID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            fillFrequency();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new AutoCompleteComboBoxListener(comboBoxBudgetFrequency);

        comboBoxBudgetFrequency.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if(wasFocused) {
                String comboVal = comboBoxBudgetFrequency.getEditor().textProperty().getValue();
                Boolean comboEquals = false;
                ArrayList<String> list = new ArrayList<>(comboBoxBudgetFrequency.getItems());
                for (String aList : list)
                    if (aList.equals(comboVal)) {
                        comboEquals = true;
                    }
                if (!comboEquals) {
                    comboBoxBudgetFrequency.getEditor().clear();
                }
            }
        });
    }

    public void setComponentValues(String budID, String budName, String budStart, String budEnd, String budFreq, String budAmount) {
        budgetID = budID;
        budgetType.setText(budName);
        comboBoxBudgetFrequency.setValue(budFreq);
        budAmount = new FormatDollarAmount().CleanDollarAmountsForSQL(budAmount);
        textFieldStartingBalance.setText(budAmount);

        if (!budStart.isEmpty()) {
            FormatDate formatDate = new FormatDate();
            Calendar calendarStartDate = formatDate.parseStringCalendar(formatDate.formatFromTableDate(budStart));

            int startYear = calendarStartDate.get(Calendar.YEAR);
            int startMonth = calendarStartDate.get(Calendar.MONTH) + 1;
            int startDay = calendarStartDate.get(Calendar.DAY_OF_MONTH);
            datePickerStartDate.setValue(LocalDate.of(startYear, startMonth, startDay));
        }

        if (!budEnd.isEmpty()) {
            FormatDate formatDate = new FormatDate();
            Calendar calendarStartDate = formatDate.parseStringCalendar(budEnd);

            int endYear = calendarStartDate.get(Calendar.YEAR);
            int endMonth = calendarStartDate.get(Calendar.MONTH) + 1;
            int endDay = calendarStartDate.get(Calendar.DAY_OF_MONTH);
            datePickerEndDate.setValue(LocalDate.of(endYear, endMonth, endDay));
        }

    }

    @FXML
    private void fillFrequency() throws SQLException {

        ObservableList<String> freqList = FXCollections.observableList(sqlMethod.listFrequency());
        comboBoxBudgetFrequency.getItems().clear();
        comboBoxBudgetFrequency.setItems(freqList);

    }

    @FXML
    protected void exitWindow() {

        Stage stage = (Stage) buttonExit.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void updateBudget() {

        String budID = budgetID;

        String budStart = "NULL";
        String budEnd = "NULL";
        String budAmount = "0.00";

        if (datePickerStartDate.getValue() != null) {
            budStart = datePickerStartDate.getValue().toString();
        }

        if (datePickerEndDate.getValue() != null) {
            budEnd = datePickerEndDate.getValue().toString();
        }

        if (!textFieldStartingBalance.getText().isEmpty()) {
            budAmount = textFieldStartingBalance.getText();
        }

        String budFreq = comboBoxBudgetFrequency.getSelectionModel().getSelectedItem();

        budSQL.updateBudget(budID, budStart, budEnd, budFreq, budAmount);

    }

    @FXML
    protected void submitBudgetEntry() throws SQLException {

        updateBudget();
        Stage stage = (Stage) buttonAddAccount.getScene().getWindow();
        stage.close();

    }

}
