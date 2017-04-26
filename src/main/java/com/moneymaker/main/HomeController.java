package com.moneymaker.main;

import com.moneymaker.modules.budgetmanager.BudgetGraph;
import com.moneymaker.modules.goalmanager.Goal;
import com.moneymaker.modules.goalmanager.SQLGoal;
import com.moneymaker.utilities.SQLMethods;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.abs;

/**
 * Created for MoneyMaker by Jay Damon on 10/8/2016.
 */
public class HomeController implements Initializable {

    @FXML
    private ListView<String> listViewMonths;
    @FXML
    private ListView<Integer> listViewYears;

    @FXML
    private StackPane stackPaneBudget, stackPaneGoals;

    @FXML
//    private BarChart barChartGoals;

    private SQLMethods sqlMethods = new SQLMethods();

    public void initialize(URL url, ResourceBundle rs) {
        Calendar today = Calendar.getInstance();
        int thisYearInt = today.get(Calendar.YEAR);
        String thisYear = String.valueOf(thisYearInt);
        String thisMonth = new DateFormatSymbols().getMonths()[today.get(Calendar.MONTH)];
        addMonths(thisMonth);
        addYears(thisYear);
        fillBudgetGraph(null, 0);

        listViewMonths.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                int selectedIndex = listViewMonths.getSelectionModel().getSelectedIndex();
                int selectedYear = listViewYears.getSelectionModel().getSelectedItem();
                String selectedMonth = listViewMonths.getItems().get(selectedIndex + 1);
                fillBudgetGraph(selectedMonth, selectedYear);
            }
            if (event.getCode() == KeyCode.UP) {
                int selectedIndex = listViewMonths.getSelectionModel().getSelectedIndex();
                int selectedYear = listViewYears.getSelectionModel().getSelectedItem();
                String selectedMonth = listViewMonths.getItems().get(selectedIndex - 1);
                fillBudgetGraph(selectedMonth, selectedYear);
            }
        });

        listViewMonths.setOnMouseClicked(event -> fillBudgetGraph(null, 0));

        listViewYears.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                int selectedIndex = listViewYears.getSelectionModel().getSelectedIndex();
                int selectedYear = listViewYears.getItems().get(selectedIndex + 1);
                String selectedMonth = listViewMonths.getSelectionModel().getSelectedItem();
                fillBudgetGraph(selectedMonth, selectedYear);
            }
            if (event.getCode() == KeyCode.UP) {
                int selectedIndex = listViewYears.getSelectionModel().getSelectedIndex();
                int selectedYear = listViewYears.getItems().get(selectedIndex - 1);
                String selectedMonth = listViewMonths.getSelectionModel().getSelectedItem();
                fillBudgetGraph(selectedMonth, selectedYear);
            }
        });

        listViewYears.setOnMouseClicked(event -> fillBudgetGraph(null, 0));

        fillGoalGraph(null, 0);

    }

    private void addMonths(String thisMonth) {
        ObservableList<String> monthsList = FXCollections.observableArrayList();

        String[] months = new DateFormatSymbols().getMonths();
        Collections.addAll(monthsList, months);

        listViewMonths.getItems().clear();
        listViewMonths.setItems(monthsList);
        int count = 0;
        for (String o : listViewMonths.getItems()) {
            if (o.equals(thisMonth)) {
                listViewMonths.getSelectionModel().select(count);
                listViewMonths.scrollTo(count);
                break;
            }
            count++;
        }
    }

    private void addYears(String thisYear) {
        ObservableList<Integer> yearList = FXCollections.observableArrayList();

        for (int i = 1980; i < 2050; i++) {
            yearList.add(i);
        }

        listViewYears.setItems(yearList);
        int count = 0;
        for(Integer o : listViewYears.getItems()) {
            if (o.toString().equals(thisYear)) {
                listViewYears.getSelectionModel().select(count);
                listViewYears.scrollTo(count);
                break;
            }
            count++;
        }
    }

    private void fillBudgetGraph(String stringSelectedMonth, int selectedYear) {

        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();

        final BarChart<Number, String> barChartPlanned = new BarChart<>(xAxis, yAxis);
        final BarChart<Number, String> barChartActual = new BarChart<>(xAxis, yAxis);

        if (stringSelectedMonth == null) {
            stringSelectedMonth = listViewMonths.getSelectionModel().getSelectedItem();
        }
        if (selectedYear == 0) {
            selectedYear = listViewYears.getSelectionModel().getSelectedItem();
        }
        Date dateSelectedMonth = new Date();
        try {
            dateSelectedMonth = new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(stringSelectedMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendarSelectedMonth = Calendar.getInstance();
        Calendar calendarStartDate = Calendar.getInstance();
        Calendar calendarEndDate = Calendar.getInstance();
        calendarSelectedMonth.setTime(dateSelectedMonth);

        int selectedMonth = calendarSelectedMonth.get(Calendar.MONTH);

        calendarStartDate.set(selectedYear, selectedMonth, 1);
        int lastDayOfMonth = calendarStartDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendarEndDate.set(selectedYear, selectedMonth, lastDayOfMonth);
        SimpleDateFormat formatter = new SimpleDateFormat(FormatDate.SQL_INPUT_DATE);
        String stringStartDate = formatter.format(calendarStartDate.getTime());
        String stringEndDate = formatter.format(calendarEndDate.getTime());
        ObservableList<BudgetGraph> budgetGraph = sqlMethods.graphBudgets(stringStartDate, stringEndDate);
        xAxis.setLabel("Dollars");
        yAxis.setLabel("Category");
        XYChart.Series<Number, String> series1 = new XYChart.Series<>();
        XYChart.Series<Number, String> series2 = new XYChart.Series<>();
        series1.setName("Actual");
        series2.setName("Planned");
        xAxis.setTickLabelRotation(90);
        ObservableList<String> categories = FXCollections.observableArrayList();
        int maxValue = 0;
        for (BudgetGraph bg: budgetGraph) {
            String budget = bg.getBudget();
            String planned = bg.getPlannedAmount();
            String actual = bg.getActualAmount();

            Float floatPlanned;
            Float floatActual;
            Float absFloatPlanned = 0f;
            Float absFloatActual = 0f;

            categories.add(budget);

            if (planned != null) {
                floatPlanned = Float.parseFloat(planned);
                absFloatPlanned = abs(floatPlanned);
            }

            if (actual != null) {
                floatActual = Float.parseFloat(actual);
                absFloatActual = abs(floatActual);
            }

            if (absFloatActual > maxValue) {
                maxValue = Math.round(absFloatActual);
            }
            if (absFloatPlanned > maxValue) {
                maxValue = Math.round(absFloatPlanned);
            }

            final XYChart.Data<Number, String> dataActual = new XYChart.Data<>(absFloatActual, budget);
            final XYChart.Data<Number, String> dataPlanned = new XYChart.Data<>(absFloatPlanned, budget);

            dataActual.nodeProperty().addListener((ov, oldNode, node) -> {
                if (node != null) {
                    double doublePlanned = Double.parseDouble(dataPlanned.getXValue().toString());
                    double doubleActual = Double.parseDouble(dataActual.getXValue().toString());

                    if (Float.parseFloat(bg.getActualAmount())>=0) {
                        if (doubleActual < doublePlanned) {
                            dataActual.getNode().setStyle("-fx-bar-fill: red;");
                        } else if (doubleActual >= doublePlanned) {
                            dataActual.getNode().setStyle("-fx-bar-fill: green;");
                        }
                    } else {
                        if (doubleActual > doublePlanned) {
                            dataActual.getNode().setStyle("-fx-bar-fill: red;");
                        } else if (doubleActual <= doublePlanned) {
                            dataActual.getNode().setStyle("-fx-bar-fill: green;");
                        }
                    }
                }
            });

            dataPlanned.nodeProperty().addListener((ov, oldNode, node) -> {
                if (node != null) {
                    double doublePlanned = Double.parseDouble(dataPlanned.getXValue().toString());
                    double doubleActual = Double.parseDouble(dataActual.getXValue().toString());

                    if (Float.parseFloat(bg.getActualAmount()) < 0 && doubleActual > doublePlanned) {
                        dataPlanned.getNode().setStyle("-fx-bar-fill: white;");
                        }
                }
            });
            series1.getData().add(dataActual);
            series2.getData().add(dataPlanned);
        }

        xAxis.autoRangingProperty().setValue(false);

        GraphBoundValues graphBoundValues = new GraphBoundValues(maxValue);
        maxValue = graphBoundValues.getMaxValue();
        double tickUnit = graphBoundValues.getTickUnit();
//        double maxValueLastTwo = maxValue % 100;
//        double intermediateMaxValue;
//        if(maxValueLastTwo == 0) {
//            intermediateMaxValue = maxValue;
//        } else {
//            intermediateMaxValue = maxValue + (100 - (maxValue % 100));
//        }
//        maxValue = (int) Math.round(intermediateMaxValue + (intermediateMaxValue*.1));
//        double tickUnit;
//
//        if(isBetween(maxValue, 0, 250)) {
//            tickUnit = 10.0;
//            maxValue = (int) (Math.ceil(maxValue/tickUnit) * tickUnit);
//        } else if (isBetween(maxValue, 251, 750)) {
//            tickUnit = 50.0;
//            maxValue = (int) (Math.ceil(maxValue/tickUnit) * tickUnit);
//        } else if (isBetween(maxValue, 751, 1500)) {
//            tickUnit = 100.0;
//            maxValue = (int) (Math.ceil(maxValue/tickUnit) * tickUnit);
//        } else if (isBetween(maxValue, 1501, 5000)) {
//            tickUnit = 200.0;
//            maxValue = (int) (Math.ceil(maxValue/tickUnit) * tickUnit);
//        } else {
//            tickUnit = 500.0;
//            maxValue = (int) (Math.ceil(maxValue/tickUnit) * tickUnit);
//        }

        xAxis.setTickUnit(tickUnit);
        xAxis.setUpperBound(maxValue);
        yAxis.getCategories().clear();
        yAxis.setCategories(categories);
        barChartActual.getData().clear();
        barChartActual.getData().add(series1);
        barChartPlanned.getData().clear();
        barChartPlanned.getData().add(series2);

        barChartActual.setTitle(stringSelectedMonth + " Budget");
        barChartActual.setLegendVisible(false);
        barChartActual.setAnimated(false);
        barChartActual.setStyle("-fx-background-color: transparent;");
        barChartActual.getStylesheets().setAll(getClass().getClassLoader().getResource("css/OverlayBarChartStyle.css").toExternalForm());

        barChartPlanned.setTitle("");
        barChartPlanned.getStylesheets().setAll(getClass().getClassLoader().getResource("css/BackgroundBarChartStyle.css").toExternalForm());
        barChartPlanned.setAnimated(false);
        barChartPlanned.setLegendVisible(false);
        barChartPlanned.setAlternativeColumnFillVisible(false);
        barChartPlanned.setAlternativeRowFillVisible(false);
        barChartPlanned.setHorizontalGridLinesVisible(false);
        barChartPlanned.setVerticalGridLinesVisible(false);
        barChartPlanned.setStyle("-fx-background-color: transparent;");


        stackPaneBudget.getChildren().clear();
        stackPaneBudget.getChildren().addAll(barChartActual, barChartPlanned);
    }

    private static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    private void fillGoalGraph(String stringSelectedMonth, int selectedYear) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        final BarChart<String, Number> barChartPlanned = new BarChart<>(xAxis, yAxis);
        final BarChart<String, Number> barChartActual = new BarChart<>(xAxis, yAxis);

        SQLGoal sqlGoal = new SQLGoal();
        ObservableList<Goal> goals = sqlGoal.viewGoals();
        FormatDollarAmount formatDollarAmount = new FormatDollarAmount();
        ObservableList<String> categories = FXCollections.observableArrayList();

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();

        int maxValue = 0;

        for (Goal g: goals) {
            String goalName = g.getGoalName();
            String goalPlanned = g.getGoalAmount();
            String goalActual = g.getGoalActualAmount();

            Float floatGoalPlanned = Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(goalPlanned));
            Float floatGoalActual = Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(goalActual));

            categories.add(goalName);

            final XYChart.Data<String, Number> dataPlanned = new XYChart.Data<>(goalName, floatGoalPlanned);
            final XYChart.Data<String, Number> dataActual = new XYChart.Data<>(goalName, floatGoalActual);

            if (floatGoalActual > maxValue) {
                maxValue = Math.round(floatGoalActual);
            }

            if (floatGoalPlanned > maxValue) {
                maxValue = Math.round(floatGoalPlanned);
            }

            series1.getData().add(dataPlanned);
            series2.getData().add(dataActual);
        }

        yAxis.autoRangingProperty().setValue(false);

        GraphBoundValues graphBoundValues = new GraphBoundValues(maxValue);

        yAxis.setUpperBound(graphBoundValues.getMaxValue());
        yAxis.setTickUnit(graphBoundValues.getTickUnit());

        xAxis.getCategories().clear();
        xAxis.setCategories(categories);

        barChartPlanned.getData().clear();
        barChartPlanned.getData().add(series1);
        barChartActual.getData().clear();
        barChartActual.getData().add(series2);

        barChartActual.setTitle("Goals");
        barChartActual.setLegendVisible(false);
        barChartActual.setAnimated(false);
        barChartActual.setStyle("-fx-background-color: transparent;");

        barChartPlanned.setTitle("");
        barChartPlanned.setAnimated(false);
        barChartPlanned.setLegendVisible(false);
        barChartPlanned.setAlternativeColumnFillVisible(false);
        barChartPlanned.setAlternativeRowFillVisible(false);
        barChartPlanned.setHorizontalGridLinesVisible(false);
        barChartPlanned.setVerticalGridLinesVisible(false);

        barChartPlanned.getStylesheets().setAll(getClass().getClassLoader().getResource("css/GoalGraphStyleOverlay.css").toExternalForm());
        barChartActual.getStylesheets().setAll(getClass().getClassLoader().getResource("css/GoalGraphStyleBackground.css").toExternalForm());

        stackPaneGoals.getChildren().addAll(barChartPlanned, barChartActual);
    }

    private class GraphBoundValues {
        int maxValue;
        double tickUnit;

        private GraphBoundValues(int maxValue) {

            double maxValueLastTwo = maxValue % 100;
            double intermediateMaxValue;
            if (maxValueLastTwo == 0) {
                intermediateMaxValue = maxValue;
            } else {
                intermediateMaxValue = maxValue + (100 - (maxValue % 100));
            }
            maxValue = (int) Math.round(intermediateMaxValue + (intermediateMaxValue * .1));
            double tickUnit;

            if (isBetween(maxValue, 0, 250)) {
                tickUnit = 10.0;
                maxValue = (int) (Math.ceil(maxValue / tickUnit) * tickUnit);
            } else if (isBetween(maxValue, 251, 750)) {
                tickUnit = 50.0;
                maxValue = (int) (Math.ceil(maxValue / tickUnit) * tickUnit);
            } else if (isBetween(maxValue, 751, 1500)) {
                tickUnit = 100.0;
                maxValue = (int) (Math.ceil(maxValue / tickUnit) * tickUnit);
            } else if (isBetween(maxValue, 1501, 5000)) {
                tickUnit = 200.0;
                maxValue = (int) (Math.ceil(maxValue / tickUnit) * tickUnit);
            } else {
                tickUnit = 500.0;
                maxValue = (int) (Math.ceil(maxValue / tickUnit) * tickUnit);
            }

            setMaxValue(maxValue);
            setTickUnit(tickUnit);
        }

        private void setMaxValue(int maxValue) {
            this.maxValue = maxValue;
        }

        private void setTickUnit(double tickUnit) {
            this.tickUnit = tickUnit;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public double getTickUnit() {
            return tickUnit;
        }
    }


}
