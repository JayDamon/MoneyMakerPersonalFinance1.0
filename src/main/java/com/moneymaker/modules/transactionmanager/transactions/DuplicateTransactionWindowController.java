package com.moneymaker.modules.transactionmanager.transactions;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

/**
 * Created by Jay Damon on 8/28/2016.
 */
public class DuplicateTransactionWindowController {

    @FXML public VBox vboxDuplicateTransaction;

    public String test;

    private String transactionAccount = "";
    private String transactionBudget = "";
    private String transactionCategory = "";
    private String transactionDate = "";
    private String transactionDesc = "";
    private String transactionAmnt = "";

    @FXML
    public void listDuplicateTransactions(String tranAccount, String tranBudget, String tranCat, String tranDate, String tranDesc,
                                          String tranAmnt, ObservableList<Transaction> transactions) {
        transactionAccount = tranAccount;
        transactionBudget = tranBudget;
        transactionCategory = tranCat;
        transactionDate = tranDate;
        transactionDesc = tranDesc;
        transactionAmnt = tranAmnt;

        Double labelWidth = 127.5;
        Double hboxWidth = 510d;
        Double compHeight = 57d;
//        Double vBoxHeight = 0d;

        for (Transaction tran : transactions) {
            HBox newHBox = new HBox();
            newHBox.setStyle("-fx-border-color: TRANSPARENT;");
            Label newLabel1 = new Label();
            Label newLabel2 = new Label();
            Label newLabel3 = new Label();
            Label newLabel4 = new Label();

            newHBox.setPrefSize(hboxWidth, compHeight);
            newLabel1.setPrefSize(labelWidth, compHeight);
            newLabel2.setPrefSize(labelWidth, compHeight);
            newLabel3.setPrefSize(labelWidth, compHeight);
            newLabel4.setPrefSize(labelWidth, compHeight);

            newLabel1.setText(tran.getTransactionDate());
            newLabel2.setText(tran.getTransactionDescription());
            newLabel3.setText(tran.getTransactionAmount());
            newLabel4.setText(tran.getTransactionTimeStamp());

            vboxDuplicateTransaction.getChildren().add(newHBox);
            newHBox.getChildren().addAll(newLabel1, newLabel2, newLabel3, newLabel4);

            newHBox.addEventHandler(
                    MouseEvent.MOUSE_ENTERED,
                    event -> {
                        newHBox.setStyle("-fx-border-color: #3399ff;");
                    });
            newHBox.addEventHandler(
                    MouseEvent.MOUSE_EXITED,
                    event -> {
                        newHBox.setStyle("-fx-border-color: TRANSPARENT;");
                    });
        }
    }

    @FXML
    protected void btnClickYes(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    protected void btnClickNo(ActionEvent event) throws SQLException {
        SQLTransaction sqlTransaction = new SQLTransaction();
        sqlTransaction.newTransaction(transactionAccount,transactionBudget,transactionCategory,transactionDate,transactionDesc,transactionAmnt);
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
