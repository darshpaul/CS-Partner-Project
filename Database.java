package com.fitness;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {

    private Connection connection;

    public Database(String fileName) {
        try {

            connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
        } catch (SQLException e) {
            System.out.println("Could not open database: " + e.getMessage());
        }
    }

    public void createTables() {
        String goalsTable =
            "CREATE TABLE IF NOT EXISTS goals (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "type TEXT, target REAL, unit TEXT, completed INTEGER)";

        String workoutsTable =
            "CREATE TABLE IF NOT EXISTS workouts (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "date TEXT, type TEXT, amount REAL, duration INTEGER)";

        try (Statement st = connection.createStatement()) {
            st.execute(goalsTable);
            st.execute(workoutsTable);
        } catch (SQLException e) {
            System.out.println("Could not create tables: " + e.getMessage());
        }
    }

    public void addGoal(String type, double target, String unit) {

        String sql = "INSERT INTO goals (type, target, unit, completed) VALUES (?, ?, ?, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setDouble(2, target);
            ps.setString(3, unit);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not add goal: " + e.getMessage());
        }
    }

    public void deleteGoal(int id) {
        String sql = "DELETE FROM goals WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not delete goal: " + e.getMessage());
        }
    }

    public void setGoalCompleted(int id, boolean completed) {
        String sql = "UPDATE goals SET completed = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, completed ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not update goal: " + e.getMessage());
        }
    }

    public ArrayList<Goal> getAllGoals() {
        ArrayList<Goal> goals = new ArrayList<>();
        String sql = "SELECT id, type, target, unit, completed FROM goals";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Goal g = new Goal(
                    rs.getInt("id"),
                    rs.getString("type"),
                    rs.getDouble("target"),
                    rs.getString("unit"),
                    rs.getInt("completed") == 1
                );
                goals.add(g);
            }
        } catch (SQLException e) {
            System.out.println("Could not read goals: " + e.getMessage());
        }
        return goals;
    }

    public void addWorkout(String date, String type, double amount, int duration) {
        String sql = "INSERT INTO workouts (date, type, amount, duration) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setInt(4, duration);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not add workout: " + e.getMessage());
        }
    }

    public ArrayList<Workout> getAllWorkouts() {
        ArrayList<Workout> workouts = new ArrayList<>();
        String sql = "SELECT id, date, type, amount, duration FROM workouts";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Workout w = new Workout(
                    rs.getInt("id"),
                    rs.getString("date"),
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getInt("duration")
                );
                workouts.add(w);
            }
        } catch (SQLException e) {
            System.out.println("Could not read workouts: " + e.getMessage());
        }
        return workouts;
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Could not close database: " + e.getMessage());
        }
    }
}
