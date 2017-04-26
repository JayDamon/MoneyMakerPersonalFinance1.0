package com.moneymaker.modules.transfermanager;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created for MoneyMaker by Jay Damon on 10/20/2016.
 */
public class Transfer {
    private final SimpleStringProperty transferID = new SimpleStringProperty("");
    private final SimpleStringProperty transferDate = new SimpleStringProperty("");
    private final SimpleStringProperty transferType = new SimpleStringProperty("");
    private final SimpleStringProperty transferFromAccount = new SimpleStringProperty("");
    private final SimpleStringProperty transferToAccount = new SimpleStringProperty("");
    private final SimpleStringProperty transferAmount = new SimpleStringProperty("");
    private final SimpleStringProperty transferFromTransactionID = new SimpleStringProperty("");
    private final SimpleStringProperty transferToTransactionID = new SimpleStringProperty("");

    public Transfer() { this("","","","","","","",""); }

    public Transfer(String transferID, String transferDate, String transferType, String transferFromAccount,
                    String transferToAccount, String transferAmount, String transferFromTransactionID,
                    String transferToTransactionID) {
        setTransferID(transferID);
        setTransferDate(transferDate);
        setTransferType(transferType);
        setTransferFromAccount(transferFromAccount);
        setTransferToAccount(transferToAccount);
        setTransferAmount(transferAmount);
        setTransferFromTransactionID(transferFromTransactionID);
        setTransferToTransactionID(transferToTransactionID);
    }

    public String getTransferID() {
        return transferID.get();
    }

    public void setTransferID(String transferID) {
        this.transferID.set(transferID);
    }

    public String getTransferDate() {
        return transferDate.get();
    }

    public void setTransferDate(String transferDate) {
        this.transferDate.set(transferDate);
    }


    public String getTransferType() {
        return transferType.get();
    }

    public void setTransferType(String transferType) {
        this.transferType.set(transferType);
    }

    public String getTransferFromAccount() {
        return transferFromAccount.get();
    }

    public void setTransferFromAccount(String transferFromAccount) {
        this.transferFromAccount.set(transferFromAccount);
    }

    public String getTransferToAccount() {
        return transferToAccount.get();
    }

    public void setTransferToAccount(String transferToAccount) {
        this.transferToAccount.set(transferToAccount);
    }

    public String getTransferAmount() {
        return transferAmount.get();
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount.set(transferAmount);
    }

    public String getTransferFromTransactionID() {
        return transferFromTransactionID.get();
    }

    public void setTransferFromTransactionID(String transferFromTransactionID) {
        this.transferFromTransactionID.set(transferFromTransactionID);
    }

    public String getTransferToTransactionID() {
        return transferToTransactionID.get();
    }

    public void setTransferToTransactionID(String transferToTransactionID) {
        this.transferToTransactionID.set(transferToTransactionID);
    }
}
