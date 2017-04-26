package com.moneymaker.modules.transactionmanager.transactions;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Jay Damon on 9/5/2016.
 */
public class TransactionCategory {

    private final SimpleStringProperty transactionCategory = new SimpleStringProperty("");

    private TransactionCategory() {this(""); }

    public TransactionCategory(String transactionCategory) {
        setTransactionCategory(transactionCategory);
    }

    public String getTransactionCategory() { return transactionCategory.get(); }

    public void setTransactionCategory(String tCat) { this.transactionCategory.set(tCat); }
}
