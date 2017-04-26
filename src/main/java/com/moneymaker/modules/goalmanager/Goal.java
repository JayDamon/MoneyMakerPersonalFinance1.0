package com.moneymaker.modules.goalmanager;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Jay Damon on 9/19/2016.
 */
public class Goal {

    private final SimpleStringProperty goalID = new SimpleStringProperty("");
    private final SimpleStringProperty goalName = new SimpleStringProperty("");
    private final SimpleStringProperty goalPriority = new SimpleStringProperty("");
    private final SimpleStringProperty goalType = new SimpleStringProperty("");
    private final SimpleStringProperty goalAccount = new SimpleStringProperty("");
    private final SimpleStringProperty goalStartDate = new SimpleStringProperty("");
    private final SimpleStringProperty goalEndDate = new SimpleStringProperty("");
    private final SimpleStringProperty goalAmount = new SimpleStringProperty("");
    private final SimpleStringProperty goalActualAmount = new SimpleStringProperty("");

    public Goal() {
        this("","","","","","","","","");
    }

    public Goal(String goalID, String goalName, String goalPriority, String goalType, String goalAccount, String goalStartDate, String goalEndDate, String goalAmount, String goalActualAmount) {
        setGoalID(goalID);
        setGoalName(goalName);
        setGoalPriority(goalPriority);
        setGoalType(goalType);
        setGoalAccount(goalAccount);
        setGoalStartDate(goalStartDate);
        setGoalEndDate(goalEndDate);
        setGoalAmount(goalAmount);
        setGoalActualAmount(goalActualAmount);
    }

    public String getGoalID() {
        return goalID.get();
    }

    public void setGoalID(String goalID) {
        this.goalID.set(goalID);
    }

    public String getGoalName() {
        return goalName.get();
    }

    public void setGoalName(String goalName) {
        this.goalName.set(goalName);
    }

    public String getGoalPriority() {
        return goalPriority.get();
    }

    public void setGoalPriority(String goalPriority) {
        this.goalPriority.set(goalPriority);
    }

    public String getGoalType() {
        return goalType.get();
    }

    public void setGoalType(String goalType) {
        this.goalType.set(goalType);
    }

    public String getGoalAccount() {
        return goalAccount.get();
    }

    public void setGoalAccount(String goalAccount) {
        this.goalAccount.set(goalAccount);
    }

    public String getGoalStartDate() {
        return goalStartDate.get();
    }

    public void setGoalStartDate(String goalStartDate) {
        this.goalStartDate.set(goalStartDate);
    }

    public String getGoalEndDate() {
        return goalEndDate.get();
    }

    public void setGoalEndDate(String goalEndDate) {
        this.goalEndDate.set(goalEndDate);
    }

    public String getGoalAmount() {
        return goalAmount.get();
    }

    public void setGoalAmount(String goalAmount) {
        this.goalAmount.set(goalAmount);
    }

    public String getGoalActualAmount() {
        return goalActualAmount.get();
    }

    public void setGoalActualAmount(String goalActualAmount) {
        this.goalActualAmount.set(goalActualAmount);
    }
}
