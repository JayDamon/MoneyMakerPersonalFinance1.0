package com.moneymaker.modules.budgetmanager;

import com.moneymaker.main.UsernameData;
import com.moneymaker.utilities.ConnectionManager.ConnectionManagerUser;
import com.moneymaker.main.MainWindowController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by jaynd on 6/26/2016.
 */
public class BudgetWindowController implements Initializable {

    private static Connection conn = ConnectionManagerUser.getInstance().getConnection();

    @FXML private Button btnExitWindow;

    @FXML private TextField txtCustomBudgetCategory;

    @FXML private ListView<String> listViewBudget, listViewInactiveBudget;

    private final ObservableList<String> budgetCategories = FXCollections.observableArrayList();
    private final ObservableList<String> genericBudgetCategories = FXCollections.observableArrayList();

    public boolean newUser = false;

    private SQLBudget budSQL = new SQLBudget();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showActiveBudgets();
        showInactiveBudgets();

//        listViewBudget.setItems(budSQL.listActiveBudget());
//        listViewInactiveBudget.setItems(budSQL.inactiveBudgets());

        listViewInactiveBudget.setOnDragDetected(event -> {
            Dragboard db = listViewInactiveBudget.startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putString(listViewInactiveBudget.getSelectionModel().getSelectedItem());
            db.setContent(content);
            event.consume();
        });

        listViewBudget.setOnDragDetected(event -> {
            Dragboard db = listViewBudget.startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putString(listViewBudget.getSelectionModel().getSelectedItem());
            db.setContent(content);
            event.consume();
        });

        //Add Drag Event to Transaction Date text field
        listViewInactiveBudget.addEventHandler(
                DragEvent.DRAG_OVER,
                event -> {
                    if(event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY);
                        listViewInactiveBudget.requestFocus();
                    }
                    event.consume();
                });

        listViewInactiveBudget.addEventHandler(
                DragEvent.DRAG_DROPPED,
                event -> {
                    Dragboard dragboard = event.getDragboard();
                    if(event.getTransferMode() == TransferMode.COPY && dragboard.hasString() && event.getGestureSource() != listViewInactiveBudget) {

                            deleteBudgetName(dragboard.getString());
                            budgetCategories.clear();
                            genericBudgetCategories.clear();

                            listViewBudget.setItems(budgetCategories);
                            listViewInactiveBudget.setItems(genericBudgetCategories);

                            showActiveBudgets();
                            showInactiveBudgets();

                        event.setDropCompleted(true);
                    }
                    event.consume();
                }
        );

        //Add Drag Event to Transaction Date text field
        listViewBudget.addEventHandler(
                DragEvent.DRAG_OVER,
                event -> {
                    if(event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY);
                        listViewBudget.requestFocus();
                    }
                    event.consume();
                });

        listViewBudget.addEventHandler(
                DragEvent.DRAG_DROPPED,
                event -> {
                    Dragboard dragboard = event.getDragboard();
                    if(event.getTransferMode() == TransferMode.COPY && dragboard.hasString() && event.getGestureSource() != listViewBudget) {
//                        listViewGenericBudgetCategoriesList.setText(dragboard.getString());

                            addBudgetName(dragboard.getString());

                            budgetCategories.clear();
                            genericBudgetCategories.clear();

                            listViewBudget.setItems(budgetCategories);
                            listViewInactiveBudget.setItems(genericBudgetCategories);

                            showActiveBudgets();
                            showInactiveBudgets();

                        event.setDropCompleted(true);
                    }
                    event.consume();
                }
        );
    }

    public void setExitTextForNewUser() {
        btnExitWindow.setText("Next");
    }

    @FXML
    protected void addBudget() throws SQLException {
        String newCat = listViewInactiveBudget.getSelectionModel().getSelectedItem();
        addBudgetName(newCat);

        budgetCategories.clear();
        genericBudgetCategories.clear();

        listViewBudget.setItems(budgetCategories);
        listViewInactiveBudget.setItems(genericBudgetCategories);

        showActiveBudgets();
        showInactiveBudgets();
    }

    @FXML
    protected void removeBudget() throws SQLException {

        String oldCat = listViewBudget.getSelectionModel().getSelectedItem();

        deleteBudgetName(oldCat);

        budgetCategories.clear();
        genericBudgetCategories.clear();

        listViewBudget.setItems(budgetCategories);
        listViewInactiveBudget.setItems(genericBudgetCategories);

        showActiveBudgets();
        showInactiveBudgets();
    }

    @FXML
    protected void addCustomBudget() throws SQLException {
        String newCat = txtCustomBudgetCategory.getText();

        addBudgetName(newCat);

        budgetCategories.clear();
        genericBudgetCategories.clear();

        listViewBudget.setItems(budgetCategories);
        listViewInactiveBudget.setItems(genericBudgetCategories);

        showActiveBudgets();
        showInactiveBudgets();
    }

    @FXML
    protected void exitWindow(ActionEvent event) {
        if (newUser) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main/mainWindow.fxml"));
            try {
                AnchorPane primaryStage = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Budget");
                Scene scene = new Scene(primaryStage);
                stage.setScene(scene);
                MainWindowController mainWindow = loader.getController();
                mainWindow.setUserName(new UsernameData().getSessionUsername());
                stage.show();
                ((Node) (event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ((Node) (event.getSource())).getScene().getWindow().hide();
        }
    }

    private void addBudgetName(String budgetName) {
        int budGeneric = budSQL.getBudgetGeneric(budgetName);
// This may not work because budGeneric may be null
        budSQL.addBudget(budGeneric, budgetName);
    }

    private void deleteBudgetName(String budgetName) {
        int budGeneric = budSQL.getBudgetGeneric(budgetName);
//Change to SQLBudget
        budSQL.deleteBudget(budGeneric, budgetName);

    }

    @FXML
    private void showActiveBudgets() {
        for (String s : budSQL.listActiveBudget()) {
            if (!s.equals(new Budget().TRANSFER)) {
                listViewBudget.getItems().add(s);
            }
        }
    }

    @FXML
    private void showInactiveBudgets() {
        for (String s : budSQL.inactiveBudgets()) {
            if (!s.equals(new Budget().TRANSFER)) {
                listViewInactiveBudget.getItems().add(s);
            }
        }
    }
}
