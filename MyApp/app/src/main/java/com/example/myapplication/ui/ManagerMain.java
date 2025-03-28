package com.example.myapplication.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.moodle.FireBaseNotification;

public class ManagerMain extends AppCompatActivity {

    Button btnTreatmentList, btnUsersList, btnCalender; // Declare buttons for navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manager); // Set the content view for this activity

        // Initialize buttons from the layout
        btnTreatmentList = findViewById(R.id.btnTreatmentList);

        // Check for notification permissions and request them if not granted
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to post notifications
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return; // Exit the method if permission is not granted
        }

        // Start a service to handle Firebase notifications
        Intent intent = new Intent(ManagerMain.this, FireBaseNotification.class);
        if (Build.VERSION.SDK_INT >= 26) {
            // Start the service in the foreground for devices with Android Oreo or later
            ManagerMain.this.startForegroundService(intent);
        } else {
            // For pre-Oreo devices, start the service normally
            ManagerMain.this.startService(intent);
        }

        // Set up click listener for the "Treatment List" button
        btnTreatmentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the "ManagerAddTreatment" screen when clicked
                Intent intent = new Intent(ManagerMain.this, ManagerAddTreatment.class);
                startActivity(intent);
            }
        });

        // Initialize and set up the "Users List" button
        btnUsersList = findViewById(R.id.btnUsersList);
        btnUsersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the "ManagerUserLists" screen when clicked
                Intent intent = new Intent(ManagerMain.this, ManagerUserLists.class);
                startActivity(intent);
            }
        });

        // Initialize and set up the "Calendar" button
        btnCalender = findViewById(R.id.btnCalender);
        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the "ManagerCalender" screen when clicked
                Intent intent = new Intent(ManagerMain.this, ManagerCalender.class);
                startActivity(intent);
            }
        });
    }

    // Method to create the options menu for the activity
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu with the items defined in the XML
        getMenuInflater().inflate(R.menu.manager_menu, menu);
        return true; // Return true to display the menu
    }

    // Method to handle item selection in the options menu
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // Get the ID of the clicked menu item

        if (id == R.id.treatment_list) {
            // Navigate to the "ManagerAddTreatment" screen
            Intent intent = new Intent(this, ManagerAddTreatment.class);
            startActivity(intent);
            return true; // Return true to indicate the action was handled
        } else if (id == R.id.users_list) {
            // Navigate to the "ManagerUserLists" screen
            Intent intent = new Intent(this, ManagerUserLists.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_calender) {
            // Navigate to the "ManagerCalender" screen
            Intent intent = new Intent(this, ManagerCalender.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_main) {
            // Reload the "ManagerMain" screen
            Intent intent = new Intent(this, ManagerMain.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_exit) {
            // Exit the manager and go back to the main activity screen
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true; // Return true if the menu item selection was handled
    }
}
