package com.moneymaker.modules.transactionmanager.transactions;

import com.moneymaker.modules.budgetmanager.SQLBudget;
import com.moneymaker.utilities.FormatDate;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ImportBudgetTransactionsController implements Initializable {

    @FXML TableColumn colIncDate, colIncDesc, colIncAmount;

    @FXML ComboBox<String> cmbDate, cmbDesc, cmbDebit, cmbCred, cmbAcc;

    @FXML TableView<Transaction> tblSpending, tblIncome;

    @FXML AnchorPane anchSpending, anchIncome;

    @FXML Button btnExit;

    @FXML VBox vBoxCategory, vboxBudgets;

    @FXML ToggleButton togSpending, togIncome;

    @FXML Label lblTransactionType;

    public File filePath;

    public ObservableList<String> tranHead;
    
    private final SQLBudget sqlBudget = new SQLBudget();
    private final SQLTransaction sqlTransaction = new SQLTransaction();
    private final FormatDate formatDate = new FormatDate();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        togSpending.setSelected(true);
        lblTransactionType.setText("Spending");

        addBudgets();

        tblSpending.setOnMouseClicked(event -> tblSpending.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE));

        tblIncome.setOnMouseClicked(event -> tblIncome.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE));

    }

    public void setComboBoxes(String tranDate, String tranDescription, String tranDebit, String tranCredit, String tranAccount) {
        cmbDate.setValue(tranDate);
        cmbDesc.setValue(tranDescription);
        cmbDebit.setValue(tranDebit);
        cmbCred.setValue(tranCredit);
        cmbAcc.setValue(tranAccount);
    }

    public void setComboBoxList(ObservableList<String> list) {
        cmbDate.getItems().addAll(list);
        cmbDesc.getItems().addAll(list);
        cmbDebit.getItems().addAll(list);
        cmbCred.getItems().addAll(list);
        cmbAcc.getItems().addAll(list);
    }

    public void importCSV(String filePath) throws Exception {
        ObservableList<Transaction> data = tblSpending.getItems();
        ObservableList<Transaction> data2 = tblIncome.getItems();
        data.clear();
        data2.clear();

        String tranDateString, tranDescriptionString, tranAmountString;

        String transactionDate = cmbDate.getSelectionModel().getSelectedItem();
        String transactionDescription = cmbDesc.getSelectionModel().getSelectedItem();
        String transactionDebit = cmbDebit.getSelectionModel().getSelectedItem();
        String transactionCredit = cmbCred.getSelectionModel().getSelectedItem();

        String line;

        int tranDate = 0;
        int tranDesc = 0;
        int tranDebit = 0;
        int tranCredit = 0;

        Scanner scanner = new Scanner(new File(filePath));
        scanner.useDelimiter(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        Path pathToFile = Paths.get(filePath);
        BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII);
        List<String> headers;
        int i = 0;

        while ((line = br.readLine()) != null) {
            //Get the first row of the csv and add the headers to the ListView
            if (i == 0) {
                String[] headerStringArray = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                headers = Arrays.asList(headerStringArray);

                //Loop through and trim string to remove leading and trailing spaces
                for(final ListIterator<String> listIterator = headers.listIterator(); listIterator.hasNext();) {
                    final String updateString = listIterator.next();
                    listIterator.set(updateString.trim());
                }

                int csvNum = 0;
                for (String s: headers) {
                    if (s.equals(transactionDate)) {
                        tranDate = csvNum;
                    } else if (s.equals(transactionDescription)) {
                        tranDesc = csvNum;
                    } else if (s.equals(transactionDebit)) {
                        tranDebit = csvNum;
                    } else if (s.equals(transactionCredit)) {
                        tranCredit = csvNum;
                    }
                    csvNum++;
                }
            }

            if (i != 0) {
                String[] cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (!cols[tranDebit].isEmpty()) {
                    tranDateString = cols[tranDate];
                    tranDescriptionString = cols[tranDesc].replace("\"","");
                    tranAmountString = "-" + cols[tranDebit];
                    data.add(new Transaction("ID","Account","Budget","Category", "Recurring", tranDateString, tranDescriptionString, tranAmountString,"timestamp"));

                } else if (cols[tranDebit].isEmpty()) {
                    tranDateString = cols[tranDate];
                    tranDescriptionString = cols[tranDesc].replace("\"","");
//                    tranDescriptionString = cols[tranDesc];
                    tranAmountString = cols[tranCredit];
                    data2.add(new Transaction("ID","Account","Budget","Category", "Recurring", tranDateString, tranDescriptionString, tranAmountString,"timestamp"));
                }
            }
            i++;
        }
    }

    @FXML
    protected void backBtn(ActionEvent event) throws IOException {

        String tranDate = cmbDate.getSelectionModel().getSelectedItem();
        String tranDesc = cmbDesc.getSelectionModel().getSelectedItem();
        String tranDebit = cmbDebit.getSelectionModel().getSelectedItem();
        String tranCredit = cmbCred.getSelectionModel().getSelectedItem();
        String tranAcc = cmbAcc.getSelectionModel().getSelectedItem();

        ((Node) (event.getSource())).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/popup/importTransactions.fxml"));
        AnchorPane newWindow = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Expenses");
        Scene scene = new Scene(newWindow);
        stage.setScene(scene);
        stage.show();
        ImportTransactionsWindowController impWind = loader.getController();
        impWind.fileText(filePath);
        impWind.setTextBoxes(tranDate, tranDesc, tranDebit, tranCredit, tranAcc);
        impWind.fillListBox(this.tranHead);
    }

    @FXML
    protected void exitWindow() {
        Stage stage = (Stage)btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void togIncome() {
        anchIncome.setVisible(true);
        anchSpending.setVisible(false);
        lblTransactionType.setText("Income");
    }

    @FXML
    private void togSpending() {
        anchIncome.setVisible(false);
        anchSpending.setVisible(true);
        lblTransactionType.setText("Spending");
    }

    @FXML
    private void submitChanges() {

        boolean missingIncome = false;
        boolean missingSpending = false;
        boolean addMissingTransaction = false;

        if (tblIncome.getItems().size() > 0) {
            missingIncome = true;
        }
        if (tblSpending.getItems().size() > 0) {
            missingSpending = true;
        }

        if (missingIncome && missingSpending) {
            addMissingTransaction = addMissingTransactions("Income and Spending");
        } else if(missingIncome) {
            addMissingTransaction = addMissingTransactions("Income");
        } else if(missingSpending) {
            addMissingTransaction = addMissingTransactions("Spending");
        }

        if (!addMissingTransaction) {
            closeWindow();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void addBudgets() {

        //Get row data from spending table for dragging
        tblSpending.setOnDragDetected(event -> {
            Dragboard db = tblSpending.startDragAndDrop(TransferMode.COPY);

            int index = tblSpending.getSelectionModel().getSelectedIndex();
            Transaction selTran = tblSpending.getItems().get(index);

            ClipboardContent content = new ClipboardContent();
            content.putString(selTran.getTransactionDescription());
            db.setContent(content);
            event.consume();
        });

        //Get row data from income table for dragging
        tblIncome.setOnDragDetected(event -> {
            Dragboard db = tblIncome.startDragAndDrop(TransferMode.COPY);

            int index = tblIncome.getSelectionModel().getSelectedIndex();
            Transaction selTran = tblIncome.getItems().get(index);

            ClipboardContent content = new ClipboardContent();
            content.putString(selTran.getTransactionDescription());
            db.setContent(content);
            event.consume();
        });
        
        List<String> budgets = sqlBudget.listActiveBudget();

        for (String e : budgets) {
            Label budgetLabel = new Label(e);
            budgetLabel.setText(e);
            budgetLabel.setStyle("-fx-border-color: BLACK;");
            budgetLabel.setPrefSize(243, 71);
            budgetLabel.setMinHeight(30);
            vboxBudgets.getChildren().add(budgetLabel);

            budgetLabel.setOnMouseClicked(event -> {
                for (Node child : vboxBudgets.getChildren()) {
                    child.setStyle("-fx-border-color: BLACK;");
                }
                addCategoryLabels(budgetLabel, e);
            });

            budgetLabel.setOnDragDetected(event -> {
                Dragboard db = budgetLabel.startDragAndDrop(TransferMode.COPY);

                ClipboardContent content = new ClipboardContent();
                content.putString(budgetLabel.getText());
                db.setContent(content);
                event.consume();
            });

            budgetLabel.addEventHandler(
                    DragEvent.DRAG_OVER,
                    event -> {
                        event.acceptTransferModes(TransferMode.COPY);
                        addCategoryLabels(budgetLabel, e);
                        event.consume();
            });

            budgetLabel.addEventHandler(
                    DragEvent.DRAG_EXITED,
                    event -> {
                        event.acceptTransferModes(TransferMode.COPY);
                        budgetLabel.setStyle("-fx-border-color: BLACK;");
                        event.consume();
                    }
            );

            budgetLabel.addEventHandler(
                    DragEvent.DRAG_DROPPED,
                    event -> {
                        if(event.getTransferMode() == TransferMode.COPY && event.getGestureSource() != budgetLabel) {

                            ObservableList<Transaction> spendingTransactions = tblSpending.getSelectionModel().getSelectedItems();
                            ObservableList<Transaction> incomeTransactions = tblIncome.getSelectionModel().getSelectedItems();
                            String tranDate = "";
                            String tranCat = "NULL";
                            String accountName = cmbAcc.getSelectionModel().getSelectedItem();
                            if (event.getGestureSource() == tblSpending) {
                                for (Transaction it : spendingTransactions) {
                                    tranDate = formatDate.formatDate(it.getTransactionDate());
                                    String transactionDescription = it.getTransactionDescription();
                                    String transactionAmount = it.getTransactionAmount();

                                    ObservableList<Transaction> dupTran = sqlTransaction.findIfDuplicateTransaction(tranDate, transactionDescription, transactionAmount);
                                    if (dupTran != null) {
                                        launchDuplicateWindow(accountName, e, tranCat, tranDate, transactionDescription, transactionAmount, dupTran);
                                    } else {
                                        sqlTransaction.newTransaction(accountName, e, tranCat, tranDate, transactionDescription, transactionAmount);
                                    }
                                }
                                tblSpending.getItems().removeAll(spendingTransactions);
                            } else if (event.getGestureSource() == tblIncome) {
                                for (Transaction tran : incomeTransactions) {
                                        tranDate = formatDate.formatDate(tran.getTransactionDate());
                                        String tranDesc = tran.getTransactionDescription();
                                        String tranAmnt = tran.getTransactionAmount();
                                        sqlTransaction.newTransaction(accountName, e, tranCat, tranDate, tranDesc, tranAmnt);
                                }
                                tblIncome.getItems().removeAll(incomeTransactions);
                            }
                        }
                        event.consume();
            });
        }
    }

    private void addCategoryLabels(Label budgetLabel, String e) {
        budgetLabel.setStyle("-fx-border-color: #3399ff;");
        String selectedCategory = budgetLabel.getText();
        ObservableList<String> transactionCategories = sqlTransaction.listTransactionCategory(selectedCategory);

        vBoxCategory.getChildren().clear();
        for (String category : transactionCategories) {
            Label categoryLabel = new Label(category);
            categoryLabel.setText(category);
            categoryLabel.setStyle("-fx-border-color: BLACK;");
            categoryLabel.setPrefSize(243, 71);
            categoryLabel.setMinHeight(30);
            vBoxCategory.getChildren().add(categoryLabel);

            categoryLabel.addEventHandler(
                    DragEvent.DRAG_OVER,
                    labelEvent -> {
                        labelEvent.acceptTransferModes(TransferMode.COPY);
                        categoryLabel.setStyle("-fx-border-color: #3399ff;");
                        labelEvent.consume();
                    });

            categoryLabel.addEventHandler(
                    DragEvent.DRAG_EXITED,
                    labelEvent -> {
                        labelEvent.acceptTransferModes(TransferMode.COPY);
                        categoryLabel.setStyle("-fx-border-color: BLACK;");
                        labelEvent.consume();
                    }
            );

            categoryLabel.addEventHandler(
                    DragEvent.DRAG_DROPPED,
                    labelEvent -> {
                        if(labelEvent.getTransferMode() == TransferMode.COPY && labelEvent.getGestureSource() != budgetLabel) {

                            ObservableList<Transaction> spendingTransactions = tblSpending.getSelectionModel().getSelectedItems();
                            ObservableList<Transaction> incomeTransactions = tblIncome.getSelectionModel().getSelectedItems();
                            String tranDate = "";
                            String accountName = cmbAcc.getSelectionModel().getSelectedItem();
                            if (labelEvent.getGestureSource() == tblSpending) {
                                for (Transaction it : spendingTransactions) {
                                    tranDate = formatDate.formatDate(it.getTransactionDate());
                                    String transactionDescription = it.getTransactionDescription();
                                    String transactionAmount = it.getTransactionAmount();

                                    ObservableList<Transaction> dupTran = sqlTransaction.findIfDuplicateTransaction(tranDate, transactionDescription, transactionAmount);
                                    if (dupTran != null) {
                                        launchDuplicateWindow(accountName, e, category, tranDate, transactionDescription, transactionAmount, dupTran);
                                    } else {
                                        sqlTransaction.newTransaction(accountName, e, category, tranDate, transactionDescription, transactionAmount);
                                    }
                                }
                                tblSpending.getItems().removeAll(spendingTransactions);
                            } else if (labelEvent.getGestureSource() == tblIncome) {
                                for (Transaction tran : incomeTransactions) {
                                    tranDate = formatDate.formatDate(tran.getTransactionDate());
                                    String tranDesc = tran.getTransactionDescription();
                                    String tranAmnt = tran.getTransactionAmount();
                                    sqlTransaction.newTransaction(accountName, e, category, tranDate, tranDesc, tranAmnt);
                                }
                                tblIncome.getItems().removeAll(incomeTransactions);
                            }
                        }
                        labelEvent.consume();
                    });
        }
    }

    private void launchDuplicateWindow(String tranAccount, String tranBudget, String tranCat, String tranDate, String tranDesc, String tranAmnt, ObservableList<Transaction> transaction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/popup/duplicateTransactionWindow.fxml"));
            AnchorPane newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Duplicate Transaction Found");
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);
            DuplicateTransactionWindowController duplicateTransactionWindow = loader.getController();
            duplicateTransactionWindow.listDuplicateTransactions(tranAccount, tranBudget, tranCat, tranDate, tranDesc, tranAmnt, transaction);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean addMissingTransactions(String transactionType) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.NONE, "Are you sure you would like to end this import before adding these transactions?", yes, no);
        alert.setTitle("Transactions Not Added");
        alert.setHeaderText("Some " + transactionType + " transactions have not been added.");
//        alert.setContentText();
        Optional<ButtonType> result = alert.showAndWait();

        return result.filter(buttonType -> buttonType == no).isPresent();
    }

}
