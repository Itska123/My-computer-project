package com.example.myapplication.moodle;

import android.widget.CalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Appointment {
    private String id; // Appointment ID
    private String userId; // User ID associated with the appointment
    private ArrayList<Treatment> treatments; // List of treatments for the appointment
    private Date date; // Date of the appointment
    private double startHour; // Start hour of the appointment
    private double endHour; // End hour of the appointment
    private boolean isBreak; // Flag to determine if this is a break or not

    // Default constructor for Firebase
    public Appointment() {
    }

    // Constructor to initialize all fields
    public Appointment(String id, String userId, ArrayList<Treatment> treatments, Date date, double startHour, double endHour, boolean isBreak) {
        this.id = id;
        this.userId = userId;
        this.treatments = treatments;
        this.date = date;
        this.startHour = startHour;
        this.endHour = endHour;
        this.isBreak = isBreak;
    }

    // Constructor with userId and treatments, default isBreak is false
    public Appointment(String userId, ArrayList<Treatment> treatments){
        this.userId = userId;
        this.treatments = treatments;
        this.isBreak = false;
    }

    // Constructor with userId and isBreak flag
    public Appointment(String userId, boolean isBreak){
        this.userId = userId;
        this.isBreak = isBreak();
    }

    // Checks if a time slot is available
    public boolean isSlotAvailable(ArrayList<TimeSlot> timeSlots, int startHour, int duration) {
        int endHour = startHour + duration; // Calculate the end hour based on the duration
        for (TimeSlot slot : timeSlots) {
            // Check if the slot is within the specified time range and if it's available
            if (slot.getStartHour() >= startHour && slot.getEndHour() <= endHour && !slot.isAvailable()) {
                return false; // Slot is not available
            }
        }
        return true; // Slot is available
    }

    // Getter and setter methods for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(ArrayList<Treatment> treatments) {
        this.treatments = treatments;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getStartHour() {
        return startHour;
    }

    public void setStartHour(double startHour) {
        this.startHour = startHour;
    }

    public double getEndHour() {
        return endHour;
    }

    public void setEndHour(double endHour) {
        this.endHour = endHour;
    }

    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }

    // Calculate the total time of all treatments
    public int countTime() {
        int sum = 0;
        for (Treatment treatment : treatments) {
            sum += treatment.getTime(); // Add the time for each treatment
        }
        return sum;
    }

    // Calculate the total price of all treatments
    public int countPrice() {
        int sum = 0;
        for (Treatment treatment : treatments) {
            sum += treatment.getPrice(); // Add the price for each treatment
        }
        return sum;
    }

    // Find the types of treatments and return them as a comma-separated string
    public String findTreatments(){
        String str = "";
        if (this.treatments != null){
            for (int i = 0; i < this.treatments.size(); i++){
                if ( i == this.treatments.size() - 1) {
                    str += this.treatments.get(i).getType(); // Last treatment type without a comma
                }
                else
                    str += this.treatments.get(i).getType() + ", "; // Add comma between treatments
            }
        }
        return str; // Return the concatenated string of treatment types
    }

    // Checks if the appointment is from the past
    public boolean hasHistory(){
        Calendar systemCalender = Calendar.getInstance(); // Get the current system date
        int year = systemCalender.get(Calendar.YEAR); // Get the current year
        int month = systemCalender.get(Calendar.MONTH); // Get the current month
        int day = systemCalender.get(Calendar.DAY_OF_MONTH); // Get the current day

        // Compare the appointment date with the current date to check if it's in the past
        if (year > this.date.getYear()){
            return true; // Appointment is in the past
        }
        if ((year == this.date.getYear()) && month > this.date.getMonth()){
            return true; // Appointment is in the past
        }
        if ((year == this.date.getYear()) && month == this.date.getMonth() && day > this.date.getDate()){
            return true; // Appointment is in the past
        }
        return false; // Appointment is not in the past
    }
}
