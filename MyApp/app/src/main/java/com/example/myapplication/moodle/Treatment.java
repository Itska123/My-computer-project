package com.example.myapplication.moodle;

import java.util.Date;

public class Treatment {
    private String id;     // Unique identifier for the treatment
    private String type;   // Type of treatment (e.g., massage, haircut, etc.)
    private int time;      // Duration of the treatment in minutes
    private double price;  // Price of the treatment

    // Constructor to initialize a Treatment with id, type, time, and price
    public Treatment(String id, String type, int time, double price) {
        this.id = id;
        this.type = type;
        this.time = time;
        this.price = price;
    }

    // Default constructor
    public Treatment() {
    }

    // Getter and setter for the 'id' field
    public String getId() {
        return id; // Returns the ID of the treatment
    }

    public void setId(String id) {
        this.id = id; // Sets the ID of the treatment
    }

    // Getter and setter for the 'type' field
    public String getType() {
        return type; // Returns the type of the treatment
    }

    public void setType(String type) {
        this.type = type; // Sets the type of the treatment
    }

    // Getter and setter for the 'time' field
    public double getTime() {
        return time; // Returns the duration of the treatment in minutes
    }

    public void setTime(int time) {
        this.time = time; // Sets the duration of the treatment
    }

    // Getter and setter for the 'price' field
    public double getPrice() {
        return price; // Returns the price of the treatment
    }

    public void setPrice(double price) {
        this.price = price; // Sets the price of the treatment
    }

    // Overridden toString method to return a string representation of the Treatment object
    @Override
    public String toString() {
        return "Treatment{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", time=" + time + '\'' +
                ", price=" + price +
                '}'; // Returns a string representation of the Treatment object
    }
}
