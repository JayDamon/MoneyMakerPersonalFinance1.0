package com.moneymaker.modules.transfermanager;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created for MoneyMaker by Jay Damon on 10/20/2016.
 */
public class TransferController implements Initializable {

    @FXML
    TableView<Transfer> tableViewTransfers;

    private static SQLTransfer sqlTransfer = new SQLTransfer();

    public void initialize(URL url, ResourceBundle rs) {
        tableViewTransfers.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                AnchorPane pane = new AnchorPane();
                ContextMenu contextMenu = contextMenu();
                tableViewTransfers.setContextMenu(contextMenu);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
            }
            tableViewTransfers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            event.consume();
        });

        tableViewTransfers.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.DELETE) {
                deleteTransfer();
            }
        });
        showTransfers();
    }

    @FXML
    private void showTransfers() {
        tableViewTransfers.setItems(sqlTransfer.viewTransfers());
    }

    @FXML
    private void newTransfer() {

        Stage stage = new Stage();
        stage.setTitle("New Transfer");
        GridPane gridPane = new GridPane();
        Label label = new Label();
        label.setText("Have the transactions for this transfer already been imported?");
        label.setPrefWidth(350);
        Button buttonYes = new Button();
        buttonYes.setText("Yes");
        buttonYes.setAlignment(Pos.BASELINE_CENTER);
        Button buttonNo = new Button();
        buttonNo.setText("No");
        buttonNo.setAlignment(Pos.BASELINE_CENTER);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(buttonYes, buttonNo);
        hBox.setAlignment(Pos.BASELINE_RIGHT);

        gridPane.setVgap(10);
        gridPane.setHgap(20);
        gridPane.add(label, 1, 1);
        gridPane.add(hBox, 1, 2);

        buttonYes.setOnAction(event -> {
            event.consume();
            Stage getStage = (Stage)buttonYes.getScene().getWindow();
            getStage.close();
            launchNewTransferWithTransactions();
        });

        buttonNo.setOnAction(event -> {
            event.consume();
            Stage getStage = (Stage)buttonNo.getScene().getWindow();
            getStage.close();
            launchNewTransferWithoutTransactions();
        });

        stage.setScene(new Scene(gridPane, 390,80));
        stage.showAndWait();
        showTransfers();
    }

    @FXML
    private void updateTransfer() {
        ObservableList<Transfer> transfer = tableViewTransfers.getSelectionModel().getSelectedItems();
        if (transfer.size() == 1) {
            String transferID = tableViewTransfers.getSelectionModel().getSelectedItem().getTransferID();
            String transferDate = tableViewTransfers.getSelectionModel().getSelectedItem().getTransferDate();
            String transferCategory = tableViewTransfers.getSelectionModel().getSelectedItem().getTransferType();
            String fromAccount = tableViewTransfers.getSelectionModel().getSelectedItem().getTransferFromAccount();
            String toAccount = tableViewTransfers.getSelectionModel().getSelectedItem().getTransferToAccount();
            String transferAmount = tableViewTransfers.getSelectionModel().getSelectedItem().getTransferAmount();
            String fromTransactionID = tableViewTransfers.getSelectionModel().getSelectedItem().getTransferFromTransactionID();
            String toTransactionID = tableViewTransfers.getSelectionModel().getSelectedItem().getTransferToTransactionID();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/inputwindows/updateTransferWindow.fxml"));
            try {
                AnchorPane newWindow = loader.load();
                Stage stage = new Stage();
                stage.setTitle(("Update Transfer"));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(tableViewTransfers.getScene().getWindow());
                Scene scene = new Scene(newWindow);
                stage.setScene(scene);

                UpdateTransferController updateTransferController = loader.getController();
                updateTransferController.fillComboBoxes(transferCategory, fromAccount, toAccount);
                String[] fromTransferTransaction = sqlTransfer.getTransferTransaction(fromTransactionID);
                String[] toTransferTransaction = sqlTransfer.getTransferTransaction(toTransactionID);
                updateTransferController.addFromTransaction(fromTransferTransaction[0], fromTransferTransaction[1],
                        fromTransferTransaction[2], fromTransferTransaction[3]);
                updateTransferController.addToTransaction(toTransferTransaction[0], toTransferTransaction[1],
                        toTransferTransaction[2], toTransferTransaction[3]);
                updateTransferController.setTransferID(transferID);

                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showTransfers();
        }
    }

    private void launchNewTransferWithTransactions() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/inputwindows/newTransferWindow.fxml"));
        try {
            AnchorPane newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle(("New Transfer"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tableViewTransfers.getScene().getWindow());
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchNewTransferWithoutTransactions() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/inputwindows/newTransferNoTransactionWindow.fxml"));
        try {
            AnchorPane newWindow = loader.load();
            Stage stage = new Stage();
            stage.setTitle(("New Transfer"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tableViewTransfers.getScene().getWindow());
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showTransfers();
    }

    @FXML
    private void deleteTransfer() {
        ObservableList<Transfer> transfers = tableViewTransfers.getSelectionModel().getSelectedItems();
        if (transfers.size() != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            if (transfers.size() == 1) {
                alert.setHeaderText("Delete Selected Transfer");
                alert.setContentText("Are you sure you want to delete the selected transfer? " +
                        "This will permanently delete the transfer and all associated transactions.");
                alert.setHeaderText("Delete Selected Transfers");
            } else if (transfers.size() > 1) {
                alert.setContentText("Are you sure you want to delete the selected transfers? " +
                        "This will permanently delete the transfers and all associated transactions.");

            }
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent())
                if (result.get() == ButtonType.OK) {
                    for (Transfer t : transfers) {
                        sqlTransfer.deleteTransfer(Integer.parseInt(t.getTransferID()));
                    }
                }
        }
        showTransfers();
    }

    private ContextMenu contextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem update = new MenuItem("Update");
        MenuItem newTransfer = new MenuItem("New");
        MenuItem delete = new MenuItem("Delete");

        update.setOnAction(event -> {
            updateTransfer();
            event.consume();
        });

        newTransfer.setOnAction(event -> {
            newTransfer();
            event.consume();
        });

        delete.setOnAction(event -> {
            deleteTransfer();
            event.consume();
        });

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(update, newTransfer, delete);

        return contextMenu;
    }
}
