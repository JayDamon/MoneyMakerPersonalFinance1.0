package com.moneymaker.modules.goalmanager;

import com.moneymaker.modules.accountmanager.SQLAccount;
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
 * Created by Jay Damon on 9/19/2016.
 */
public class UpdateGoalController implements Initializable {


    @FXML
    Button btnAddGoal, btnExit;

    @FXML
    ComboBox<Object> cmbType;
    @FXML
    ComboBox<Object> cmbAccount;
    @FXML
    ComboBox<Object> cmbPriority;

    @FXML
    DatePicker datePickerStartDate, datePickerEndDate;

    @FXML
    TextField txtName, txtAmount;

    private String goalID;

    private SQLGoal sqlGoal = new SQLGoal();
    private SQLAccount sqlAccount = new SQLAccount();

    private String whiteBackgroundStyle = "-fx-control-inner-background: white";
    private String redBackgroundStyle = "-fx-control-inner-background: red";

    public void initialize(URL url, ResourceBundle rs) {
        new AutoCompleteComboBoxListener(cmbType);
        new AutoCompleteComboBoxListener(cmbPriority);
        new AutoCompleteComboBoxListener(cmbAccount);

        txtAmount.focusedProperty().addListener((Observable, oldValue, newValue) -> {
            if (!newValue) {
                if(!txtAmount.getText().isEmpty()) {
                    txtAmount.setStyle(whiteBackgroundStyle);
                }
            }
        });

        txtName.focusedProperty().addListener((Observable, oldValue, newValue) -> {
            if (!newValue) {
                if(!txtName.getText().isEmpty()) {
                    txtName.setStyle(whiteBackgroundStyle);
                }
            }
        });

        datePickerStartDate.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (datePickerStartDate.getValue() != null) {
                    datePickerStartDate.setStyle(whiteBackgroundStyle);
                }
            }
        }));

        fillComboBoxes();
    }

    public void fillDatePickers(String endDate, String startDate) {
        if(endDate != null) {
            FormatDate formatDate = new FormatDate();
            Calendar calEndDate = formatDate.parseStringCalendar(formatDate.formatFromTableDate(endDate));
            int selectedEndDay = calEndDate.get(Calendar.DAY_OF_MONTH);
            int selectedEndMonth = calEndDate.get(Calendar.MONTH)+1;
            int selectedEndYear = calEndDate.get(Calendar.YEAR);

            datePickerEndDate.setValue(LocalDate.of(selectedEndYear, selectedEndMonth, selectedEndDay));
        }

        if(startDate != null) {

            FormatDate formatDate = new FormatDate();
            Calendar calStartDate = formatDate.parseStringCalendar(formatDate.formatFromTableDate(startDate));
            int selectedStartDay = calStartDate.get(Calendar.DAY_OF_MONTH);
            int selectedStartMonth = calStartDate.get(Calendar.MONTH)+1;
            int selectedStartYear = calStartDate.get(Calendar.YEAR);

            datePickerStartDate.setValue(LocalDate.of(selectedStartYear, selectedStartMonth, selectedStartDay));
        }
    }

    public void setComboBoxesAndTextFields(String goalName, String goalPriority, String goalType, String goalAccount, String goalAmount) {
        txtName.setText(goalName);
        cmbPriority.setValue(goalPriority);
        cmbType.setValue(goalType);
        cmbAccount.setValue(goalAccount);
        txtAmount.setText(goalAmount);
    }

    private void fillComboBoxes() {
        cmbPriority.getItems().addAll(sqlGoal.goalCount());
        cmbType.getItems().addAll(sqlGoal.listOfGoalTypes());
        cmbAccount.getItems().addAll(sqlAccount.listAccounts());
    }

    @FXML
    protected void buttonClickUpdateGoal() {

        Boolean areRequiredFieldsEmpty = false;
        String goalName = txtName.getText();
        String goalAmount = txtAmount.getText();
        int goalPriorityIndex = cmbPriority.getSelectionModel().getSelectedIndex();
        int goalTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
        int accountIndex = cmbAccount.getSelectionModel().getSelectedIndex();

        if (goalName.isEmpty()) {
            txtName.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            txtName.setStyle(whiteBackgroundStyle);
        }

        if (goalAmount.isEmpty()) {
            txtAmount.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            txtAmount.setStyle(whiteBackgroundStyle);
        }

        if (datePickerStartDate.getValue() == null) {
            datePickerStartDate.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            datePickerStartDate.setStyle(whiteBackgroundStyle);
        }

        if (goalPriorityIndex == -1) {
            cmbPriority.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            cmbPriority.setStyle(whiteBackgroundStyle);
        }

        if (accountIndex == -1) {
            cmbAccount.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            cmbAccount.setStyle(whiteBackgroundStyle);
        }

        if (goalTypeIndex == -1) {
            cmbType.setStyle(redBackgroundStyle);
            areRequiredFieldsEmpty = true;
        } else {
            cmbType.setStyle(whiteBackgroundStyle);
        }

        if (!areRequiredFieldsEmpty) {

            String goalID = this.goalID;
            String goalPriority = cmbPriority.getSelectionModel().getSelectedItem().toString();
            String goalType = cmbType.getSelectionModel().getSelectedItem().toString();
            String goalAccount = cmbAccount.getSelectionModel().getSelectedItem().toString();
            String goalStartDate = datePickerStartDate.getValue().toString();

            String goalEndDate = "NULL";

            if (datePickerEndDate.getValue() != null) {
                goalEndDate = datePickerEndDate.getValue().toString();
            }

            sqlGoal.updateGoals(goalID, goalName, goalPriority, goalType, goalAccount, goalStartDate, goalEndDate, goalAmount);
            exitWindow();
        }
    }

    @FXML
    protected void buttonClickExit() {
        exitWindow();
    }

    private void exitWindow() {
        Stage stage = (Stage)btnExit.getScene().getWindow();
        stage.close();
    }

    public void setGoalID(String goalID) {
        this.goalID = goalID;
    }

}
