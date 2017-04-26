package com.moneymaker.modules.budgetmanager;

import com.moneymaker.modules.transactionmanager.transactions.Transaction;
import com.moneymaker.modules.transactionmanager.transactions.TransactionCategory;
import com.moneymaker.modules.transactionmanager.transactions.SQLTransaction;
import com.moneymaker.modules.transactionmanager.transactions.TransactionCategoryController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created for MoneyMaker by jaynd on 3/28/2016.
 */
public class BudgetController implements Initializable{

    @FXML
    private TableView<Budget> tblBudget;

    @FXML
    private TableView<TransactionCategory> tblTranCat;

    @FXML
    private Button btnUpdateEntry, btnBudgetCategories, btnUncategorizedTransactions;

    private SQLBudget budSQL = new SQLBudget();
    private SQLTransaction tranSQL = new SQLTransaction();

    public void initialize(URL location, ResourceBundle resources) {
        showBudget();
        tblBudget.addEventHandler(MouseEvent.MOUSE_CLICKED,
        event -> {
            if (!tblBudget.getSelectionModel().getSelectedItems().isEmpty()) {
                String budgetCategory = tblBudget.getSelectionModel().getSelectedItem().getBudgetName();
                ObservableList<TransactionCategory> data = tblTranCat.getItems();
                data.clear();
                ObservableList<String> transactionCategories = tranSQL.listTransactionCategory(budgetCategory);
                data.addAll(transactionCategories.stream().map(TransactionCategory::new).collect(Collectors.toList()));
            }
        });

        tblBudget.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                AnchorPane pane = new AnchorPane();
                ContextMenu contextMenu = launchContextWindow();
                tblBudget.setContextMenu(contextMenu);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
                event.consume();
            }
        });
    }

    public void showBudget() {
        ObservableList<Budget> data = tblBudget.getItems();
        data.clear();
        data.addAll(budSQL.viewBudget());

    }

    @FXML
    private void deleteBudget() {
        ObservableList<Budget> budgets = tblBudget.getSelectionModel().getSelectedItems();
            if (budgets.size() != 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                if (budgets.size() > 1) {
                    alert.setHeaderText("Delete Selected Budgets");
                    alert.setContentText("Are you sure you want to delete the selected Budgets? " +
                        "This will permanently delete them.");
                } else {
                    alert.setHeaderText("Delete Selected Budget");
                    alert.setContentText("Are you sure you want to delete the selected Budget? " +
                            "This will permanently delete it.");
                }
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        for (Budget b : budgets) {
                            int budGenStatus = budSQL.getBudgetGeneric(b.getBudgetName());
                            String budgetName = b.getBudgetName();
                            budSQL.deleteBudget(budGenStatus, budgetName);
                        }
                    }
                }
            }
            showBudget();
    }

    @FXML
    private void updateBudget() {

        int selectedIndex = tblBudget.getSelectionModel().getSelectedIndex();

        if (selectedIndex != -1) {
            Budget selBud = tblBudget.getItems().get(selectedIndex);

            String budName = selBud.getBudgetName();
            String budStart = selBud.getBudgetStartDate();
            String budEnd = selBud.getBudgetEndDate();
            String budFreq = selBud.getBudgetFrequency();
            String budAmount = selBud.getBudgetAmount();
            String budID = selBud.getBudgetID();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/inputwindows/updateBudget.fxml"));
            AnchorPane newWindow = null;
            try {
                newWindow = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle(budName);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnUpdateEntry.getScene().getWindow());

            if (newWindow != null) {
                Scene scene = new Scene(newWindow);
                stage.setScene(scene);
                UpdateBudgetController updateBudget = loader.getController();
                updateBudget.setComponentValues(budID, budName, budStart, budEnd, budFreq, budAmount);
                stage.showAndWait();
            }
            showBudget();
        }
    }

    @FXML
    private void openBudgetCategoryList() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/popup/budgetTableWindow.fxml"));
        AnchorPane newWindow = null;
        try {
            newWindow = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle(("Budget Categories"));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnBudgetCategories.getScene().getWindow());
        Scene scene = null;
        if (newWindow != null) {
            scene = new Scene(newWindow);
        }
        stage.setScene(scene);
        stage.showAndWait();

        showBudget();
    }

    @FXML
    private void showUncategorizedTransactions() {
        int selectedIndex = tblBudget.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            int numberOfUncategorizedTransactions = Integer.parseInt(tblBudget.getSelectionModel().getSelectedItem().getUncategorizedTransactions());
            String budgetName = tblBudget.getSelectionModel().getSelectedItem().getBudgetName();
                if (numberOfUncategorizedTransactions != 0) {
                    String budgetID = tblBudget.getSelectionModel().getSelectedItem().getBudgetID();
                    ObservableList<Transaction> uncategorizedTransactions = tranSQL.viewUncategorizedTransactions(budgetID);

                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/popup/transactionCategoryWindow.fxml"));
                    AnchorPane newWindow = null;
                    try {
                        newWindow = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle(budgetName);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(btnUncategorizedTransactions.getScene().getWindow());
                    Scene scene = null;
                    if (newWindow != null) {
                        scene = new Scene(newWindow);
                    }
                    stage.setScene(scene);

                    TransactionCategoryController transactionCategoryController = loader.getController();
                    transactionCategoryController.addUncategorizedTransactions(uncategorizedTransactions, budgetName);
                    transactionCategoryController.setBudgetID(budgetID);

                    stage.showAndWait();
                } else {
                    ButtonType btnSelectOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    Dialog<String> dialog = new Dialog<>();
                    dialog.getDialogPane().getButtonTypes().add(btnSelectOK);
                    dialog.setContentText("All \"" + budgetName + "\" entries have already been categorized.");
                    dialog.getDialogPane().lookupButton(btnSelectOK).setDisable(false);
                    dialog.showAndWait();
                }
        }
        showBudget();
    }

    @FXML
    protected void buttonClickRefreshTable() {
        showBudget();
    }

    private ContextMenu launchContextWindow() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem update = new MenuItem("Update");
        MenuItem delete = new MenuItem("Delete");
        MenuItem budgetCategories = new MenuItem("Budget Categories");
        MenuItem uncategorizedTransactions = new MenuItem("Uncategorized Transactions");

        update.setOnAction(event -> {
            updateBudget();
            event.consume();
        });

        delete.setOnAction(event -> {
            deleteBudget();
            event.consume();
        });

        budgetCategories.setOnAction(event -> {
            openBudgetCategoryList();
            event.consume();
        });

        uncategorizedTransactions.setOnAction(event -> {
            showUncategorizedTransactions();
            event.consume();
        });

        contextMenu.getItems().addAll(update, delete, budgetCategories, uncategorizedTransactions);
        return contextMenu;

    }
}
