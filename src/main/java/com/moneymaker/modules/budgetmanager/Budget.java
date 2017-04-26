package com.moneymaker.modules.budgetmanager;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created for MoneyMaker by Jay Damon on 6/25/2016.
 */
public class Budget {

    private final SimpleStringProperty budgetID = new SimpleStringProperty("");
    private final SimpleStringProperty budgetName = new SimpleStringProperty("");
    private final SimpleStringProperty budgetStartDate = new SimpleStringProperty("");
    private final SimpleStringProperty budgetEndDate = new SimpleStringProperty("");
    private final SimpleStringProperty budgetFrequency = new SimpleStringProperty("");
    private final SimpleStringProperty budgetAmount = new SimpleStringProperty("");
    private final SimpleStringProperty uncategorizedTransactions = new SimpleStringProperty("");

    public final String TRANSFER = "Transfer/Payment";

    public Budget() {
        this("","","","","","","");
    }

    public Budget(String budgetID, String budgetName, String budgetStartDate, String budgetEndDate,
                  String budgetFrequency, String budgetAmount, String uncategorizedTransactions) {
        setBudgetID(budgetID);
        setBudgetName(budgetName);
        setBudgetStartDate(budgetStartDate);
        setBudgetEndDate(budgetEndDate);
        setBudgetFrequency(budgetFrequency);
        setBudgetAmount(budgetAmount);
        setUncategorizedTransactions(uncategorizedTransactions);
    }

    public String getBudgetID() {
        return budgetID.get();
    }

    public void setBudgetID(String bID) {
        this.budgetID.set(bID);
    }

    public String getBudgetName() {
        return budgetName.get();
    }

    public void setBudgetName(String bName) {
        this.budgetName.set(bName);
    }

    public String getBudgetStartDate() {
        return budgetStartDate.get();
    }

    public void setBudgetStartDate(String bStartDate) {
        this.budgetStartDate.set(bStartDate);
    }

    public String getBudgetEndDate() {
        return budgetEndDate.get();
    }

    public void setBudgetEndDate(String bEndDate) {
        this.budgetEndDate.set(bEndDate);
    }

    public String getBudgetFrequency() {
        return budgetFrequency.get();
    }

    public void setBudgetFrequency(String bFreq) {
        this.budgetFrequency.set(bFreq);
    }

    public String getBudgetAmount() {
        return budgetAmount.get();
    }

    public void setBudgetAmount(String bAm) {
        this.budgetAmount.set(bAm);
    }

    public String getUncategorizedTransactions() {
        return uncategorizedTransactions.get();
    }

    public void setUncategorizedTransactions(String uT) {
        this.uncategorizedTransactions.set(uT);
    }
}
