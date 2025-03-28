package com.example.myapplication.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.moodle.Appointment;
import com.example.myapplication.moodle.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ManagerCalender extends AppCompatActivity {

    private CalendarView calendarView;
    private ArrayList<String> appointmentsList; // Holds available time slots for the day
    private Button btnChangeDate; // Button to open the date picker
    private FirebaseDatabase database;
    private DatabaseReference appointmentsRef, timeSlotsRef, userRef;
    private long selectedDate; // Selected date as a Unix timestamp
    private double startHour = 9; // Default start hour (9 AM)
    private ListView lvManagerCalender; // ListView to display the time slots for the day
    private ArrayList<Appointment> apt; // Holds appointment data
    private ArrayList<User> uapt; // Holds user data
    String uid; // User ID (retrieved from SharedPreferences)
    SharedPreferences sp; // SharedPreferences to get user data
    User user; // Holds the current user data
    Date date; // Holds the selected date for appointments
    int year, month, day; // Year, month, and day for date picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge layout
        setContentView(R.layout.activity_manager_calender);

        // Apply window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the UI components
        lvManagerCalender = findViewById(R.id.lvManagerCalender);
        appointmentsList = new ArrayList<>(); // Initialize appointments list
        btnChangeDate = findViewById(R.id.btnChangeDate); // Date picker button
        sp = getSharedPreferences("details1", 0); // Retrieve SharedPreferences
        uid = sp.getString("uid", ""); // Get user ID
        getUser(); // Fetch user data from Firebase

        // Initialize the appointments list with time slots (9:00 AM to 5:00 PM)
        for (int i = 0; i < 16; i++) {
            appointmentsList.add(9 + 0.5 * i + ": open");
        }

        // Open the date picker when the button is clicked
        btnChangeDate.setOnClickListener(view -> {
            Calendar systemCalendar = Calendar.getInstance();
            year = systemCalendar.get(Calendar.YEAR);
            month = systemCalendar.get(Calendar.MONTH);
            day = systemCalendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(ManagerCalender.this, new SetDate(), year, month, day);
            datePickerDialog.show();
        });

        // Handle time slot clicks (book or cancel appointment)
        lvManagerCalender.setOnItemClickListener((adapterView, view, i, l) -> {
            if (date != null && (date.getYear() > year || date.getMonth() > month && date.getYear() == year || date.getDate() > day && date.getMonth() == month && date.getYear() == year)) {
                // If slot is open, book it
                if (appointmentsList.get(i).contains("open")) {
                    appointmentsList.set(i, "Break");
                    Appointment appointment = new Appointment(user.getId(), true);
                    appointment.setStartHour(9 + i * 0.5);
                    appointment.setEndHour(appointment.getStartHour() + 0.5);
                    appointment.setDate(date);
                    user.addToCart(appointment);
                    userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    userRef.setValue(user); // Update the user's data in Firebase
                    ArrayAdapter arrayAdapter = new ArrayAdapter(ManagerCalender.this, android.R.layout.simple_list_item_1, appointmentsList);
                    lvManagerCalender.setAdapter(arrayAdapter); // Refresh the ListView
                    Toast.makeText(ManagerCalender.this, "התור נשמר בהצלחה", Toast.LENGTH_LONG).show(); // Show success message
                } else if (appointmentsList.get(i).contains("Break")) { // If the slot is a break, cancel the appointment
                    for (int j = 0; j < user.getCart().size(); j++) {
                        if (user.getCart().get(j).getDate().getYear() == date.getYear() && user.getCart().get(j).getDate().getMonth() == date.getMonth() && user.getCart().get(j).getDate().getDate() == date.getDate() && user.getCart().get(j).getStartHour() == (9 + i * 0.5)) {
                            user.getCart().remove(j);
                            userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                            userRef.setValue(user); // Update the user's data in Firebase
                            appointmentsList.set(i, 9 + 0.5 * i + ": open"); // Mark slot as open again
                            ArrayAdapter arrayAdapter = new ArrayAdapter(ManagerCalender.this, android.R.layout.simple_list_item_1, appointmentsList);
                            lvManagerCalender.setAdapter(arrayAdapter); // Refresh the ListView
                        }
                    }
                }
            }
        });
    }

    // Date Picker Listener to set the selected date
    public class SetDate implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date = new Date();
            appointmentsList = new ArrayList<>(); // Reset appointments list
            for (int i = 0; i < 16; i++) {
                appointmentsList.add(9 + 0.5 * i + ": open");
            }
            date.setDate(dayOfMonth);
            date.setMonth(monthOfYear);
            date.setYear(year);
            String str = "You selected: " + dayOfMonth + "/" + monthOfYear + "/" + year;
            Toast.makeText(ManagerCalender.this, str, Toast.LENGTH_LONG).show(); // Show selected date message
            btnChangeDate.setText(str); // Update button text with selected date
            appointmentsRef = FirebaseDatabase.getInstance().getReference("Users");
            int finalMonthOfYear = monthOfYear;

            // Fetch the appointments from Firebase and update the calendar view
            appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    apt = new ArrayList<>();
                    uapt = new ArrayList<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User u = data.getValue(User.class);
                        if (u.getCart() != null) {
                            for (int i = 0; i < u.getCart().size(); i++) {
                                if (u.getCart().get(i).getDate() != null && u.getCart().get(i).getDate().getYear() == year && u.getCart().get(i).getDate().getMonth() == finalMonthOfYear && u.getCart().get(i).getDate().getDate() == dayOfMonth) {
                                    apt.add(u.getCart().get(i));
                                    uapt.add(u);
                                }
                            }
                        }
                    }
                    // Update the time slots based on existing appointments
                    for (int i = 0; i < apt.size(); i++) {
                        int pos = (int) ((apt.get(i).getStartHour() - 9) * 2);
                        int count = (int) ((apt.get(i).getEndHour() - apt.get(i).getStartHour()) * 2 + 0.5);
                        for (int j = 0; j < count; j++) {
                            if (uapt.get(i).getType().equals("manager")) {
                                appointmentsList.set(pos + j, "Break"); // Mark as Break
                            } else
                                appointmentsList.set(pos + j, "U: " + uapt.get(i).getfName() + ", Treatments: " + apt.get(i).findTreatments()); // Show user and treatments
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(ManagerCalender.this, android.R.layout.simple_list_item_1, appointmentsList);
                    lvManagerCalender.setAdapter(arrayAdapter); // Refresh the ListView
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
    }

    // Fetch user data from Firebase
    private void getUser() {
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class); // Set user data
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    // Options menu creation
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.manager_menu, menu); // Inflate the menu
        return true;
    }

    // Menu item selection
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.treatment_list) {
            Intent intent = new Intent(this, ManagerAddTreatment.class);
            startActivity(intent); // Navigate to Add Treatment screen
            return true;
        } else if (id == R.id.users_list) {
            Intent intent = new Intent(this, ManagerUserLists.class);
            startActivity(intent); // Navigate to User List screen
            return true;
        } else if (id == R.id.manager_calender) {
            Intent intent = new Intent(this, ManagerCalender.class);
            startActivity(intent); // Navigate to Manager Calendar screen
            return true;
        } else if (id == R.id.manager_main) {
            Intent intent = new Intent(this, ManagerMain.class);
            startActivity(intent); // Navigate to Manager Main screen
            return true;
        } else if (id == R.id.manager_exit) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent); // Exit to MainActivity
            return true;
        }
        return true;
    }
}
