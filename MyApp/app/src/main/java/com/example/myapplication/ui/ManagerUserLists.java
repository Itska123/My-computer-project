package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.moodle.AdapterAppointment;
import com.example.myapplication.moodle.AdapterUsers;
import com.example.myapplication.moodle.Appointment;
import com.example.myapplication.moodle.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerUserLists extends AppCompatActivity {

    TextView tvTitle; // Title for the user list
    ListView lvMain; // ListView to display the list of users
    DatabaseReference myRef; // Reference to the Firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://textmaster-123-default-rtdb.firebaseio.com/"); // Initialize the Firebase database
    AdapterUsers adapterUserList; // Adapter to handle the user list data
    ArrayList<User> userList; // ArrayList to store the list of users
    String userId; // Store the user ID for navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge layout (for newer Android versions)
        setContentView(R.layout.activity_manager_user_lists); // Set the layout for this activity

        // Adjust padding to avoid system bar overlap using insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the TextView and ListView
        tvTitle = findViewById(R.id.tvUserListsTitle);
        lvMain = findViewById(R.id.lvUsersList);

        // Get the database reference to the "Users" node
        myRef = database.getReference("Users");
        userList = new ArrayList<>(); // Initialize the user list

        // Add a listener to the "Users" reference in the database to fetch the user data
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Loop through all the users and filter out the ones with type "user"
                for (DataSnapshot data : snapshot.getChildren()) {
                    User u = data.getValue(User.class); // Get the user data from the snapshot
                    if (u.getType().equals("user")) {
                        userList.add(u); // Add the user to the list if they are of type "user"
                    }
                }
                // Set up the adapter to display the user list in the ListView
                adapterUserList = new AdapterUsers(ManagerUserLists.this, 0, 0, userList);
                lvMain.setAdapter(adapterUserList); // Attach the adapter to the ListView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read failure (if needed)
            }
        });

        // Set up the click listener for when a user is selected from the list
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // When a user is clicked, navigate to their profile
                Intent intent = new Intent(ManagerUserLists.this, UserProfile.class);
                userId = userList.get(i).getId(); // Get the ID of the clicked user
                intent.putExtra("userID", userId); // Pass the user ID to the next activity
                startActivity(intent); // Start the UserProfile activity
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
            // Navigate to the "ManagerAddTreatment" screen when clicked
            Intent intent = new Intent(this, ManagerAddTreatment.class);
            startActivity(intent);
            return true; // Return true to indicate the action was handled
        } else if (id == R.id.users_list) {
            // Navigate to the "ManagerUserLists" screen when clicked (this is the current screen, so no need to reload)
            Intent intent = new Intent(this, ManagerUserLists.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_calender) {
            // Navigate to the "ManagerCalender" screen when clicked
            Intent intent = new Intent(this, ManagerCalender.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_main) {
            // Navigate to the "ManagerMain" screen when clicked
            Intent intent = new Intent(this, ManagerMain.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.manager_exit) {
            // Exit the manager and go back to the main activity screen when clicked
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true; // Return true if the menu item selection was handled
    }
}
