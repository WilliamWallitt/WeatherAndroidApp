package com.example.weatherapp.Fragments;


import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.weatherapp.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ListFrag extends ListFragment {

    public ListFrag(){}

    public interface ItemSelected {
        // return the index so we can find the other information
        void onItemSelected(int index);
    }

    ItemSelected activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = (ItemSelected) context;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // our array called data will hold the current month and day
        // the first two days will be called today and tomorrow
        // the last 3 days will be the month followed by the day of the month

        ArrayList<String> data = new ArrayList<>();
        Calendar cal = Calendar.getInstance();


        String[] months = {"January", "February", "March","April",
                "May","June","July","August","September","October","November","December"};

        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                data.add("Today");
            } else if (i == 1) {
                data.add("Tomorrow");
            } else {
                data.add(months[month - 1] + " " + (dayOfMonth + i));
            }
        }


        // set our data array as an array adaptor
        setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.fragment_list, data));


    }


    // interface method override -> passing through the index of the list item that was clicked
    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {

        activity.onItemSelected(position);


    }
}
