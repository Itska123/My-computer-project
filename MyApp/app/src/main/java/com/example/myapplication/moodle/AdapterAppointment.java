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


 //AdapterAppointment: Custom adapter to display a list of appointments in a ListView.

public class AdapterAppointment extends ArrayAdapter<Appointment> {
    Context context; // Context of the calling activity
    List<Appointment> objects; // List of appointments to display


     //Constructor: Initializes the adapter with context and list of appointments.

    public AdapterAppointment(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Appointment> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;  // Set context
        this.objects = objects;  // Set list of appointments
    }


     //getView: Returns a view for each appointment in the list.

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the item layout for each appointment.
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.appointment_list, parent, false);

        // Find TextViews in the layout.
        TextView tvTreatments = view.findViewById(R.id.tvTreatments);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvDuration = view.findViewById(R.id.tvDuration);

        // Get the appointment data for the current position.
        Appointment temp = objects.get(position);

        // Create a string to hold all treatments in the appointment.
        String str = "";
        for(int i = 0; i < temp.getTreatments().size() - 1; i++) {
            str += temp.getTreatments().get(i).getType() + ","; // Add treatment types separated by commas
        }
        str += temp.getTreatments().get(temp.getTreatments().size() - 1).getType(); // Add the last treatment type without a comma

        // Set the data into the TextViews.
        tvTreatments.setText(str);
        tvDate.setText(temp.getDate().getDate() + "/" + (temp.getDate().getMonth() + 1) + "/" + temp.getDate().getYear());
        tvDuration.setText(temp.getStartHour() + "-" + temp.getEndHour());

        // Return the view with the appointment data.
        return view;
    }
}
