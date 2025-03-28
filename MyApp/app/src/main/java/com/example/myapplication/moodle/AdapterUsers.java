package com.example.myapplication.moodle;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

import java.util.List;

// AdapterUsers: Custom adapter to display a list of users in a ListView.
public class AdapterUsers extends ArrayAdapter<User> {
    Context context; // Context of the calling activity
    List<User> objects; // List of users to display

    // Constructor: Initializes the adapter with context and list of users.
    public AdapterUsers(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<User> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;  // Set context
        this.objects = objects;  // Set list of users
    }

    // getView: Returns a view for each user in the list.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the item layout for each user.
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.userslist, parent, false);

        // Find TextViews in the layout.
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvUserPhone = view.findViewById(R.id.tvUserPhone);
        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);

        // Get the user data for the current position.
        User temp = objects.get(position);

        // Set the user data into the TextViews.
        tvUserName.setText(temp.getfName()); // Display user name
        tvUserPhone.setText(temp.getPhone()); // Display user phone number
        tvUserEmail.setText(temp.getEmail()); // Display user email address

        // Return the view with the user data.
        return view;
    }
}
