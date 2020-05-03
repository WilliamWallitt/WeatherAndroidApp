package com.example.weatherapp.Adaptors;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.Other.Weather;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeatherAdaptor extends RecyclerView.Adapter<WeatherAdaptor.ViewHolder> implements Filterable {
    private ArrayList<Weather> weather;
    itemClicked activity;

    private ArrayList<Weather> weatherFull;

    // interface that we can send to the main activity to tell it what item was clicked
    // we will extend this interface and override it in the main activity
    public interface itemClicked {
        void onItemClicked(int index);
        // when delete button is pressed
        void onDeleteClicked(int index);
    }

    // getting the list of people we want to display
    public WeatherAdaptor(Context context, ArrayList<Weather> list){
        weather = list;

        // need a copy of list
        weatherFull = new ArrayList<>(list);

        // a connection to the activity
        // context refers to the main activity as we pass in this from the main activity
        // when a new Person Adaptor is created
        activity = (itemClicked) context;
    }
    // we need the context so we refer to the right activity
    // THE CONTEXT HOLD A REFERENCE TO THE ACTIVITY THAT IS USING THE ADAPTOR
    // public class inside person adaptor class
    // view holder class represents every item that we are going to place on this view

    public class ViewHolder extends RecyclerView.ViewHolder {

        // getting textViews and imageView that we are going to display
        TextView tvCity, tvCountry;
        Button deleteBtn;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // linking to components
            // we set onClick listener inside of this view holder
            // so here we are connecting our components such as Text/Image views
            // the argument View itemView is the LinearLayout resource we have created
            tvCity = itemView.findViewById(R.id.tvCity);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            deleteBtn = itemView.findViewById(R.id.deleteButton);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // what to do on onclick - send through to the main activity what index was clicked
                    // as for each object we have a tag holding the index of that object
                    // each view now has the index - so v.getTag gives us the index
                    // we convert it to a person object - to make sure it is the correct type of object
                    activity.onItemClicked(weather.indexOf(v.getTag()));
                }
            });

        }
    }

    // as we have created our own view holder its the class.ViewHolder not Recycler view;
    // this connects our list_items resource -> WE RETURN A NEW VIEW HOLDER with this created view v

    @NonNull
    @Override
    public WeatherAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // the view v refers to list items now
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_items, viewGroup, false);
        // when v is passed in, goes to ViewHolder class, and item view is now referring to list items
        return new ViewHolder(v);
    }

    public void onBindViewHolder(@NonNull final WeatherAdaptor.ViewHolder holder, final int index) {

        holder.itemView.setTag(weather.get(index));
        // we can use this setTag as a way to get the index of the object
        holder.tvCity.setText(weather.get(index).getCity());
        holder.tvCountry.setText(weather.get(index).getCountry());
        // if the preference is bus/plane set image resource to bus or plane

        // creating a onClick listener for our delete button
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weather.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index,weather.size());
                activity.onDeleteClicked(index);
            }
        });

    }

    @Override
    public int getItemCount() {
        // count for our array list (how many people objects are currently in the array list
        return weather.size();
    }

    // abstract method as we implemented Filterable
    @Override
    public Filter getFilter() {
        // returns our new list that contains the filtered search terms
        return weatherFilter;
    }

    // this method will just filter based on the user search query is

    private Filter weatherFilter = new Filter() {
        @Override
        // performs in background
        protected FilterResults performFiltering(CharSequence constraint) {
            // return filter results
            ArrayList<Weather> filteredList = new ArrayList<>();

            // if empty or null -> add all items
            if (constraint == null || constraint.length() == 0) {
                // return entire list
                filteredList.addAll(weatherFull);
                
            } else {
                // trim removes any trailing spaces
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Weather item: weatherFull) {
                    // check if our first line contains the filter pattern
                    if (item.getCity().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                    
                }
            }

            // return a filterResult object that contains our search results
            FilterResults results = new FilterResults();
            results.values = filteredList;
            // return results to be passed into publish results
            return results;
            
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // when we have found our results then we update our main ArrayList<Weather>
            weather.clear();
            weather.addAll((ArrayList) results.values);
            notifyDataSetChanged();

        }
    };

}
