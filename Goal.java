package com.fitness;

public class Goal {
    private int id;
    private String type;
    private double target;
    private String unit;
    private boolean completed;

    public Goal(int id, String type, double target, String unit, boolean completed) {
        this.id = id;
        this.type = type;
        this.target = target;
        this.unit = unit;
        this.completed = completed;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public double getTarget() { return target; }
    public String getUnit() { return unit; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
