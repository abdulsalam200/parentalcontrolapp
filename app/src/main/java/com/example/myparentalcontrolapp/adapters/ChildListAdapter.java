package com.example.myparentalcontrolapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myparentalcontrolapp.R;
import com.example.myparentalcontrolapp.models.Child;

import java.util.ArrayList;

public class ChildListAdapter extends ArrayAdapter<Child> {

    private int resourceLayout;
    private Context mContext;

    private TextView labelText;

    public ChildListAdapter(Context context, int resource, ArrayList<Child> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Child child = getItem(position);

        Log.i("abd", child.getName()+ " - "+position);

        if (child != null) {
            labelText = (TextView) v.findViewById(R.id.list_item_label);
            labelText.setText(child.getName());
        }

        return v;
    }
}
