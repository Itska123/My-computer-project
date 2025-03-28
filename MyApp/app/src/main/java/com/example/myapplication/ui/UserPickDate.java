package com.example.myapplication.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class UserPickDate extends AppCompatActivity {

    private CalendarView calendarView;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private ArrayList<String> appointmentsList;
    private Button bookButton, btnSelectDate;
    private FirebaseDatabase database;
    private DatabaseReference appointmentsRef, timeSlotsRef, userRef;
    private long selectedDate; // Selected date as a Unix timestamp
    private double startHour = 9; // Default start hour
    private ListView appointmentCalender;
    private ArrayList<Appointment> apt;
    String uid, phoneNo, message = "קבעת תור, תודה";
    SharedPreferences sp;
    User user;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pick_date);
        appointmentCalender = findViewById(R.id.listViewForCalender);
        appointmentsList = new ArrayList<>();
        btnSelectDate = findViewById(R.id.dateButton);
        appointmentCalender = findViewById(R.id.listViewForCalender);
        sp = getSharedPreferences("details1", 0);
        uid = sp.getString("uid", "");
        getUser();

        // Initialize appointmentsList with default open slots
        for (int i = 0; i < 16; i++) {
            appointmentsList.add(9 + 0.5 * i + ": open");
        }

        // Button to open Date Picker dialog for selecting a date
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar systemCalender = Calendar.getInstance();
                int year = systemCalender.get(Calendar.YEAR);
                int month = systemCalender.get(Calendar.MONTH);
                int day = systemCalender.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UserPickDate.this, new SetDate(), year, month, day);
                datePickerDialog.show();
            }
        });

        // Handle item click on the appointment calendar (time slots)
        appointmentCalender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Check if the selected time slot is available
                double sum = user.getCart().get(user.getCart().size() - 1).countTime();
                double startTime = 9 + i / 2.0;
                boolean isopen = true;
                if (i + sum / 30 < appointmentsList.size()) {
                    for (int j = i; j < i + (sum / 30); j++) {
                        if (appointmentsList.get(j).contains("close")) {
                            isopen = false;
                        }
                    }
                } else {
                    isopen = false;
                }

                // Show error message if slot is not available
                if (!isopen) {
                    Toast.makeText(UserPickDate.this, "הודעת שגיאה לא ניתן לקבוע תור בזמן זה", Toast.LENGTH_LONG).show();
                } else {
                    // Save selected date and time for the appointment
                    user.getCart().get(user.getCart().size() - 1).setDate(date);
                    user.getCart().get(user.getCart().size() - 1).setStartHour(startTime);
                    user.getCart().get(user.getCart().size() - 1).setEndHour(startTime + sum / 60);
                    userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    userRef.setValue(user);

                    // Mark the time slots as "close" to prevent double booking
                    for (int j = i; j < i + (sum / 30); j++) {
                        appointmentsList.set(j, "close");
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(UserPickDate.this, android.R.layout.simple_list_item_1, appointmentsList);
                    appointmentCalender.setAdapter(arrayAdapter);
                    Toast.makeText(UserPickDate.this, "התור נשמר בהצלחה", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UserPickDate.this, UserProfile.class);
                    startActivity(intent);
                }
            }
        });
    }

    // Handle Date Picker Dialog for selecting a date
    public class SetDate implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date = new Date();
            appointmentsList = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                appointmentsList.add(9 + 0.5 * i + ": open");
            }
            date.setDate(dayOfMonth);
            date.setMonth(monthOfYear);
            date.setYear(year);
            String str = "You selected :" + dayOfMonth + "/" + monthOfYear + "/" + year;
            Toast.makeText(UserPickDate.this, str, Toast.LENGTH_LONG).show();
            btnSelectDate.setText(str);

            // Fetch existing appointments for the selected date
            appointmentsRef = FirebaseDatabase.getInstance().getReference("Users");
            int finalMonthOfYear = monthOfYear;
            appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    apt = new ArrayList<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User u = data.getValue(User.class);
                        if (u.getCart() != null) {
                            // Check if cart exists and retrieve appointments for the selected day
                            for (int i = 0; i < u.getCart().size(); i++) {
                                if (u.getCart().get(i).getDate() != null &&
                                        u.getCart().get(i).getDate().getYear() == year &&
                                        u.getCart().get(i).getDate().getMonth() == finalMonthOfYear &&
                                        u.getCart().get(i).getDate().getDate() == dayOfMonth) {
                                    apt.add(u.getCart().get(i));
                                }
                            }
                        }
                    }

                    // Mark time slots as "close" based on existing appointments
                    for (int i = 0; i < apt.size(); i++) {
                        int pos = (int) ((apt.get(i).getStartHour() - 9) * 2);
                        int count = (int) ((apt.get(i).getEndHour() - apt.get(i).getStartHour()) * 2 + 0.5);
                        for (int j = 0; j < count; j++) {
                            appointmentsList.set(pos + j, "close");
                        }
                    }

                    // Update the displayed list of available time slots
                    ArrayAdapter arrayAdapter = new ArrayAdapter(UserPickDate.this, android.R.layout.simple_list_item_1, appointmentsList);
                    appointmentCalender.setAdapter(arrayAdapter);

                    // Send SMS notification to the user
                    phoneNo = user.getPhone();
                    sendSMSMessage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("TAG", "Failed to read value.", error.toException());
                }
            });
        }
    }

    private void getUser() {
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        // Read user data from Firebase
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    // Request permission for sending SMS if not granted
    protected void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.order_an_appointment) {
            Intent intent = new Intent(this, UserChooseTreatments.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.profile) {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.user_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
