package com.fitness;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class CompareView implements View {

    private Database db;
    private VBox root;
    private ListView<String> summary;
    private BarChart<String, Number> chart;

    public CompareView(Database db) {
        this.db = db;
        build();
        refresh();
    }

    private void build() {
        root = new VBox(12);
        root.setPadding(new Insets(15));

        Label title = new Label("Goals vs. Progress");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Goal");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");
        chart = new BarChart<>(xAxis, yAxis);
        chart.setPrefHeight(320);

        summary = new ListView<>();
        summary.setPrefHeight(220);

        root.getChildren().addAll(title, chart, summary);
    }

    public void refresh() {
        ArrayList<Goal> goals = db.getAllGoals();
        ArrayList<Workout> workouts = db.getAllWorkouts();

        double[] percent = new double[goals.size()];
        for (int i = 0; i < goals.size(); i++) {
            Goal g = goals.get(i);
            double done = totalForType(workouts, g.getType());
            if (g.getTarget() > 0) {
                percent[i] = (done / g.getTarget()) * 100.0;
            } else {
                percent[i] = 0;
            }
        }

        Sorting.bubbleSortByProgress(goals, percent);

        chart.getData().clear();
        XYChart.Series<String, Number> targetSeries = new XYChart.Series<>();
        targetSeries.setName("Target");
        XYChart.Series<String, Number> doneSeries = new XYChart.Series<>();
        doneSeries.setName("Done");

        summary.getItems().clear();
        for (int i = 0; i < goals.size(); i++) {
            Goal g = goals.get(i);
            double done = totalForType(workouts, g.getType());

            targetSeries.getData().add(new XYChart.Data<>(g.getType(), g.getTarget()));
            doneSeries.getData().add(new XYChart.Data<>(g.getType(), done));

            double pct = Math.round(percent[i] * 10.0) / 10.0;
            String tag = g.isCompleted() ? "   [DONE]" : "";
            summary.getItems().add(
                g.getType() + ": " + done + " / " + g.getTarget() + " " + g.getUnit()
                + "   (" + pct + "%)" + tag
            );
        }

        chart.getData().add(targetSeries);
        chart.getData().add(doneSeries);

        if (goals.isEmpty()) {
            summary.getItems().add("No goals yet. Add some on the Goals tab!");
        }
    }

    private double totalForType(ArrayList<Workout> workouts, String type) {
        double total = 0;
        for (int i = 0; i < workouts.size(); i++) {
            if (workouts.get(i).getType().equalsIgnoreCase(type)) {
                total = total + workouts.get(i).getAmount();
            }
        }
        return total;
    }

    public Node getNode() {
        return root;
    }
}
