package com.moneymaker.modules.transactionmanager.recurringtransactions;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Jay Damon on 9/13/2016.
 */
public class RecurringTransaction {

    private final SimpleStringProperty recurringTransactionID = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionName = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionAccount = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionBudget = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionFrequency = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionOccurrence = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionType = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionStartDate = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionEndDate = new SimpleStringProperty("");
    private final SimpleStringProperty recurringTransactionAmount = new SimpleStringProperty("");

    public RecurringTransaction() { this("","","","","","","","","",""); }

    public RecurringTransaction(String recurringTransactionID, String recurringTransactionName,
                                String recurringTransactionAccount, String recurringTransactionBudget,
                                String recurringTransactionFrequency, String recurringTransactionOccurrence,
                                String recurringTransactionType, String recurringTransactionStartDate,
                                String recurringTransactionEndDate, String recurringTransactionAmount) {
        setRecurringTransactionID(recurringTransactionID);
        setRecurringTransactionName(recurringTransactionName);
        setRecurringTransactionAccount(recurringTransactionAccount);
        setRecurringTransactionBudget(recurringTransactionBudget);
        setRecurringTransactionFrequency(recurringTransactionFrequency);
        setRecurringTransactionOccurrence(recurringTransactionOccurrence);
        setRecurringTransactionType(recurringTransactionType);
        setRecurringTransactionStartDate(recurringTransactionStartDate);
        setRecurringTransactionEndDate(recurringTransactionEndDate);
        setRecurringTransactionAmount(recurringTransactionAmount);
    }

    public String getRecurringTransactionID() {
        return recurringTransactionID.get();
    }

    public void setRecurringTransactionID(String recurringTransactionID) {
        this.recurringTransactionID.set(recurringTransactionID);
    }

    public String getRecurringTransactionName() {
        return recurringTransactionName.get();
    }

    public void setRecurringTransactionName(String recurringTransactionName) {
        this.recurringTransactionName.set(recurringTransactionName);
    }

    public String getRecurringTransactionAccount() {
        return recurringTransactionAccount.get();
    }

    public void setRecurringTransactionAccount(String recurringTransactionAccount) {
        this.recurringTransactionAccount.set(recurringTransactionAccount);
    }

    public String getRecurringTransactionBudget() {
        return recurringTransactionBudget.get();
    }

    public void setRecurringTransactionBudget(String recurringTransactionBudget) {
        this.recurringTransactionBudget.set(recurringTransactionBudget);
    }

    public String getRecurringTransactionFrequency() {
        return recurringTransactionFrequency.get();
    }

    public void setRecurringTransactionFrequency(String recurringTransactionFrequency) {
        this.recurringTransactionFrequency.set(recurringTransactionFrequency);
    }

    public String getRecurringTransactionOccurrence() {
        return recurringTransactionOccurrence.get();
    }

    public void setRecurringTransactionOccurrence(String recurringTransactionOccurrence) {
        this.recurringTransactionOccurrence.set(recurringTransactionOccurrence);
    }

    public String getRecurringTransactionType() {
        return recurringTransactionType.get();
    }

    public void setRecurringTransactionType(String recurringTransactionType) {
        this.recurringTransactionType.set(recurringTransactionType);
    }

    public String getRecurringTransactionStartDate() {
        return recurringTransactionStartDate.get();
    }

    public void setRecurringTransactionStartDate(String recurringTransactionStartDate) {
        this.recurringTransactionStartDate.set(recurringTransactionStartDate);
    }

    public String getRecurringTransactionEndDate() {
        return recurringTransactionEndDate.get();
    }

    public void setRecurringTransactionEndDate(String recurringTransactionEndDate) {
        this.recurringTransactionEndDate.set(recurringTransactionEndDate);
    }

    public String getRecurringTransactionAmount() {
        return recurringTransactionAmount.get();
    }

    public void setRecurringTransactionAmount(String recurringTransactionAmount) {
        this.recurringTransactionAmount.set(recurringTransactionAmount);
    }
}
