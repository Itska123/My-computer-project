package com.example.myapplication.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;
import com.example.myapplication.moodle.AdapterAppointment;
import com.example.myapplication.moodle.Appointment;
import com.example.myapplication.moodle.HandleImage;
import com.example.myapplication.moodle.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class UserProfile extends AppCompatActivity {

    SharedPreferences sp;
    private DatabaseReference userRef;
    String uid, userType;
    User user;
    ImageView imgvpic;
    TextView tvProfileName;
    ArrayList<Appointment> future, history;
    AdapterAppointment adapterAppointment;
    ListView lvProfile;
    Button btnChange, btnCamera, btnGallery;
    boolean isFuture = true;
    Intent intent;
    ActivityResultLauncher<String> arlFromGallery;
    ActivityResultLauncher<Intent> arlMakePhoto;
    Uri imageUri;
    private String mCurrentPhotoPath;
    File imageFile;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvProfileName = findViewById(R.id.tvProfileName);
        btnCamera = findViewById(R.id.btncamera);
        imgvpic = findViewById(R.id.imgvforprofile);
        btnGallery = findViewById(R.id.btngallery);
        sp = getSharedPreferences("details1", 0);
        uid = sp.getString("uid", "");
        userType = sp.getString("type", "");
        intent = getIntent();

        if (userType.equals("manager")) {
            uid = intent.getStringExtra("userID");
        }

        lvProfile = findViewById(R.id.lvProfile);
        btnChange = findViewById(R.id.btnChangeHistoryFuture);
        getUserData();

        // Request permissions only if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        // Handle image capture
        arlMakePhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle bundle = result.getData().getExtras();
                            bitmap = (Bitmap) bundle.get("data");
                            imgvpic.setImageBitmap(bitmap);
                            HandleImage.LoadImageFile(bitmap, UserProfile.this, userRef.getKey());
                        }
                    }
                }
        );

        // Handle gallery selection
        arlFromGallery = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            try {
                                final InputStream imageStream = getContentResolver().openInputStream(result);
                                bitmap = BitmapFactory.decodeStream(imageStream);
                                imgvpic.setImageURI(result);
                                HandleImage.LoadImageFile(bitmap, UserProfile.this, userRef.getKey());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(UserProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        );

        btnGallery.setOnClickListener(v -> arlFromGallery.launch("image/*"));

        try {
            imageUri = createUri();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Handle camera capture
        btnCamera.setOnClickListener(v -> {
            Intent intentMakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            arlMakePhoto.launch(intentMakePhoto);
        });

        lvProfile.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if (isFuture) {
                for (int j = 0; j < user.getCart().size(); j++) {
                    if (user.getCart().get(j).getDate().compareTo(future.get(i).getDate()) == 0) {
                        user.getCart().remove(j);
                        future.remove(i);
                        break;
                    }
                }
                userRef.setValue(user);
                adapterAppointment = new AdapterAppointment(UserProfile.this, 0, 0, future);
                lvProfile.setAdapter(adapterAppointment);
            }
            return false;
        });
    }

    private void getUserData() {
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                HandleImage.DownLoadImage(imgvpic, UserProfile.this, user.getId());
                tvProfileName.setText("ברוך הבא " + user.getfName());

                if (user.getCart() != null) {
                    future = new ArrayList<>();
                    history = new ArrayList<>();
                    categorizeAppointments(user.getCart());
                    btnChange.setText("תורים עתידים");

                    adapterAppointment = new AdapterAppointment(UserProfile.this, 0, 0, future);
                    lvProfile.setAdapter(adapterAppointment);

                    btnChange.setOnClickListener(view -> {
                        if (isFuture) {
                            btnChange.setText("היסטוריה");
                            isFuture = !isFuture;
                            adapterAppointment = new AdapterAppointment(UserProfile.this, 0, 0, history);
                            lvProfile.setAdapter(adapterAppointment);
                        } else {
                            btnChange.setText("תורים עתידים");
                            isFuture = !isFuture;
                            adapterAppointment = new AdapterAppointment(UserProfile.this, 0, 0, future);
                            lvProfile.setAdapter(adapterAppointment);
                        }
                    });
                } else {
                    btnChange.setText("אין תורים");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    private void categorizeAppointments(ArrayList<Appointment> cart) {
        for (Appointment appointment : cart) {
            if (appointment.getDate() != null) {
                if (appointment.hasHistory()) {
                    history.add(appointment);
                } else {
                    future.add(appointment);
                }
            }
        }
    }

    private Uri createUri() throws IOException {
        imageFile = createImageFile();
        return FileProvider.getUriForFile(getApplicationContext(),
                "com.example.myapplication.fileProvider", imageFile);
    }

    private File createImageFile() throws IOException {
        Calendar calendar = Calendar.getInstance();
        String timeStamp = String.format("%02d_%02d_%04d_%02d_%02d_%02d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));

        String imageFileName = "HCApp_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.order_an_appointment) {
            startActivity(new Intent(this, UserChooseTreatments.class));
            return true;
        } else if (id == R.id.profile) {
            startActivity(new Intent(this, UserProfile.class));
            return true;
        } else if (id == R.id.user_home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
