package com.moneymaker.modules.accountmanager;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created for Money Maker by Jay Damon on 2/23/2016.
 */
public class Account {

    private final SimpleStringProperty accountID = new SimpleStringProperty("");
    private final SimpleStringProperty accountName = new SimpleStringProperty("");
    private final SimpleStringProperty accountType = new SimpleStringProperty("");
    private final SimpleStringProperty accountStartingBalance = new SimpleStringProperty("");
    private final SimpleStringProperty accountCurrentBalance = new SimpleStringProperty("");
    private final SimpleStringProperty isPrimaryAccount = new SimpleStringProperty("");
    private final SimpleStringProperty inCashFlow = new SimpleStringProperty("");

    public Account() {
        this("","","","","","","");
    }

    public Account(String accountID, String accountName, String accountType, String accountStartingBalance, String accountCurrentBalance,
                   String isPrimaryAccount, String inCashFlow) {
        setAccountID(accountID);
        setAccountName(accountName);
        setAccountType(accountType);
        setAccountStartingBalance(accountStartingBalance);
        setAccountCurrentBalance(accountCurrentBalance);
        setIsPrimaryAccount(isPrimaryAccount);
        setInCashFlow(inCashFlow);
    }

    public String getAccountID() {
        return accountID.get();
    }

    public void setAccountID(String aID) {
        this.accountID.set(aID);
    }

    public String getAccountName() {
        return accountName.get();
    }

    public void setAccountName(String aName) {
        this.accountName.set(aName);
    }

    public String getAccountType() {
        return accountType.get();
    }

    public void setAccountType(String aType) {
        this.accountType.set(aType);
    }

    public String getAccountStartingBalance() {
        return accountStartingBalance.get();
    }

    public void setAccountStartingBalance(String aStartBalance) {
        this.accountStartingBalance.set(aStartBalance);
    }

    public String getAccountCurrentBalance() {
        return accountCurrentBalance.get();
    }

    public void setAccountCurrentBalance(String accountCurrentBalance) {
        this.accountCurrentBalance.set(accountCurrentBalance);
    }

    public String getIsPrimaryAccount() {
        return isPrimaryAccount.get();
    }

    public void setIsPrimaryAccount(String isPrimaryAccount) {
        this.isPrimaryAccount.set(isPrimaryAccount);
    }

    public String getInCashFlow() {
        return inCashFlow.get();
    }

    public void setInCashFlow(String inCashFlow) {
        this.inCashFlow.set(inCashFlow);
    }
}
