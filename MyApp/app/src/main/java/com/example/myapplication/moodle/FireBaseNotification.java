package com.example.myapplication.moodle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.ui.ManagerMain;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FireBaseNotification extends Service {
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://textmaster-123-default-rtdb.firebaseio.com/"); // Initialize Firebase database reference
    DatabaseReference myRef = database.getReference(); // Reference to the root of the Firebase database
    Notification notification; // Notification object to be displayed to the user
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1; // Request permission for notification

    NotificationCompat.Builder builder; // Builder to create a notification (title, content, icon)
    NotificationManagerCompat notificationManager; // Manages the display and control of notifications
    Query q; // Firebase database query object for listening to data changes
    private ManagerMain mainActivity; // Reference to the main activity (although not used in this code)

    @Override
    public void onCreate() {
        super.onCreate();
        // Check if the device's Android version supports Notification Channels (Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID"; // Notification channel ID
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title", // Channel name
                    NotificationManager.IMPORTANCE_DEFAULT); // Default notification importance
            // Create the notification channel where the notification will be shown
            notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
            // Build the notification with title, content, and icon
            builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Update!") // Notification title
                    .setContentText("Users was updated") // Notification content
                    .setSmallIcon(R.drawable.logo); // Icon to show in the notification
            builder.setChannelId(channelId); // Set the notification channel
            builder.setDefaults(NotificationCompat.DEFAULT_ALL); // Set default notification sounds, vibrations, etc.
            notificationManager = NotificationManagerCompat.from(this); // Initialize notification manager
        }
    }

    // This method is called when the service starts
    public int onStartCommand(Intent intent, int flags, int startId) {
        q = myRef.child("Users").orderByValue(); // Query to listen for changes in the "Users" node in the database
        // Add a listener to detect any changes in the Firebase database
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // When data changes, start showing the notification in the foreground
                startForeground(1, builder.build()); // Start the service in the foreground and show notification
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors in case the Firebase data fetching fails
            }
        });
        return START_NOT_STICKY; // Service is not sticky, so it stops if not explicitly restarted
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This service is not bound to any component, so it returns an UnsupportedOperationException
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
