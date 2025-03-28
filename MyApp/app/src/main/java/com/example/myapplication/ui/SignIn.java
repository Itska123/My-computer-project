package com.example.myapplication.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    // Declare UI components
    ImageButton imgEntrance;
    EditText etEmail, etPassword;
    Button btnReg;

    private FirebaseAuth mAuth; // Firebase authentication instance
    SharedPreferences sp; // SharedPreferences to store user details locally

    String strMail, strPass;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://textmaster-123-default-rtdb.firebaseio.com/"); // Firebase database reference
    DatabaseReference myRef; // Database reference to interact with Firebase
    Person newUser; // Person object to store the user details fetched from the database
    String Mail, Pass, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in); // Set the layout for the SignIn activity
        EdgeToEdge.enable(this); // Enable edge-to-edge layout for modern device displays

        // Set window insets listener to handle system bar insets (adjust padding for status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        imgEntrance = findViewById(R.id.imgEntrance);
        etEmail = findViewById(R.id.etemailEntrance);
        etPassword = findViewById(R.id.etpasswordEntrance);
        btnReg = findViewById(R.id.btnEntrance);

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase authentication
        sp = getSharedPreferences("details1", 0); // Retrieve SharedPreferences

        // Get previously saved email and password from SharedPreferences (if available)
        strMail = sp.getString("mail", "");
        strPass = sp.getString("pass", "");

        // Pre-fill the email and password fields with the saved values
        etEmail.setText(strMail);
        etPassword.setText(strPass);

        // Set up button click listener
        btnReg.setOnClickListener(this);

        // Set up the image button to navigate back to the MainActivity
        imgEntrance.setOnClickListener(view -> {
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent); // Navigate to MainActivity
        });
    }

    @Override
    public void onClick(View view) {
        // Get email and password from user input
        Mail = etEmail.getText().toString();
        Pass = etPassword.getText().toString();

        // Authenticate the user with Firebase
        mAuth.signInWithEmailAndPassword(Mail, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If sign-in is successful
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid(); // Get the user ID
                            Log.d("TAG", user.getUid());

                            // Reference to the "Users" node in the Firebase database for the current user
                            myRef = database.getReference("Users").child(uid);

                            // Retrieve user data from the Firebase Realtime Database
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again when data updates
                                    newUser = dataSnapshot.getValue(Person.class); // Convert the snapshot into a Person object
                                    Log.d("TAG", "Value is: " + newUser.getfName());

                                    // Save the user data locally in SharedPreferences
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("uid", uid);
                                    editor.putString("mail", Mail);
                                    editor.putString("pass", Pass);
                                    editor.putString("type", newUser.getType()); // Save user type (manager/user)
                                    editor.commit(); // Commit changes

                                    // Navigate to the appropriate activity based on user type
                                    if (newUser.getType().equals("manager")) {
                                        // Navigate to the manager activity
                                        Intent intent = new Intent(SignIn.this, ManagerMain.class);
                                        startActivity(intent);
                                    } else {
                                        // Navigate to the user profile activity
                                        Intent intent = new Intent(SignIn.this, UserProfile.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value from Firebase
                                    Log.w("TAG", "Failed to read value.", error.toException());
                                }
                            });

                        } else {
                            // If sign-in fails, show an error message
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to create the options menu (e.g., login, register, about, home)
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu); // Inflate the menu from XML
        return true;
    }

    // Handle item selections from the options menu
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // Get the selected menu item's ID

        // Check which menu item is selected
        if (id == R.id.login) {
            Intent intent = new Intent(this, SignIn.class); // Navigate to SignIn activity
            startActivity(intent);
            return true;
        } else if (id == R.id.register) {
            Intent intent = new Intent(this, Register.class); // Navigate to Register activity
            startActivity(intent);
            return true;
        } else if (id == R.id.about) {
            Intent intent = new Intent(this, About.class); // Navigate to About activity
            startActivity(intent);
            return true;
        } else if (id == R.id.home) {
            Intent intent = new Intent(this, MainActivity.class); // Navigate to MainActivity
            startActivity(intent);
            return true;
        }
        return true;
    }
}
