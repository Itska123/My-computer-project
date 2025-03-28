package com.example.myapplication.moodle;

public class Person {
    private String id;       // Unique identifier for the person
    private String fName;    // First name of the person
    private String email;    // Email address of the person
    private String password; // Password for the person
    private String phone;    // Phone number of the person
    private String type;     // Type of person (e.g., "user", "admin")

    // Constructor with all fields
    public Person(String id, String fName, String email, String password, String phone, String type) {
        this.id = id;
        this.fName = fName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.type = type;
    }

    // Constructor without 'type', defaults to "user"
    public Person(String id, String fName, String email, String password, String phone) {
        this.id = id;
        this.fName = fName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.type = "user"; // Default type is "user"
    }

    // Default constructor
    public Person() {
    }

    // Getter and setter for 'id'
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for 'fName'
    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    // Getter and setter for 'email'
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and setter for 'password'
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter for 'phone'
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter and setter for 'type'
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Override toString method to print Person's details in a readable format
    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", fName='" + fName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
