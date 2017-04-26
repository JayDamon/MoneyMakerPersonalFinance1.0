package com.moneymaker.modules.transactionmanager.transactions;

import javafx.beans.property.SimpleStringProperty;

public class Transaction {

    private final SimpleStringProperty transactionID = new SimpleStringProperty("");
    private final SimpleStringProperty transactionAccount = new SimpleStringProperty("");
    private final SimpleStringProperty transactionBudget = new SimpleStringProperty("");
    private final SimpleStringProperty transactionCategory = new SimpleStringProperty("");
    private final SimpleStringProperty transactionRecurring = new SimpleStringProperty("");
    private final SimpleStringProperty transactionDate = new SimpleStringProperty("");
    private final SimpleStringProperty transactionDescription = new SimpleStringProperty("");
    private final SimpleStringProperty transactionAmount = new SimpleStringProperty("");
    private final SimpleStringProperty transactionTimeStamp = new SimpleStringProperty("");

    public final String EXPENSE = "Expense";
    public final String INCOME = "Income";
    public final String TRANSFER = "Transfer";

    public Transaction() {
        this("", "", "", "", "", "", "", "", "");
    }

    public Transaction(String transactionID, String transactionAccount, String transactionBudget, String transactionCategory,
                       String transactionRecurring, String transactionDate, String transactionDescription,
                       String transactionAmount, String transactionTimeStamp) {
        setTransactionID(transactionID);
        setTransactionAccount(transactionAccount);
        setTransactionBudget(transactionBudget);
        setTransactionCategory(transactionCategory);
        setTransactionRecurring(transactionRecurring);
        setTransactionDate(transactionDate);
        setTransactionDescription(transactionDescription);
        setTransactionAmount(transactionAmount);
        setTransactionTimeStamp(transactionTimeStamp);
    }

    public String getTransactionID() {
        return transactionID.get();
    }

    public void setTransactionID(String tID) {
        this.transactionID.set(tID);
    }

    public String getTransactionAccount() {
        return transactionAccount.get();
    }

    public void setTransactionAccount(String tAcc) {
        this.transactionAccount.set(tAcc);
    }

    public String getTransactionBudget() {
        return transactionBudget.get();
    }

    public void setTransactionBudget(String tranBud) {
        this.transactionBudget.set(tranBud);
    }

    public String getTransactionCategory() {
        return transactionCategory.get();
    }

    public void setTransactionCategory(String tranCat) {
        this.transactionCategory.set(tranCat);
    }

    public String getTransactionRecurring() {
        return transactionRecurring.get();
    }

    public void setTransactionRecurring(String transactionRecurring) {
        this.transactionRecurring.set(transactionRecurring);
    }

    public String getTransactionDate() {
        return transactionDate.get();
    }

    public void setTransactionDate(String tDate) {
        this.transactionDate.set(tDate);
    }

    public String getTransactionDescription() {
        return transactionDescription.get();
    }

    public void setTransactionDescription(String tDesc) {
        this.transactionDescription.set(tDesc);
    }

    public String getTransactionAmount() {
        return transactionAmount.get();
    }

    public void setTransactionAmount(String tAmnt) {
        this.transactionAmount.set(tAmnt);
    }

    public String getTransactionTimeStamp() {
        return transactionTimeStamp.get();
    }

    public void setTransactionTimeStamp(String tTimeStmp) {
        this.transactionTimeStamp.set(tTimeStmp);
    }
}





















