package com.example.myapplication.ui;

import android.content.Intent;           // For navigating between activities
import android.os.Bundle;              // For saving/restoring activity state
import android.view.Menu;               // For creating options menu
import android.view.MenuItem;           // For handling menu item clicks
import android.view.View;              // For handling UI components like buttons
import android.widget.ImageButton;      // For creating a clickable image button

import androidx.activity.EdgeToEdge;    // For enabling edge-to-edge display (removes gaps at the edges of the screen)
import androidx.appcompat.app.AppCompatActivity;  // Base class for activities that use modern Android features (like action bar)
import androidx.core.graphics.Insets;   // For handling system bar insets (status bar, navigation bar)
import androidx.core.view.ViewCompat;   // For view compatibility across different Android versions
import androidx.core.view.WindowInsetsCompat;  // For managing window insets (e.g., system bars)

import com.example.myapplication.R;     // Access the resources (like layouts and strings)

public class About extends AppCompatActivity {
    ImageButton btn;                    // Declare the ImageButton variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);        // Enable edge-to-edge display (content fills the screen without gaps)

        setContentView(R.layout.activity_about);  // Set the layout for this activity (UI defined in XML)

        // Handle window insets (adjusts layout for system bars like status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Apply padding based on system bars
            return insets;  // Return the insets
        });

        // Initialize the ImageButton (find it by its ID in the layout)
        btn = findViewById(R.id.imgAbout);

        // Set an OnClickListener for the button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to open MainActivity when the button is clicked
                Intent intent = new Intent(About.this, MainActivity.class);
                startActivity(intent);  // Start MainActivity
            }
        });
    }

    // Create the options menu (this will be shown in the app's toolbar)
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);  // Inflate the menu from the resource file (main_menu.xml)
        return true;    // Return true to show the menu
    }

    // Handle menu item clicks
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();  // Get the ID of the selected menu item

        // Handle each menu item by checking its ID
        if (id == R.id.login) {
            // Open SignIn activity when 'Login' is clicked
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            return true;  // Indicate that the event was handled
        }
        else if (id == R.id.register) {
            // Open Register activity when 'Register' is clicked
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.about) {
            // Open About activity (which is the current activity) when 'About' is clicked
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.home) {
            // Open MainActivity when 'Home' is clicked
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return true;    // Return true to indicate the event was handled
    }
}
