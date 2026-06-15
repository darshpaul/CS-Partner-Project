package com.fitness;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CalendarView implements View {

    private Database db;
    private VBox root;
    private GridPane grid;
    private Label monthLabel;
    private ListView<String> dayList;
    private YearMonth shownMonth;

    public CalendarView(Database db) {
        this.db = db;
        shownMonth = YearMonth.now();
        build();
        refresh();
    }

    private void build() {
        root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("Workout Calendar");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button prev = new Button("< Prev");
        prev.setOnAction(e -> { shownMonth = shownMonth.minusMonths(1); refresh(); });
        Button next = new Button("Next >");
        next.setOnAction(e -> { shownMonth = shownMonth.plusMonths(1); refresh(); });
        monthLabel = new Label();
        monthLabel.setStyle("-fx-font-size: 16px;");
        HBox topRow = new HBox(10, prev, monthLabel, next);
        topRow.setAlignment(Pos.CENTER_LEFT);

        grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        dayList = new ListView<>();
        dayList.setPrefHeight(160);

        root.getChildren().addAll(
            title, topRow, grid,
            new Label("Workouts on the day you click:"), dayList
        );
    }

    public void refresh() {
        monthLabel.setText(shownMonth.getMonth() + " " + shownMonth.getYear());
        grid.getChildren().clear();
        dayList.getItems().clear();

        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int c = 0; c < 7; c++) {
            Label header = new Label(dayNames[c]);
            header.setStyle("-fx-font-weight: bold;");
            grid.add(header, c, 0);
        }

        ArrayList<Workout> workouts = db.getAllWorkouts();

        LocalDate firstDay = shownMonth.atDay(1);
        int daysInMonth = shownMonth.lengthOfMonth();

        int startColumn = firstDay.getDayOfWeek().getValue() - 1;

        int row = 1;
        int col = startColumn;
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = shownMonth.atDay(day);
            String dateText = date.toString();

            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefSize(70, 45);

            if (hasWorkout(workouts, dateText)) {
                dayButton.setStyle("-fx-background-color: #8fd19e;");
            }

            dayButton.setOnAction(e -> showWorkoutsFor(workouts, dateText));
            grid.add(dayButton, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private boolean hasWorkout(ArrayList<Workout> workouts, String date) {
        for (int i = 0; i < workouts.size(); i++) {
            if (workouts.get(i).getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    private void showWorkoutsFor(ArrayList<Workout> workouts, String date) {
        dayList.getItems().clear();
        for (int i = 0; i < workouts.size(); i++) {
            Workout w = workouts.get(i);
            if (w.getDate().equals(date)) {
                dayList.getItems().add(
                    w.getType() + " - " + w.getAmount() + " (" + w.getDuration() + " min)"
                );
            }
        }
        if (dayList.getItems().isEmpty()) {
            dayList.getItems().add("No workouts on " + date + ".");
        }
    }

    public Node getNode() {
        return root;
    }
}
