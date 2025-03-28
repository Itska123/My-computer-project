package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;  // Base class for activities with action bar support

import android.app.Dialog;                  // For displaying dialog boxes
import android.content.Intent;              // For navigating between activities (screens)
import android.content.SharedPreferences;   // For saving and retrieving shared preferences (app settings)
import android.os.Bundle;                   // For saving/restoring activity state
import android.util.Log;                    // For logging information (helpful for debugging)
import android.view.Menu;                   // For creating options menu at the top of the screen
import android.view.MenuItem;               // For handling clicks on menu items
import android.view.View;                   // For handling user actions like clicks
import android.widget.AdapterView;         // For handling clicks on list items
import android.widget.ArrayAdapter;         // For creating a list adapter
import android.widget.Button;               // For creating clickable buttons
import android.widget.EditText;             // For accepting user input (text fields)
import android.widget.ListView;             // For displaying lists of items
import android.widget.RadioButton;          // For creating radio buttons (for selection)
import android.widget.Spinner;              // For creating dropdown menus (spinner)
import android.widget.Toast;                // For showing short messages (toast)

import com.example.myapplication.R;           // Access the resources (like layouts and strings)
import com.example.myapplication.moodle.AdapterAppointment; // Adapter for appointments (not used in code)
import com.example.myapplication.moodle.AdapterTreatment; // Adapter for treatments
import com.example.myapplication.moodle.Treatment; // Treatment class to represent treatment data
import com.google.firebase.database.DataSnapshot;  // Firebase data snapshot (used for retrieving data)
import com.google.firebase.database.DatabaseError;  // Firebase database error
import com.google.firebase.database.DatabaseReference;  // Firebase reference to the database
import com.google.firebase.database.FirebaseDatabase;  // Firebase database instance
import com.google.firebase.database.ValueEventListener;  // Firebase event listener for data changes

import java.util.ArrayList;  // Used for storing lists of objects

public class ManagerAddTreatment extends AppCompatActivity {
    private SharedPreferences sp;  // For accessing shared preferences
    private String name, time, price;  // To store the input data for the treatment

    private Button btnAddTreatment;  // Button for adding a new treatment
    private ArrayList<Treatment> alTreatments;  // List to hold treatments
    private EditText etPriceAdd, etNameAdd, etTime;  // EditText fields for input
    private ListView lvTreatments;  // ListView to display treatments
    private RadioButton rbSalt, rbSweet;  // Radio buttons for selection (not used in code)
    private DatabaseReference myRef, database;  // Firebase references for reading and writing data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_add_treatments);  // Set the layout for this activity

        // Initialize views by finding them by their ID
        etPriceAdd = findViewById(R.id.etPriceAdd);
        etNameAdd = findViewById(R.id.etNameAdd);
        etTime = findViewById(R.id.etTime);
        btnAddTreatment = findViewById(R.id.btnAddTreatment);
        lvTreatments = findViewById(R.id.lvTreatments);

        myRef = FirebaseDatabase.getInstance().getReference("Treatments");  // Firebase reference to Treatments

        sp = getSharedPreferences("details1", 0);  // Get shared preferences (used for storing details)

        alTreatments = new ArrayList<>();  // Initialize the list to store treatments
        retriveData();  // Retrieve existing data (treatments) from Firebase

        // Set a listener on the "Add Treatment" button
        btnAddTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {  // Check if all input fields are filled
                    myRef = FirebaseDatabase.getInstance().getReference("Treatments").push();
                    Treatment t = new Treatment(myRef.getKey(), name, Integer.parseInt(time), Double.parseDouble(price));
                    myRef.setValue(t);  // Save the new treatment to Firebase
                }
            }
        });

        // Set a listener for clicks on treatment items in the list
        lvTreatments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                myRef = FirebaseDatabase.getInstance().getReference("Treatments").child(alTreatments.get(i).getId());
                Log.d("hjghjg", alTreatments.get(i).getId());  // Log the ID of the treatment
                myRef.removeValue();  // Remove the selected treatment from Firebase
            }
        });

        // Set a listener for long clicks on treatment items (no action yet)
        lvTreatments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return true;  // No action for long click yet
            }
        });
    }

    // Method to validate the input fields (check if they are not empty)
    private boolean check() {
        name = etNameAdd.getText().toString();
        time = etTime.getText().toString();
        price = etPriceAdd.getText().toString();

        // Show a toast if any field is empty
        if (name.equals("") || time.equals("") || price.equals("")) {
            Toast.makeText(ManagerAddTreatment.this, "נא למלא את כל הפרטים", Toast.LENGTH_SHORT).show();
            return false;  // Return false to indicate incomplete data
        }
        return true;  // Return true if all fields are filled
    }

    // Method to retrieve the list of treatments from Firebase
    private void retriveData() {
        myRef = FirebaseDatabase.getInstance().getReference("Treatments");  // Reference to the Treatments node in Firebase

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alTreatments = new ArrayList<>();  // Clear the list before adding new data

                // Loop through the data from Firebase and add each treatment to the list
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Treatment p = data.getValue(Treatment.class);  // Convert snapshot to Treatment object
                    alTreatments.add(p);  // Add the treatment to the list
                    Log.d("hjghjg", p.getId());  // Log the treatment ID for debugging
                }

                // Set the adapter for the ListView to display the treatments
                AdapterTreatment adapterTreatment = new AdapterTreatment(ManagerAddTreatment.this, 0, 0, alTreatments);
                lvTreatments.setAdapter(adapterTreatment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors here
            }
        });
    }

    // Method to create the options menu (appears in the toolbar)
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.manager_menu, menu);  // Inflate the menu from the XML resource
        return true;
    }

    // Method to handle clicks on the menu items
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();  // Get the ID of the clicked menu item

        // Navigate to different activities based on the menu item clicked
        if (id == R.id.treatment_list) {
            Intent intent = new Intent(this, ManagerAddTreatment.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.users_list) {
            Intent intent = new Intent(this, ManagerUserLists.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_calender) {
            Intent intent = new Intent(this, ManagerCalender.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_main) {
            Intent intent = new Intent(this, ManagerMain.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_exit) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return true;  // Return true to indicate the event was handled
    }
}
