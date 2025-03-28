package com.example.myapplication.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;           // For navigating between activities (screens)
import android.content.IntentFilter;
import android.os.Bundle;              // For saving/restoring activity state
import android.view.Menu;               // For creating the options menu at the top of the screen
import android.view.MenuItem;           // For handling clicks on menu items
import android.view.View;              // For handling UI actions like button clicks
import android.widget.Button;          // For creating clickable buttons
import android.widget.TextView;        // For displaying text on the screen

import androidx.activity.EdgeToEdge;    // For enabling edge-to-edge display (removes gaps at the edges of the screen)
import androidx.appcompat.app.AppCompatActivity;  // Base class for activities that use modern Android features (like action bar)
import androidx.core.graphics.Insets;   // For handling system bar insets (status bar, navigation bar)
import androidx.core.view.ViewCompat;   // For ensuring compatibility across different Android versions
import androidx.core.view.WindowInsetsCompat;  // For managing window insets (space used by system bars)

import com.example.myapplication.R;     // For accessing resources like layouts and strings

public class MainActivity extends AppCompatActivity {

    TextView tv;        // Declare a TextView variable to display text
    Button btn, btn2, btn3;  // Declare Button variables for user interaction
    TextView tvBattery;
    BroadCastBattery broadCastBattery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);  // Ensure the app fills the entire screen, removing gaps at the edges

        setContentView(R.layout.activity_main);  // Set the layout for the activity (UI defined in XML)

        // Handle window insets (adjusts layout for system bars like the status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);  // Add padding for system bars
            return insets;  // Return the insets to adjust the layout properly
        });

        // Initialize the TextView and Buttons by finding them using their IDs in the layout
        tvBattery = findViewById(R.id.tvBattery);
        broadCastBattery=new BroadCastBattery();
        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        // Set click listeners on the buttons to navigate to different activities
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When btn is clicked, open the Register activity
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When btn2 is clicked, open the SignIn activity
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When btn3 is clicked, open the About activity
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });
    }

    // This method creates the options menu in the app's toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);  // Inflate the menu from the resource file (main_menu.xml)
        return true;  // Return true to show the menu
    }

    // This method handles clicks on the menu items
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();  // Get the ID of the clicked menu item

        // Handle each menu item click
        if (id == R.id.login) {
            // If 'Login' is clicked, open the SignIn screen
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            return true;  // Indicate that the event was handled
        }
        else if (id == R.id.register) {
            // If 'Register' is clicked, open the Register screen
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.about) {
            // If 'About' is clicked, open the About screen
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.home) {
            // If 'Home' is clicked, stay on the current MainActivity screen
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return true;  // Return true to indicate the event was handled
    }
    private class BroadCastBattery extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            int battery = intent.getIntExtra("level",0);
            tvBattery.setText(String.valueOf(battery) + " %");
        }
    }
    @Override

    protected void onResume() {
        super.onResume();
        registerReceiver(broadCastBattery,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadCastBattery);
    }

}
