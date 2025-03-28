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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.moodle.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity implements View.OnClickListener {

    // Declare UI components
    EditText etName, etEmail, etPassword, etPhone;
    String Name, Email, Phone, Pass, id;
    Spinner spinner;
    Button btnReg;
    List<String> HairStyle;
    ImageButton btn2;

    // Initialize Firebase and SharedPreferences
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://textmaster-123-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;
    SharedPreferences sp;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge layout
        setContentView(R.layout.activity_register); // Set the layout for the Register activity

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Authentication instance

        // Bind UI elements to variables
        etName = findViewById(R.id.etname);
        etEmail = findViewById(R.id.etemail);
        etPassword = findViewById(R.id.etpassword);
        etPhone = findViewById(R.id.etphone);
        btnReg = findViewById(R.id.btn);

        sp = getSharedPreferences("details1", 0); // Initialize SharedPreferences

        // Set up button click listener
        btnReg.setOnClickListener(this);

        // Handle system bar insets (adjust padding for status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize spinner (dropdown) with hair styles
        spinner = findViewById(R.id.spinner);
        HairStyle = new ArrayList<>();
        btn2 = findViewById(R.id.imgRegister);

        // Add hair styles to list
        HairStyle.add("שיער באורך ארוך"); // Long hair
        HairStyle.add("שיער באורך בינוני"); // Medium hair
        HairStyle.add("שיער באורך קצר"); // Short hair

        // Set up the adapter for the spinner (dropdown)
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, HairStyle);
        spinner.setAdapter(dataAdapter);

        // Set up listener for spinner item selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = HairStyle.get(i); // Get selected hair style
                Toast.makeText(Register.this, "Selected: " + item, Toast.LENGTH_LONG).show(); // Show selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing if no item is selected
            }
        });

        // Set up image button click listener (to navigate to MainActivity)
        btn2.setOnClickListener(view -> {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent); // Navigate to MainActivity
        });
    }

    @Override
    public void onClick(View view) {
        // Get user input from the EditText fields
        Name = etName.getText().toString();
        Email = etEmail.getText().toString();
        Pass = etPassword.getText().toString();
        Phone = etPhone.getText().toString();

        myRef = database.getReference("Users"); // Reference to the "Users" node in Firebase

        // Validate input fields and show appropriate error message
        if (Name.equals("")) {
            Toast.makeText(Register.this, "לא מילאת שם מלא", Toast.LENGTH_LONG).show(); // Missing name
        } else if (Email.equals("")) {
            Toast.makeText(Register.this, "לא מילאת דואר אלקטרוני", Toast.LENGTH_LONG).show(); // Missing email
        } else if (Pass.equals("")) {
            Toast.makeText(Register.this, "לא מילאת סיסמה", Toast.LENGTH_LONG).show(); // Missing password
        } else if (Phone.equals("")) {
            Toast.makeText(Register.this, "לא מילאת טלפון", Toast.LENGTH_LONG).show(); // Missing phone
        } else {
            Log.d("else", Email + " " + Pass); // Log the email and password for debugging purposes

            // Register user using Firebase Authentication
            mAuth.createUserWithEmailAndPassword(Email, Pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // If registration is successful
                                FirebaseUser user = mAuth.getCurrentUser(); // Get the current user
                                Person newUser = new Person(user.getUid(), Name, Email, Pass, Phone); // Create a new user object

                                // Save user data to Firebase Realtime Database under the "Users" node
                                myRef = database.getReference("Users").child(user.getUid());
                                myRef.setValue(newUser);

                                // Save user details to SharedPreferences for local storage
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("uid", user.getUid());
                                editor.putString("name", Name);
                                editor.putString("mail", Email);
                                editor.putString("pass", Pass);
                                editor.putString("type", "user");
                                editor.commit(); // Commit the changes to SharedPreferences

                                // Navigate to UserProfile activity after successful registration
                                Intent intent = new Intent(Register.this, UserProfile.class);
                                startActivity(intent);
                            } else {
                                // If registration fails, show an error message
                                Toast.makeText(Register.this, "אימייל או סיסמה לא תקינים", Toast.LENGTH_LONG).show(); // Invalid email or password
                            }
                        }
                    });
        }
    }

    // Method to create the options menu for the activity (e.g., login, register, about)
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu); // Inflate menu from XML
        return true;
    }

    // Handle item selections in the options menu
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // Get the ID of the selected menu item

        if (id == R.id.login) {
            // Navigate to the SignIn activity when login option is clicked
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.register) {
            // Navigate to the Register activity (this activity)
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.about) {
            // Navigate to the About activity when clicked
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.home) {
            // Navigate to the MainActivity when clicked
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true; // Return true if the menu item selection is handled
    }
}
