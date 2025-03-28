package com.example.myapplication.moodle;

import java.util.ArrayList;

public class User extends Person {
    private ArrayList<Appointment> history; // List to store the user's past appointments
    private ArrayList<Appointment> Cart;    // List to store appointments added to the user's cart

    // Constructor to initialize a User with id, name, email, password, phone, and type
    public User(String id, String fName, String email, String password, String phone, String type) {
        super(id, fName, email, password, phone, type); // Call the parent class constructor (Person)
    }

    // Default constructor for creating a User without initial values
    public User() {
    }

    // Method to check if the user can edit a specific appointment
    public boolean canEditAppointment(String appointmentId) {
        // Check if the user's history contains an appointment with the given ID
        return this.getHistory().stream().anyMatch(a -> a.getId().equals(appointmentId));
    }

    // Getter for the user's appointment history
    public ArrayList<Appointment> getHistory() {
        return history; // Returns the list of past appointments
    }

    // Method to add an appointment from the cart to the history
    public void AddToHistory(int pos) {
        if (this.history == null)
            this.history = new ArrayList<>(); // Initialize the history list if it's null
        this.history.add(this.Cart.get(pos)); // Add the selected appointment from the cart to history
        this.Cart.remove(pos); // Remove the appointment from the cart after adding it to history
    }

    // Setter for the user's appointment history
    public void setHistory(ArrayList<Appointment> history) {
        this.history = history; // Sets the history list
    }

    // Getter for the user's cart of appointments
    public ArrayList<Appointment> getCart() {
        return Cart; // Returns the list of appointments in the cart
    }

    // Setter for the user's cart
    public void setCart(ArrayList<Appointment> cart) {
        Cart = cart; // Sets the cart list
    }

    // Method to add an appointment to the user's cart
    public void addToCart(Appointment appointment) {
        if (this.Cart == null)
            this.Cart = new ArrayList<>(); // Initialize the cart if it's null
        this.Cart.add(appointment); // Add the given appointment to the cart
    }

    // Overridden toString method to return a string representation of the User object
    @Override
    public String toString() {
        return "User{" +
                "history=" + history +  // Display user's history
                ", Cart=" + Cart +      // Display user's cart
                '}';
    }
}
