package com.example.tihomir.myapplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String>values;

    public SimpleArrayAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.category, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.category, parent, true);
        TextView textView = rowView.findViewById(R.id.nameOfTheCategory);
        textView.setText(values.get(position));
        textView = rowView.findViewById(R.id.numberOfBills);
        textView.setText(values.get(position));
        textView = rowView.findViewById(R.id.allTheCost);
        textView.setText(values.get(position));

        return rowView;
    }
}
