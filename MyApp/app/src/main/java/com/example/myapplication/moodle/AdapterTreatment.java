package com.example.myapplication.moodle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

import java.util.List;

// AdapterTreatment: Custom adapter to display a list of treatments in a ListView.
public class AdapterTreatment extends ArrayAdapter<Treatment> {
    Context context; // Context of the calling activity
    List<Treatment> objects; // List of treatments to display

    // Constructor: Initializes the adapter with context and list of treatments.
    public AdapterTreatment(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Treatment> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;  // Set context
        this.objects = objects;  // Set list of treatments
    }

    // getView: Returns a view for each treatment in the list.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the item layout for each treatment.
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.treatment_list, parent, false);

        // Find TextViews in the layout.
        TextView tvType = view.findViewById(R.id.tvType);
        TextView tvPrice = view.findViewById(R.id.trPrice);
        TextView tvTime = view.findViewById(R.id.tvTime);

        // Get the treatment data for the current position.
        Treatment temp = objects.get(position);

        // Set the data into the TextViews.
        tvType.setText("סוג: " + temp.getType()); // Display treatment type
        tvPrice.setText("מחיר: " + temp.getPrice()); // Display treatment price
        tvTime.setText("משך זמן: " + temp.getTime()); // Display treatment duration

        // Return the view with the treatment data.
        return view;
    }
}
