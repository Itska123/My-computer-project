package com.example.myapplication.ui;

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
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.moodle.AdapterTreatment;
import com.example.myapplication.moodle.Appointment;
import com.example.myapplication.moodle.Treatment;
import com.example.myapplication.moodle.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserChooseTreatments extends AppCompatActivity {

    // Declare UI components
    private ListView listView;
    private ListView listView2;
    private Button savebtn;
    private ArrayAdapter<Treatment> leftAdapter;
    private ArrayAdapter<Treatment> rightAdapter;
    private ArrayList<Treatment> leftListItems;
    private DatabaseReference database, userRef;
    private ArrayList<Treatment> rightListItems;
    private AdapterTreatment adapterTreatment;
    String uid;
    SharedPreferences sp;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choose_treatments);
        database = FirebaseDatabase.getInstance().getReference("Treatments"); // Reference to treatments in Firebase

        // Initialize ListViews
        listView = findViewById(R.id.listView); // Left ListView for available treatments
        listView2 = findViewById(R.id.listView2); // Right ListView for selected treatments
        savebtn = findViewById(R.id.savetreatmentsbtn); // Button to save selected treatments

        sp = getSharedPreferences("details1", 0); // Get shared preferences to retrieve the current user's UID
        uid = sp.getString("uid", ""); // Get user ID from shared preferences
        getUser(); // Get the user's data from Firebase

        // Initialize lists for treatments
        leftListItems = new ArrayList<>(); // List for available treatments
        rightListItems = new ArrayList<>(); // List for selected treatments

        // Retrieve available treatments from Firebase
        retriveData();

        // Set adapters for ListViews
        leftAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leftListItems);
        rightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rightListItems);

        listView.setAdapter(leftAdapter); // Set adapter for the left ListView
        listView2.setAdapter(rightAdapter); // Set adapter for the right ListView

        // Set item click listener for the left ListView (to add treatment to the right ListView)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rightListItems.add(leftListItems.get(position)); // Add selected treatment to the right list
                adapterTreatment = new AdapterTreatment(UserChooseTreatments.this, 0, 0, rightListItems); // Update adapter
                listView2.setAdapter(adapterTreatment); // Update right ListView
            }
        });

        // Set item click listener for the right ListView (to remove treatment from the right ListView)
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rightListItems.remove(position); // Remove selected treatment from the right list
                adapterTreatment = new AdapterTreatment(UserChooseTreatments.this, 0, 0, rightListItems); // Update adapter
                listView2.setAdapter(adapterTreatment); // Update right ListView
            }
        });

        // Set click listener for the "savebtn" button
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightListItems.size() > 0) {
                    // If there are treatments selected, create an Appointment object
                    Appointment apt = new Appointment(user.getId(), rightListItems); // Create appointment with selected treatments
                    user.addToCart(apt); // Add appointment to the user's cart
                    userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid); // Reference to user's Firebase data
                    userRef.setValue(user); // Update the user data in Firebase
                    Intent intent = new Intent(UserChooseTreatments.this, UserPickDate.class); // Navigate to pick date activity
                    startActivity(intent);
                }
            }
        });
    }

    // Retrieve treatments from Firebase and update the left ListView
    private void retriveData() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leftListItems = new ArrayList<>(); // Clear current list
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Treatment p = data.getValue(Treatment.class); // Get treatment data
                    leftListItems.add(p); // Add treatment to the left list
                }
                adapterTreatment = new AdapterTreatment(UserChooseTreatments.this, 0, 0, leftListItems); // Update adapter
                listView.setAdapter(adapterTreatment); // Update left ListView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    // Retrieve user data from Firebase
    private void getUser() {
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid); // Reference to current user's data
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // When user data is fetched, save it in the user object
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database error
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    // Create options menu for the activity
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_menu, menu); // Inflate user menu
        return true;
    }

    // Handle options menu item clicks
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // Get the ID of the clicked menu item

        // Check which item was clicked and navigate accordingly
        if (id == R.id.order_an_appointment) {
            Intent intent = new Intent(this, UserChooseTreatments.class); // Navigate to current activity
            startActivity(intent);
            return true;
        } else if (id == R.id.profile) {
            Intent intent = new Intent(this, UserProfile.class); // Navigate to profile activity
            startActivity(intent);
            return true;
        } else if (id == R.id.user_home) {
            Intent intent = new Intent(this, MainActivity.class); // Navigate to home activity
            startActivity(intent);
            return true;
        }
        return true;
    }
}
