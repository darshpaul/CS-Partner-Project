package com.fitness;

import java.time.LocalDate;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class WorkoutView implements View {

    private Database db;
    private VBox root;

    private TextField typeField;
    private TextField amountField;
    private TextField durationField;
    private DatePicker datePicker;
    private Label message;

    public WorkoutView(Database db) {
        this.db = db;
        build();
    }

    private void build() {
        root = new VBox(12);
        root.setPadding(new Insets(15));

        Label title = new Label("Add a Workout");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        typeField = new TextField();
        typeField.setPromptText("e.g. Running");
        amountField = new TextField();
        amountField.setPromptText("e.g. 5");
        durationField = new TextField();
        durationField.setPromptText("e.g. 30");
        datePicker = new DatePicker(LocalDate.now());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Type:"), 0, 0);
        form.add(typeField, 1, 0);
        form.add(new Label("Amount:"), 0, 1);
        form.add(amountField, 1, 1);
        form.add(new Label("Duration (min):"), 0, 2);
        form.add(durationField, 1, 2);
        form.add(new Label("Date:"), 0, 3);
        form.add(datePicker, 1, 3);

        Button saveButton = new Button("Save Workout");
        saveButton.setOnAction(e -> saveWorkout());

        message = new Label("");

        root.getChildren().addAll(title, form, saveButton, message);
    }

    private void saveWorkout() {
        String type = typeField.getText().trim();

        if (type.isEmpty()) {
            message.setText("Please type a workout type.");
            return;
        }

        double amount;
        int duration;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            duration = Integer.parseInt(durationField.getText().trim());
        } catch (NumberFormatException ex) {
            message.setText("Amount and duration must be numbers.");
            return;
        }

        String date = datePicker.getValue().toString();

        db.addWorkout(date, type, amount, duration);

        typeField.clear();
        amountField.clear();
        durationField.clear();
        message.setText("Workout saved for " + date + "!");
    }

    public void refresh() {

    }

    public Node getNode() {
        return root;
    }
}
