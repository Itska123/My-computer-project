package com.example.myapplication.moodle;

public class TimeSlot {
    private double startHour; // Start time of the time slot (in hours)
    private double endHour;   // End time of the time slot (in hours)
    private boolean available; // Whether the time slot is available for booking
    private boolean isBreak;   // Indicates if this time slot is for a break

    // Constructor to initialize a TimeSlot with start and end hours, availability, and break status
    public TimeSlot(double startHour, double endHour, boolean available, boolean isBreak) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.available = available;
        this.isBreak = isBreak;
    }

    // Getter and setter for the 'isBreak' field
    public boolean isBreak() {
        return isBreak; // Returns true if it's a break slot
    }

    public void setBreak(boolean isBreak) {
        this.isBreak = isBreak; // Sets the break status
    }

    // Getter and setter for the 'available' field
    public boolean isAvailable() {
        return available; // Returns true if the slot is available
    }

    public void setAvailable(boolean available) {
        this.available = available; // Sets the availability status
    }

    // Getter and setter for the 'endHour' field
    public double getEndHour() {
        return endHour; // Returns the end hour of the time slot
    }

    public void setEndHour(double endHour) {
        this.endHour = endHour; // Sets the end hour for the time slot
    }

    // Getter and setter for the 'startHour' field
    public double getStartHour() {
        return startHour; // Returns the start hour of the time slot
    }

    public void setStartHour(double startHour) {
        this.startHour = startHour; // Sets the start hour for the time slot
    }
}
