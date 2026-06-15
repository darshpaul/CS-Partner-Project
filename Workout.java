package com.fitness;

public class Workout {
    private int id;
    private String date;
    private String type;
    private double amount;
    private int duration;

    public Workout(int id, String date, String type, double amount, int duration) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.duration = duration;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public int getDuration() { return duration; }
}
