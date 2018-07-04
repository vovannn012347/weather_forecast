package com.vovik.weatherforcast;

import android.content.res.AssetManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends AppCompatActivity {

    private static SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    static AssetManager mgr = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if(BaseActivity.mgr == null) BaseActivity.mgr = getAssets();

    }

    class PlaceWeather{

        long shift;
        double lattitude, longitude;

        String place, timezone;

        WeatherData currentData;

        List<WeatherData>
                hourlyData,
                dailyData;

        List<MinuteWeatherData>
            minutelyData;

        static final int
            REQUIRE_MINUTELY = 1,
            REQUIRE_HOURLY = 2,
            //REQUIRE_DAILY = 3,
            //UPDATE_ERROR = -1,
            UPDATE_SUCCESS = 0;
            //UPDATE_REQUIRED = -2;

        int
            currentMinute,
            currentHour;
            //currentDay;

        PlaceWeather(){
            currentMinute = 0;
            currentHour = 0;
            //currentDay = 0;
        }

        void Update(long time){

            boolean
                minutelyUpdate = false,
                hourlyUpdate = false;

            if(dailyData!= null && dailyData.size() > 0){
                WeatherData currentDay = dailyData.get(0);

                currentData.sunsetTime = currentDay.sunsetTime;
                currentData.sunriseTime = currentDay.sunriseTime;
                currentData.temperatureHigh = currentDay.temperatureHigh;
                currentData.temperatureLow = currentDay.temperatureLow;
            }

            if(minutelyData != null){
                for(; currentMinute < minutelyData.size() &&
                        minutelyData.get(currentMinute).time - time - shift > 0;
                    ++currentMinute){minutelyUpdate = true;}

                if(minutelyUpdate && currentMinute < minutelyData.size()){
                        MinuteWeatherData minute = minutelyData.get(currentMinute);

                        currentData.icon = minute.icon;
                        currentData.precipIntensity = minute.precipIntensity;
                        currentData.time = minute.time;
                }
            }


            if(minutelyData == null || (minutelyData != null && currentMinute >= minutelyData.size()))
                if(hourlyData != null) {
                    for(; currentHour < hourlyData.size() &&
                            hourlyData.get(currentHour).time - time - shift > 0;
                        ++currentHour){hourlyUpdate = true;}

                    if(hourlyUpdate && currentHour < hourlyData.size()){
                        WeatherData hour = hourlyData.get(currentHour);

                        currentData.icon = hour.icon;
                        currentData.precipIntensity = hour.precipIntensity;
                        currentData.time = hour.time;
                        currentData.humidity = hour.humidity;
                        currentData.windSpeed = hour.windSpeed;
                        currentData.temperature = hour.temperature;
                    }
                }

            if(hourlyUpdate && currentHour >= hourlyData.size()){
                currentData.sunriseTime = dailyData.get(0).sunriseTime;
                currentData.sunsetTime = dailyData.get(0).sunsetTime;
            }
        }

        int RequireUpload(){

            if(minutelyData != null && currentMinute >= minutelyData.size())
                return REQUIRE_MINUTELY;

            if(hourlyData != null && (currentHour >= hourlyData.size() || currentHour >= 24))
                return REQUIRE_HOURLY;

            return UPDATE_SUCCESS;
        }

        void LoadFromJson(JSONObject data){

            if(data == null){
                Log.d("null", "data was null");
                return;
            }
            try{

                if(data.has("timezone")){
                    timezone = data.getString("timezone");
                    shift = TimeZone.getTimeZone(timezone).getRawOffset() - TimeZone.getDefault().getRawOffset();
                }

                if(data.has("daily"))
                    UpdateDailyJson(data.getJSONObject("daily"));

                if(data.has("hourly"))
                    UpdateHourlyJson(data.getJSONObject("hourly"));

                if(data.has("minutely"))
                    UpdateMinutelyJson(data.getJSONObject("minutely"));

                //load current
                if(data.has("currently")){
                    JSONObject currently = data.getJSONObject("currently");

                    currentData = new WeatherData(currently);
                }

            }catch(JSONException e){
                Log.d("JSON", "invalid (full) response message");
            }
        }

        void UpdateDailyJson(JSONObject data){

            if(data == null){

                return;
            }
            try{
                if(data.has("data")){

                    JSONArray array = data.getJSONArray("data");

                    dailyData = new ArrayList<>(array.length());

                    JSONObject current;
                    for(int i = 0; i < array.length(); ++i){
                        current = array.getJSONObject(i);

                        if(i<dailyData.size()){

                            dailyData.get(i).loadFromJson(current);
                        }else{
                            dailyData.add(new WeatherData(current));
                        }
                    }

                }
            }catch(JSONException e){
                Log.d("JSON", "invalid (day) response message");
            }
        }

        void UpdateHourlyJson(JSONObject data){
            if(data == null) return;
            try{
                if(data.has("data")){

                    JSONArray array = data.getJSONArray("data");
                    hourlyData = new ArrayList<>(array.length());

                    JSONObject current;
                    for(int i = 0; i < array.length(); ++i){
                        current = array.getJSONObject(i);

                        if(i<hourlyData.size()){

                            hourlyData.get(i).loadFromJson(current);
                        }else{
                            hourlyData.add(new WeatherData(current));
                        }
                    }

                }


            }catch(JSONException e){
                Log.d("JSON", "invalid (hour) response message");
            }
        }

        void UpdateMinutelyJson(JSONObject data){
            if(data == null){
                return;
            }
            try{
                if(data.has("data")){

                    JSONArray array = data.getJSONArray("data");
                    minutelyData = new ArrayList<>(array.length());

                    currentMinute = 0;

                    JSONObject current;
                    for(int i = 0; i < array.length(); ++i){
                        current = array.getJSONObject(i);

                        if(i<minutelyData.size()){

                            minutelyData.get(i).loadFromJson(current);
                        }else{
                            minutelyData.add(new MinuteWeatherData(current));
                        }
                    }

                }

            }catch(JSONException e){
                Log.d("JSON", "invalid (minute) response message");
            }
        }
    }

    public class WeatherData{

        long
                time,
//                apparentTemperatureHighTime,
//                apparentTemperatureLowTime,
//                precipIntensityMaxTime,
                sunriseTime,
                sunsetTime;
//                temperatureLowTime,
//                temperatureHighTime;

        String
                summary,
                icon;
//                precipType;

        double
                apparentTemperature,
                apparentTemperatureHigh,
                apparentTemperatureLow,
//                cloudCover,
//                dewPoint,
                humidity,
                moonPhase,
//                nearestStormBearing,
//                nearestStormDistance,
//                ozone,
//                precipAccumulation,
                precipIntensity,
//                precipIntensityMax,
//                precipProbability,
                pressure,
                temperature,
                temperatureHigh,
                temperatureLow,
//                uvIndex,
//                uvIndexTime,
//                visibility,
//                windBearing,
                windGust,
                windSpeed;

        WeatherData(){

        }

        WeatherData(JSONObject object){

            loadFromJson(object);

        }

        void loadFromJson(JSONObject object){
            //load current data
            try{
                if(object.has("time"))
                    time = object.getLong("time");
//                if(object.has("apparentTemperatureHighTime"))
//                    apparentTemperatureHighTime = object.getLong("apparentTemperatureHighTime");
//                if(object.has("apparentTemperatureLowTime"))
//                    apparentTemperatureLowTime = object.getLong("apparentTemperatureLowTime");
//                if(object.has("precipIntensityMaxTime"))
//                    precipIntensityMaxTime = object.getLong("precipIntensityMaxTime");
                if(object.has("sunriseTime"))
                    sunriseTime = object.getLong("sunriseTime");
                if(object.has("sunsetTime"))
                    sunsetTime = object.getLong("sunsetTime");
//                if(object.has("temperatureHighTime"))
//                    temperatureHighTime = object.getLong("temperatureHighTime");

                if(object.has("summary"))
                    summary = object.getString("summary");
                if(object.has("icon"))
                    icon = object.getString("icon");
//                if(object.has("precipType"))
//                    precipType = object.getString("precipType");

                if(object.has("apparentTemperature"))
                    apparentTemperature = object.getDouble("apparentTemperature");
                if(object.has("apparentTemperatureHigh"))
                    apparentTemperatureHigh = object.getDouble("apparentTemperatureHigh");
                if(object.has("apparentTemperatureLow"))
                    apparentTemperatureLow = object.getDouble("apparentTemperatureLow");
//                if(object.has("cloudCover"))
//                    cloudCover = object.getDouble("cloudCover");
//                if(object.has("dewPoint"))
//                    dewPoint = object.getDouble("dewPoint");
                if(object.has("humidity"))
                    humidity = object.getDouble("humidity");
                if(object.has("moonPhase"))
                    moonPhase = object.getDouble("moonPhase");
//                if(object.has("nearestStormBearing"))
//                    nearestStormBearing = object.getDouble("nearestStormBearing");
//                if(object.has("nearestStormDistance"))
//                    nearestStormDistance = object.getDouble("nearestStormDistance");
//                if(object.has("ozone"))
//                    ozone = object.getDouble("ozone");
//                if(object.has("precipAccumulation"))
//                    precipAccumulation = object.getDouble("precipAccumulation");
                if(object.has("precipIntensity"))
                    precipIntensity = object.getDouble("precipIntensity");
//                if(object.has("precipIntensityMax"))
//                    precipIntensityMax = object.getDouble("precipIntensityMax");
//                if(object.has("precipProbability"))
//                    precipProbability = object.getDouble("precipProbability");
                if(object.has("pressure"))
                    pressure = object.getDouble("pressure");
                if(object.has("temperature"))
                    temperature = object.getDouble("temperature");
                if(object.has("temperatureHigh"))
                    temperatureHigh = object.getDouble("temperatureHigh");
                if(object.has("temperatureLow"))
                    temperatureLow = object.getDouble("temperatureLow");
//                if(object.has("temperatureLowTime"))
//                    temperatureLowTime = object.getDouble("temperatureLowTime");
//                if(object.has("uvIndex"))
//                    uvIndex = object.getDouble("uvIndex");
//                if(object.has("uvIndexTime"))
//                    uvIndexTime = object.getDouble("uvIndexTime");
//                if(object.has("visibility"))
//                    visibility = object.getDouble("visibility");
//                if(object.has("windBearing"))
//                    windBearing = object.getDouble("windBearing");
                if(object.has("windGust"))
                    windGust = object.getDouble("windGust");
                if(object.has("windSpeed"))
                    windSpeed = object.getDouble("windSpeed");

            }catch(JSONException e){
                Log.d("JSON", "load json data error");
            }
        }
    }

    class MinuteWeatherData{

        long time;

        String icon;

        double
                precipIntensity;
//                precipIntensityError,
//                precipProbability;

        MinuteWeatherData(){

        }

        MinuteWeatherData(JSONObject obj){
            loadFromJson(obj);
        }

        void loadFromJson(JSONObject object){

            try{

                if(object.has("time"))
                    time = object.getLong("time");

                if(object.has("icon"))
                    icon = object.getString("icon");

                if(object.has("precipIntensity"))
                    precipIntensity = object.getDouble("precipIntensity");
//                if(object.has("precipIntensityError"))
//                    precipIntensityError = object.getDouble("precipIntensityError");
//                if(object.has("precipProbability"))
//                    precipProbability = object.getDouble("precipProbability");


            }catch(JSONException e){
                Log.d("JSON", "load json data error");
            }
        }
    }

    static class DarkSkyApi {

        private final static class Units{
            static final int
                    AUTO = 1,
                    CA = 2,
                    UK2 = 3,
                    US = 4,
                    SI = 5;
        }

        private final static class ExcludeBlocks{
            static final String
                    currently = "currently",
                    minutely = "minutely",
                    hourly = "hourly",
                    daily = "daily",
                    alerts = "alerts",
                    flags = "flags";
        }

        private final static String language = "en";
        private final static String key = "c4f30e7ffd56f8a739fbf78c2ec2f22e";
        private final static String apiURL = "https://api.darksky.net/forecast";

        private static JSONObject GET(double longitude, double lattitude, String[] excludeBlocks, int units){

            StringBuilder request = new StringBuilder(apiURL + '/' + key + '/' + longitude +',' + lattitude + '?');

            boolean manyOptions = false;

            if(null != excludeBlocks && excludeBlocks.length > 0){
                request.append("exclude=");
                request.append(excludeBlocks[0]);

                for(int i = 1; i < excludeBlocks.length; ++i){
                    request.append(',');
                    request.append(excludeBlocks[i]);
                }
                manyOptions = true;
            }

            if(manyOptions)
                request.append('&');
            request.append("lang=");
            request.append(language);

            if(units != 0){
                request.append('&');
                request.append("units=");
                switch (units){
                    case Units.CA:
                        request.append("ca");
                        break;
                    case Units.UK2:
                        request.append("uk2");
                        break;
                    case Units.US:
                        request.append("us");
                        break;
                    case Units.SI:
                        request.append("si");
                        break;
                    default:
                        request.append("auto");
                }
            }

            JSONObject result = null;
            String
                    inputLine;

            Log.d("DarkSky", request.toString());

            try{

                Log.d("DarkSky", "1");
                URL url = new URL(request.toString());

                Log.d("DarkSky", "2");
                URLConnection con = url.openConnection();
                Log.d("DarkSky", "3");
                HttpURLConnection connection = (HttpURLConnection) con;
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                Log.d("DarkSky", "4");
                connection.connect();

                Log.d("DarkSky", "response code " + connection.getResponseCode());


                InputStreamReader
                        streamReader = new InputStreamReader(connection.getInputStream());

                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder resultString = new StringBuilder();

                Log.d("DarkSky", "result parsing");

                while((inputLine = reader.readLine()) != null){
                    resultString.append(inputLine);
                    resultString.append('\n');
                }

                result = new JSONObject(resultString.toString());

            }catch(IOException e){
                Log.d("DarkSky", "inout exception: " + e.toString());
            }catch(Exception e){
                Log.d("DarkSky", "tried to upload: " + e.toString());
            }

            return result;
        }

        static JSONObject getWeatherData(double longitude, double lattitude){

            return GET(longitude, lattitude, new String[]{ ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
        }

        static JSONObject getDailyWeather(double longitude, double lattitude){

            return GET(longitude, lattitude, new String[]{ExcludeBlocks.currently, ExcludeBlocks.minutely, ExcludeBlocks.hourly, ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
        }

        static JSONObject getMinutelyWeather(double longitude, double lattitude){

            return GET(longitude, lattitude, new String[]{ExcludeBlocks.currently, ExcludeBlocks.hourly, ExcludeBlocks.daily, ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
        }

        static JSONObject getHourlyWeather(double longitude, double lattitude){

            return GET(longitude, lattitude, new String[]{ExcludeBlocks.currently, ExcludeBlocks.minutely, ExcludeBlocks.daily, ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
        }

        public static JSONObject getCurrentWeather(double longitude, double lattitude){

            return GET(longitude, lattitude, new String[]{ExcludeBlocks.minutely, ExcludeBlocks.hourly, ExcludeBlocks.daily, ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
        }

    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        List<PlaceWeather> WeatherLocation;
        int currentLocation;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            WeatherLocation = new LinkedList<>();
        }

        @Override
        public Fragment getItem(int position) {

            currentLocation = position;

            if(position < WeatherLocation.size())
                return DisplayPageFragment.GetFragment(WeatherLocation.get(position));

            return AddPageFragment.Instance();
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return WeatherLocation.size()+1;
        }

        void AddItem(Address location){

            PlaceWeather data = new PlaceWeather();

            data.lattitude = location.getLatitude();
            data.longitude = location.getLongitude();


            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < location.getMaxAddressLineIndex(); ++i){
                builder.append(location.getAddressLine(i));
            }

            data.place = builder.toString();

            WeatherLocation.add(data);
        }
    }

    public void AddNewPlaceItem(Address location){
        mSectionsPagerAdapter.AddItem(location);
        mSectionsPagerAdapter.notifyDataSetChanged();

        mViewPager.setCurrentItem(mSectionsPagerAdapter.getCount()-1, false );
        mViewPager.setCurrentItem(mSectionsPagerAdapter.getCount()-2, true );
    }

    public void RemovePlaceItem(){

    }


    public static class AddPageFragment extends Fragment{// implements OnMapReadyCallback {

        AutoCompleteTextView placeAddressView;
//        MapView mv;
//        GoogleMap map;
        Address selectedAddress = null;

        public static AddPageFragment Instance(){
            return new AddPageFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_city_add, container, false);

//            mv = rootView.findViewById(R.id.mapView);
//            mv.onCreate(savedInstanceState);
//            mv.getMapAsync(this);

            placeAddressView = rootView.findViewById(R.id.autoCompleteTextViewPlace);
            final AutocompleteAdapterMapPlace adapter = new AutocompleteAdapterMapPlace(getContext(), android.R.layout.simple_list_item_1);

            if(savedInstanceState != null){
                placeAddressView.setText(savedInstanceState.getCharSequence("PlaceString"),false);
            }

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

//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatitude(), place.getLongitude()), 10);
//                    map.animateCamera(cameraUpdate);

                    selectedAddress = place;
                }
            });

            final Button button = rootView.findViewById(R.id.AddButton);
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if(selectedAddress!=null)
                    ((BaseActivity)getActivity()).AddNewPlaceItem(selectedAddress);
                }
            });

            return rootView;
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

            savedInstanceState.putCharSequence("PlaceString" ,placeAddressView.getText().subSequence(0, placeAddressView.getText().length()));
        }

        @Override
        public void onResume() {
//            mv.onResume();
            super.onResume();
        }


        @Override
        public void onPause() {
            super.onPause();
//            mv.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
//            mv.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
//            mv.onLowMemory();
        }
    }

    public static class DisplayPageFragment extends Fragment{

        PlaceWeather data;
        View rootView;
        Timer updateTimer;
        populateInitially initialUpload;

        boolean dataUploaded, dataUpdatedMinutely, dataUpdatedHourly;

        public static DisplayPageFragment GetFragment(PlaceWeather data){

            DisplayPageFragment fragment = new DisplayPageFragment();
            fragment.data = data;

            return fragment;
        }

        public DisplayPageFragment(){

            dataUploaded = false;
            updateTimer = new Timer();
            initialUpload = new populateInitially();
        }

        public static Drawable getImage(String weather){

            Drawable ret = null;
            try {
                InputStream ims = mgr.open("cloudy.jpg");
                // load image as Drawable
                ret = Drawable.createFromStream(ims, null);
            }catch (Exception e){
                return null;
            }

            return ret;
        }

        public Drawable getIcon(String weather){
            switch(weather){
                case "clear-day":
                    return rootView.getResources().getDrawable(R.drawable.clear_day);
                case "clear-night":
                    return rootView.getResources().getDrawable(R.drawable.clear_night);
                case "rain":
                    return rootView.getResources().getDrawable(R.drawable.rain);
                case "snow":
                    return rootView.getResources().getDrawable(R.drawable.snow);
                case "sleet":
                    return rootView.getResources().getDrawable(R.drawable.sleet);
                case "wind":
                    return rootView.getResources().getDrawable(R.drawable.ic_wind);
                case "fog":
                    return rootView.getResources().getDrawable(R.drawable.fog);
                case "cloudy":
                    return rootView.getResources().getDrawable(R.drawable.cloudy);
                case "partly-cloudy-day":
                    return rootView.getResources().getDrawable(R.drawable.partly_cloudy_day);
                case "partly-cloudy-night":
                    return rootView.getResources().getDrawable(R.drawable.partly_cloudy_night);
                case "sunrise":
                    return rootView.getResources().getDrawable(R.drawable.sunrise);
                case "sunset":
                    return rootView.getResources().getDrawable(R.drawable.sunset);
                default:
                    return rootView.getResources().getDrawable(R.drawable.ic_cloud_download);
            }
         }

        //todo: redo into layout.inflate
        View newHourView(String above, String iconWeather, String below){

            LinearLayout base = new LinearLayout(getContext());
            base.setOrientation(LinearLayout.VERTICAL);
            base.setLayoutParams(new LinearLayout.
                    LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            base.setGravity(Gravity.CENTER | Gravity.TOP);

            TextView tv1 = new TextView(getContext());
            tv1.setText(above);
            tv1.setId(R.id.textViewAbove);
            tv1.setLayoutParams(new LinearLayout.
                    LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            base.addView(tv1);

            ImageView iv = new ImageView(getContext());
            iv.setImageDrawable( getIcon(iconWeather));
            iv.setId(R.id.imageViewMiddle);
            iv.setLayoutParams(new LinearLayout.
                    LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            iv.setColorFilter(getResources().getColor(R.color.textColor));

            iv.getLayoutParams().width = (int)getResources().getDimension(R.dimen.image_size);
            iv.getLayoutParams().height = (int)getResources().getDimension(R.dimen.image_size);

            base.addView(iv);

            TextView tv2 = new TextView(getContext());
            tv2.setText(below);
            tv2.setId(R.id.textViewBelow);
            tv2.setLayoutParams(new LinearLayout.
                    LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            base.addView(tv2);

            return base;
        }

        void SetHourView(LinearLayout root, int index, String above, String iconWeather, String below){

            if(index < root.getChildCount()){

                View update = root.getChildAt(index);
                ((TextView)update.findViewById(R.id.textViewAbove)).setText(above);
                ((ImageView)update.findViewById(R.id.imageViewMiddle)).setImageDrawable(getIcon(iconWeather));
                ((TextView)update.findViewById(R.id.textViewBelow)).setText(below);

                root.addView(update, index);

            }else{
                View add = newHourView(above, iconWeather, below);
                root.addView(add);
            }
        }

        void SetDayView(LinearLayout root, int index, String day, String iconWeather, String minTemp, String maxTemp){

            View dayView;

            if(index < root.getChildCount())
                dayView = root.getChildAt(index);
            else{
                dayView = getLayoutInflater().inflate(R.layout.daily_item, root, false);
                root.addView(dayView);
            }

            TextView weekDayName = dayView.findViewById(R.id.textViewWeekDay);
            weekDayName.setText(day);

            ImageView dayIcon = dayView.findViewById(R.id.imageViewDayIcon);
            dayIcon.setImageDrawable(getIcon(iconWeather));

            TextView weekDayMin = dayView.findViewById(R.id.textViewDayMin);
            weekDayMin.setText(minTemp);

            TextView weekDayMax = dayView.findViewById(R.id.textViewDayMax);
            weekDayMax.setText(maxTemp);
        }

        void PopulateUi(){
            UpdateUIMinutely();
            UpdateUIHourly();
            UpdateUIDaily();
        }

        void UpdateUIMinutely(){
            //set background
            ConstraintLayout background = rootView.findViewById(R.id.constraintLayout);
            background.setBackground(getImage(""));

            //set current data view
            ImageView currentWeather = rootView.findViewById(R.id.imageViewCurrentIcon);
            TextView
                    currentTemperature = rootView.findViewById(R.id.textViewCurrentTemperature),
                    currentHumidity = rootView.findViewById(R.id.textViewHumidity),
                    currentWind = rootView.findViewById(R.id.textViewWindSpeed),
                    currentPrecipitation = rootView.findViewById(R.id.textViewPrecipitation);

            currentWeather.setImageDrawable(getIcon(data.currentData.icon));
            currentTemperature.setText((int)(data.currentData.temperature) + " °" );
            currentHumidity.setText((int)(data.currentData.humidity*100) + "%");
            currentWind.setText((int)(data.currentData.windSpeed) + " km/h");
            currentPrecipitation.setText((int)(data.currentData.precipIntensity) + " mm");
        }

        void UpdateUIHourly(){

            Date tempDate = new Date();
            DateFormat
                    sunRiseFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()),
                    hourFormat =  new SimpleDateFormat("h a", Locale.getDefault());

            //populate hourly view
            LinearLayout hourView = rootView.findViewById(R.id.HourlyWeatherLinearLayout);

            boolean sunrise = false, sunset = false;

            int cur = 0;
            for(int i = 0; i < 24; ++i, ++cur){
                if(data.currentData.sunriseTime > 10 && !sunrise)
                    if(data.hourlyData.get(data.currentHour+i).time > data.currentData.sunriseTime)
                    {
                        tempDate.setTime(data.currentData.sunriseTime*1000);
                        SetHourView(hourView, cur,  sunRiseFormat.format(tempDate), "sunrise", "Sunrise");
                        ++cur;
                        sunrise = true;
                    }
                if(data.currentData.sunsetTime > 10 && !sunset)
                    if(data.hourlyData.get(data.currentHour+i).time > data.currentData.sunsetTime)
                    {
                        tempDate.setTime(data.currentData.sunsetTime*1000);
                        SetHourView(hourView, cur,  sunRiseFormat.format(tempDate), "sunset", "Sunset");
                        ++cur;
                        sunset = true;
                    }

                tempDate.setTime(data.hourlyData.get(data.currentHour+i).time*1000);
                SetHourView(hourView,
                        cur,
                        hourFormat.format(tempDate),
                        data.hourlyData.get(data.currentHour+i).icon,
                        "" + (int)(data.hourlyData.get(data.currentHour+i).temperature) + " °" );
            }

            while(hourView.getChildCount() > cur){
                hourView.removeViewAt(cur);
            }

        }

        void UpdateUIDaily(){
            Date tempDate = new Date();
            DateFormat
                    currentDayFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()),
                    weekDayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

            //set current date
            TextView currentDay = rootView.findViewById(R.id.textViewCurrentDay);
            tempDate.setTime(System.currentTimeMillis());
            currentDay.setText(currentDayFormat.format(tempDate));

            TextView
                    minDayTemp = rootView.findViewById(R.id.textViewHourlyTempMin),
                    maxDayTemp = rootView.findViewById(R.id.textViewHourlyTempMax);

            minDayTemp.setText( (int)(data.currentData.apparentTemperatureLow));
            maxDayTemp.setText( (int)(data.currentData.apparentTemperatureHigh));

            //populate daily view
            LinearLayout dayViewList = rootView.findViewById(R.id.DailyWeatherLinearLayout);

            for(int i = 1; i < data.dailyData.size(); ++i){
                tempDate.setTime(data.dailyData.get(i).time*1000);
                SetDayView(dayViewList, i,
                        weekDayFormat.format(tempDate),
                        data.dailyData.get(i).icon,
                        ""+(int)(data.dailyData.get(i).temperatureLow),
                        ""+(int)(data.dailyData.get(i).temperatureHigh));
            }

            while(dayViewList.getChildCount() > data.dailyData.size()-1){
                dayViewList.removeViewAt(data.dailyData.size()-1);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_city_weather, container, false);

            initialUpload.execute();

            updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(dataUploaded) {

                        dataUpdatedHourly = false;
                        dataUpdatedMinutely = false;

                        if(data.RequireUpload() == PlaceWeather.REQUIRE_MINUTELY){

                            data.UpdateMinutelyJson(DarkSkyApi.getMinutelyWeather(data.longitude, data.lattitude));
                            dataUpdatedMinutely = true;
                        }
                        if(data.RequireUpload()== PlaceWeather.REQUIRE_HOURLY){
                            data.UpdateHourlyJson(DarkSkyApi.getHourlyWeather(data.longitude, data.lattitude));
                            data.UpdateDailyJson(DarkSkyApi.getHourlyWeather(data.longitude, data.lattitude));
                            dataUpdatedHourly = true;
                        }

                        data.Update(System.currentTimeMillis()/1000);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UpdateUIMinutely();
                                if(dataUpdatedMinutely)UpdateUIHourly();
                                if(dataUpdatedHourly)UpdateUIDaily();
                            }
                        });
                    }
                }
            }, 60000, 60000);

            return rootView;
        }

        @Override
        public void onDestroyView(){
            super.onDestroyView();

            updateTimer.cancel();
            updateTimer.purge();

        }

        private class populateInitially extends AsyncTask<Integer, Integer, Integer>{

            @Override
            protected Integer doInBackground(Integer ... params ) {

                if(!dataUploaded){
                    data.LoadFromJson(DarkSkyApi.getWeatherData(data.longitude, data.lattitude));
                    dataUploaded = true;
                }

                return 0;
            }

            @Override
            protected void onPostExecute(Integer integer) {

                PopulateUi();
            }
        }

    }

}