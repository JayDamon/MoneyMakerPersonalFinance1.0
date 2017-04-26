package com.moneymaker.modules.goalmanager;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Jay Damon on 9/18/2016.
 */
public class GoalsController implements Initializable {

    @FXML
    Button btnNewEntry;

    @FXML
    TableView<Goal> tblGoals;

    private SQLGoal sqlGoal = new SQLGoal();

    public void initialize(URL url, ResourceBundle rs) {
        tblGoals.setOnMouseClicked(event -> tblGoals.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE));

        tblGoals.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                AnchorPane pane = new AnchorPane();
                ContextMenu contextMenu = launchContextMenu();
                tblGoals.setContextMenu(contextMenu);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
                event.consume();
            }
        });

        showGoals();
    }

    private ContextMenu launchContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New");
        MenuItem update = new MenuItem("Update");
        MenuItem delete = new MenuItem("Delete");

        addNew.setOnAction(event -> {
            addGoal();
            showGoals();
            event.consume();
        });

        update.setOnAction(event -> {
            updateGoal();
            showGoals();
            event.consume();
        });

        delete.setOnAction(event -> {
            deleteGoal(tblGoals.getSelectionModel().getSelectedItems());
            showGoals();
            event.consume();
        });

        contextMenu.getItems().addAll(addNew, update, delete);
        return contextMenu;
    }

    private void showGoals() {
        ObservableList<Goal> goals = tblGoals.getItems();
        goals.clear();

        goals.addAll(sqlGoal.viewGoals());
    }

    @FXML
    protected void buttonClickNew() {
        addGoal();
        showGoals();
    }

    @FXML
    protected void buttonClickDelete() {

        showGoals();
    }

    @FXML
    protected void buttonClickUpdate() {
        updateGoal();
        showGoals();
    }

    @FXML
    protected void buttonClickRefreshTable() {
        showGoals();
    }

    private void addGoal() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/inputwindows/newGoalWindow.fxml"));
        AnchorPane newWindow = null;
        try {
            newWindow = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("New Goal");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnNewEntry.getScene().getWindow());
        if (newWindow != null) {
            Scene scene = new Scene(newWindow);
            stage.setScene(scene);

            stage.showAndWait();
        }
    }

    private void updateGoal() {
        ObservableList<Goal> selectedGoal = tblGoals.getSelectionModel().getSelectedItems();
        int selectedCount = selectedGoal.size();

        if (selectedCount == 1) {
            for (Goal g : selectedGoal) {
                String goalID = g.getGoalID();
                String goalName = g.getGoalName();
                String goalPriority = g.getGoalPriority();
                String goalType = g.getGoalType();
                String goalAccount = g.getGoalAccount();
                String goalStartDate = g.getGoalStartDate();
                String goalEndDate = g.getGoalEndDate();
                String goalAmount = g.getGoalAmount();

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/inputwindows/updateGoalWindow.fxml"));
                AnchorPane newWindow = null;
                try {
                    newWindow = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Stage stage = new Stage();
                stage.setTitle("Update " + goalName);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(btnNewEntry.getScene().getWindow());
                if (newWindow != null) {
                    Scene scene = new Scene(newWindow);
                    stage.setScene(scene);

                    UpdateGoalController goalController = loader.getController();

                    goalController.setGoalID(goalID);
                    goalController.fillDatePickers(goalEndDate, goalStartDate);
                    goalController.setComboBoxesAndTextFields(goalName, goalPriority, goalType, goalAccount, goalAmount);

                    stage.showAndWait();
                }
            }
        }
    }

    private void deleteGoal(ObservableList<Goal> goal) {
        if (goal.size() != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Delete Selected Goal");
            alert.setContentText("Are you sure you want to delete the selected Goal? " +
                    "This will permanently delete them.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {

                for (Goal rt : goal) {
                    String goalID = rt.getGoalID();
                    sqlGoal.deleteGoal(goalID);
                }
            }
            showGoals();
        }
    }
}
