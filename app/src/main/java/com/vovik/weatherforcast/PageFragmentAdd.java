package com.vovik.weatherforcast;

import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

/**
 * Created by vovik on 06.07.2018.
 */

public class PageFragmentAdd extends Fragment {// implements OnMapReadyCallback {

    AutoCompleteTextView placeAddressView;
//        MapView mv;
//        GoogleMap map;
    Address selectedAddress = null;
    Button addButton;

    public static PageFragmentAdd instance(){
        return new PageFragmentAdd();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_city_add, container, false);

//            mv = rootView.findViewById(R.id.mapView);
//            mv.onCreate(savedInstanceState);
//            mv.getMapAsync(this);

        placeAddressView = rootView.findViewById(R.id.autoCompleteTextViewPlace);

        if(savedInstanceState != null){
            placeAddressView.setText(savedInstanceState.getCharSequence("PlaceString"),false);
        }

        this.addButton = rootView.findViewById(R.id.AddButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedAddress!= null){
                    addPage(selectedAddress);
                }
            }
        });


        initializeAutocomplete();

        return rootView;
    }

    void initializeAutocomplete(){
        final AutocompleteAdapterMapPlace adapter = new AutocompleteAdapterMapPlace(getContext(), android.R.layout.simple_list_item_1);
        placeAddressView.setAdapter(adapter);
        placeAddressView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

                Address place = adapter.getItem(pos);

                StringBuilder builder = new StringBuilder();


                for(int i = 0; i <= place.getMaxAddressLineIndex(); ++i){
                    builder.append(place.getAddressLine(i));
                }
                placeAddressView.setText(builder.toString(), false);

                /*
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatitude(), place.getLongitude()), 10);
                map.animateCamera(cameraUpdate);
                */

                selectedAddress = place;
            }
        });
    }

    public Address getSelectedAddress(){
        return selectedAddress;
    }

    public Button getAddButton(){
        return addButton;
    }

    public void addPage(Address a){

    }



//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//            map = googleMap;
//            map.getUiSettings().setMyLocationButtonEnabled(false);
//
//            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED
//                    )
//            map.setMyLocationEnabled(true);
//
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
//            map.animateCamera(cameraUpdate);
//
//        }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//            mv.onSaveInstanceState(savedInstanceState);
        if(placeAddressView!=null)
        savedInstanceState.putCharSequence("PlaceString", placeAddressView.getText());
    }

    @Override
    public void onResume() {
//            mv.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
//            mv.onLowMemory();
    }
}

