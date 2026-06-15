package com.fitness;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GoalsView implements View {

    private Database db;
    private VBox root;
    private ListView<String> list;
    private ArrayList<Goal> goals;

    private TextField typeField;
    private TextField targetField;
    private TextField unitField;
    private Label message;

    public GoalsView(Database db) {
        this.db = db;
        build();
        refresh();
    }

    private void build() {
        root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("My Goals");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        typeField = new TextField();
        typeField.setPromptText("Type (e.g. Running)");
        targetField = new TextField();
        targetField.setPromptText("Target (e.g. 50)");
        unitField = new TextField();
        unitField.setPromptText("Unit (e.g. miles)");

        Button addButton = new Button("Add Goal");
        addButton.setOnAction(e -> addGoal());

        HBox inputRow = new HBox(8, typeField, targetField, unitField, addButton);

        list = new ListView<>();
        list.setPrefHeight(350);

        Button completeButton = new Button("Mark Complete / Undo");
        completeButton.setOnAction(e -> toggleComplete());
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> deleteSelected());
        HBox buttonRow = new HBox(8, completeButton, deleteButton);

        message = new Label("");

        root.getChildren().addAll(title, inputRow, list, buttonRow, message);
    }

    private void addGoal() {
        String type = typeField.getText().trim();
        String unit = unitField.getText().trim();

        double target;
        try {
            target = Double.parseDouble(targetField.getText().trim());
        } catch (NumberFormatException ex) {
            message.setText("Target must be a number.");
            return;
        }

        if (type.isEmpty()) {
            message.setText("Please type a goal type.");
            return;
        }

        db.addGoal(type, target, unit);
        typeField.clear();
        targetField.clear();
        unitField.clear();
        message.setText("Goal added!");
        refresh();
    }

    private void deleteSelected() {
        int index = list.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            message.setText("Pick a goal from the list first.");
            return;
        }
        Goal g = goals.get(index);
        db.deleteGoal(g.getId());
        message.setText("Goal deleted.");
        refresh();
    }

    private void toggleComplete() {
        int index = list.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            message.setText("Pick a goal from the list first.");
            return;
        }
        Goal g = goals.get(index);
        db.setGoalCompleted(g.getId(), !g.isCompleted());
        refresh();
    }

    public void refresh() {
        goals = db.getAllGoals();
        list.getItems().clear();
        for (int i = 0; i < goals.size(); i++) {
            Goal g = goals.get(i);
            String status = g.isCompleted() ? "   [DONE]" : "";
            String text = g.getType() + " - target " + g.getTarget() + " " + g.getUnit() + status;
            list.getItems().add(text);
        }
    }

    public Node getNode() {
        return root;
    }
}
