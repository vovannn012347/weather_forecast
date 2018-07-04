package com.vovik.weatherforcast;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vovik on 25.06.2018.
 */

public class AutocompleteAdapterMapPlace extends ArrayAdapter implements Filterable {
    private ArrayList<Address> places;
    private Context context;

    AutocompleteAdapterMapPlace(Context context, int resource) {
        super(context, resource);
        places = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Address getItem(int position) {
        return places.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null){
                    try{
                        //get data from the web
                        String term = constraint.toString();
                        places = new DownloadPlaces().execute(term).get();
                    }catch (Exception e){
                        Log.d("HUS","EXCEPTION "+e);
                    }
                    filterResults.values = places;
                    filterResults.count = places.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0){
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView =  LayoutInflater.from(this.context).inflate(R.layout.location_select_view,parent,false);
        }

        //get place
        Address place = places.get(position);
        String placeText = "";
        for(int i = 0; i <= place.getMaxAddressLineIndex(); ++i){
            placeText += place.getAddressLine(i) + " ";
        }

        TextView placeName = convertView.findViewById(R.id.location_view_item_name);

        placeName.setText(placeText);

        return convertView;
    }

    //download places list
    //todo:redo this into static to avoid shit happening
    private class DownloadPlaces extends AsyncTask<String, Integer, ArrayList<Address>> {

        @Override
        protected ArrayList<Address> doInBackground(String... params) {
            try {

                Geocoder coder = new Geocoder(getContext(), Locale.getDefault());

                List<Address> loc = coder.getFromLocationName(params[0], 3);

                ArrayList<Address> result = new ArrayList<>(loc.size());
                result.addAll(loc);

                return result;


            } catch (Exception e) {
                Log.d("HUS", "EXCEPTION " + e);
                return null;
            }
        }
    }
}


