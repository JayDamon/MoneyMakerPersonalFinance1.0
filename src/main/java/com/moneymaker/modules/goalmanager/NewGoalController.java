package com.moneymaker.modules.goalmanager;

import com.moneymaker.modules.accountmanager.SQLAccount;
import com.moneymaker.utilities.AutoCompleteComboBoxListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jay Damon on 9/19/2016.
 */
public class NewGoalController implements Initializable {

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

    private SQLGoal sqlGoal = new SQLGoal();
    private SQLAccount sqlAccount = new SQLAccount();

    String whiteBackgroundStyle = "-fx-control-inner-background: white";
    String redBackgroundStyle = "-fx-control-inner-background: red";

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

    private void fillComboBoxes() {
        int size = sqlGoal.goalCount().size();
        cmbPriority.getItems().addAll(sqlGoal.goalCount());
        cmbPriority.getItems().add(size + 1);
        cmbType.getItems().addAll(sqlGoal.listOfGoalTypes());
        cmbAccount.getItems().addAll(sqlAccount.listAccounts());
    }

    @FXML
    protected void buttonClickAddGoal() {

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

            String goalPriority = cmbPriority.getSelectionModel().getSelectedItem().toString();
            String goalType = cmbType.getSelectionModel().getSelectedItem().toString();
            String goalAccount = cmbAccount.getSelectionModel().getSelectedItem().toString();
            String goalStartDate = datePickerStartDate.getValue().toString();

            String goalEndDate = "NULL";

            if (datePickerEndDate.getValue() != null) {
                goalEndDate = datePickerEndDate.getValue().toString();
            }

            sqlGoal.addGoal(goalName, goalPriority, goalType, goalAccount, goalStartDate, goalEndDate, goalAmount);
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

}
