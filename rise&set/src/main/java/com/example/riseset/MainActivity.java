package com.example.riseset;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "myTag";

    private DelayAutoCompleteTextView autoCompleteTextView = null;
    private ImageButton gpsButton = null;

    private LocationManager locationManager = null;
    private SunriseAndSunsetAPIConnector apiConnector = null;

    private Location location = null;

    private TextView dateTextView = null;
    private TextView resultTextView = null;
    private Button chooseDateButton = null;
    private Button getInfoButton = null;
    private Button clearButton = null;
    private Date date = new Date();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private Context context;

    private Place placeForSeek = null;

//    String format = "Sunrise: %s\nSunset: %s\nSolar noon: %s\nDay length: %s\nCivil twilight begin: %s\nCivil twilight end: %s\n" +
//            "Nautical twilight begin: %s\nNautical twilight end: %s\nAstronomical twilight begin: %s\nAstronomical twilight end: %s\nTimes is in your current timezone!";

    String format = "Sunrise: \t%s\nSunset: \t%s\nSolar noon: \t%s\nDay length: \t%s\nCivil twilight begin: \t%s\nCivil twilight end: \t%s\n" +
            "Nautical twilight begin: \t%s\nNautical twilight end: \t%s\nAstronomical twilight begin: \t%s\nAstronomical twilight end: \t%s\nAll times are in your current timezone!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        new GeocoderSingleton(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        apiConnector = new SunriseAndSunsetAPIConnector();

        setContentView(R.layout.activity_main);
        resultTextView = findViewById(R.id.resultTextView);
        clearButton = findViewById(R.id.button5);
        gpsButton = findViewById(R.id.button);
        chooseDateButton = findViewById(R.id.button3);
        getInfoButton = findViewById(R.id.button4);
        dateTextView = findViewById(R.id.dateTextView2);

        dateTextView.setText("Choosen date:\n" + sdf.format(date));

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000 * 10, 10, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.setText("");
            }
        });

        chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DatePickerActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        getInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placeForSeek == null) {
                    Toast.makeText(context, "Choose place", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject jsonObject = apiConnector.getTimes(placeForSeek, date);
                if(jsonObject == null) {
                    Toast.makeText(context, R.string.con_error, Toast.LENGTH_SHORT).show();
                } else {
                    String result = String.format(format, apiConnector.getSunriseTime(), apiConnector.getSunsetTime(),
                            apiConnector.getNoonTime(), apiConnector.getDayLength(), apiConnector.getCtb(), apiConnector.getCte(),
                            apiConnector.getNtb(), apiConnector.getNte(), apiConnector.getAtb(), apiConnector.getAte());
                    resultTextView.setText(result);
                }
            }
        });

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        CustomAdapter adapter = new CustomAdapter(this, R.layout.auto_complete_button);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Place place = (Place) adapterView.getItemAtPosition(position);
                place.retrieveAddress();
                placeForSeek = place;
                autoCompleteTextView.setText(place.getDescription());
                InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        date = new Date(data.getLongExtra("date", 0));
        dateTextView.setText("Choosen date:\n" + sdf.format(date));
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location loc) {
            location = loc;
            Address address = null;
            try {
                address = GeocoderSingleton.getInstance().getFromLocation(loc.getLatitude(), loc.getLongitude(), 1).get(0);
            } catch (IOException e1) {
                Log.d(TAG, "onLocationChanged: " + e1.toString());
            }
            placeForSeek = new Place(address);
            autoCompleteTextView.setText(address.getAddressLine(0));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locationManager.getLastKnownLocation(provider);
        }

    };
}
