package com.moneymaker.modules.cashflowmanager;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Jay Damon on 9/20/2016.
 */
public class CashFlow {
    private final SimpleStringProperty cashFlowAccount = new SimpleStringProperty("");
    private final SimpleStringProperty cashFlowBudget = new SimpleStringProperty("");
    private final SimpleStringProperty cashFlowDate = new SimpleStringProperty("");
    private final SimpleStringProperty cashFlowCategory = new SimpleStringProperty("");
    private final SimpleStringProperty cashFlowProjected = new SimpleStringProperty("");
    private final SimpleStringProperty cashFlowActual = new SimpleStringProperty("");
    private final SimpleStringProperty cashOnHandStartingProjection = new SimpleStringProperty("");
    private final SimpleStringProperty cashOnHandActual = new SimpleStringProperty("");
    private final SimpleStringProperty cashOnHandCurrentProjection = new SimpleStringProperty("");

    public CashFlow() { this("","","","","","","","",""); }

    public CashFlow(String cashFlowAccount, String cashFlowBudget, String cashFlowDate, String cashFlowCategory,
                    String cashFlowProjected, String cashFlowActual, String cashOnHandStartingProjection,
                    String cashOnHandActual, String cashOnHandCurrentProjection) {
        setCashFlowAccount(cashFlowAccount);
        setCashFlowBudget(cashFlowBudget);
        setCashFlowDate(cashFlowDate);
        setCashFlowCategory(cashFlowCategory);
        setCashFlowProjected(cashFlowProjected);
        setCashFlowActual(cashFlowActual);
        setCashOnHandStartingProjection(cashOnHandStartingProjection);
        setCashOnHandActual(cashOnHandActual);
        setCashOnHandCurrentProjection(cashOnHandCurrentProjection);
    }

    public String getCashFlowAccount() {
        return cashFlowAccount.get();
    }

    public SimpleStringProperty cashFlowAccountProperty() {
        return cashFlowAccount;
    }

    public void setCashFlowAccount(String cashFlowAccount) {
        this.cashFlowAccount.set(cashFlowAccount);
    }

    public String getCashFlowBudget() {
        return cashFlowBudget.get();
    }

    public SimpleStringProperty cashFlowBudgetProperty() {
        return cashFlowBudget;
    }

    public void setCashFlowBudget(String cashFlowBudget) {
        this.cashFlowBudget.set(cashFlowBudget);
    }

    public String getCashFlowDate() {
        return cashFlowDate.get();
    }

    public SimpleStringProperty cashFlowDateProperty() {
        return cashFlowDate;
    }

    public void setCashFlowDate(String cashFlowDate) {
        this.cashFlowDate.set(cashFlowDate);
    }

    public String getCashFlowCategory() {
        return cashFlowCategory.get();
    }

    public SimpleStringProperty cashFlowCategoryProperty() {
        return cashFlowCategory;
    }

    public void setCashFlowCategory(String cashFlowCategory) {
        this.cashFlowCategory.set(cashFlowCategory);
    }

    public String getCashFlowProjected() {
        return cashFlowProjected.get();
    }

    public SimpleStringProperty cashFlowProjectedProperty() {
        return cashFlowProjected;
    }

    public void setCashFlowProjected(String cashFlowProjected) {
        this.cashFlowProjected.set(cashFlowProjected);
    }

    public String getCashFlowActual() {
        return cashFlowActual.get();
    }

    public SimpleStringProperty cashFlowActualProperty() {
        return cashFlowActual;
    }

    public void setCashFlowActual(String cashFlowActual) {
        this.cashFlowActual.set(cashFlowActual);
    }

    public String getCashOnHandStartingProjection() {
        return cashOnHandStartingProjection.get();
    }

    public SimpleStringProperty cashOnHandStartingProjectionProperty() {
        return cashOnHandStartingProjection;
    }

    public void setCashOnHandStartingProjection(String cashOnHandStartingProjection) {
        this.cashOnHandStartingProjection.set(cashOnHandStartingProjection);
    }

    public String getCashOnHandActual() {
        return cashOnHandActual.get();
    }

    public SimpleStringProperty cashOnHandActualProperty() {
        return cashOnHandActual;
    }

    public void setCashOnHandActual(String cashOnHandActual) {
        this.cashOnHandActual.set(cashOnHandActual);
    }

    public String getCashOnHandCurrentProjection() {
        return cashOnHandCurrentProjection.get();
    }

    public SimpleStringProperty cashOnHandCurrentProjectionProperty() {
        return cashOnHandCurrentProjection;
    }

    public void setCashOnHandCurrentProjection(String cashOnHandCurrentProjection) {
        this.cashOnHandCurrentProjection.set(cashOnHandCurrentProjection);
    }
}
