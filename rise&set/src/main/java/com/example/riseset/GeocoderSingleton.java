package com.example.riseset;

import android.content.Context;
import android.location.Geocoder;
import android.widget.Toast;

public class GeocoderSingleton {

    private static Geocoder instance;

    public GeocoderSingleton(Context context) {
        if(!Geocoder.isPresent()) {
            Toast.makeText(context, "google maps required", Toast.LENGTH_LONG).show();
            return;
        }
        if(instance == null) {
            instance = new Geocoder(context);
        }
    }

    public static Geocoder getInstance() {
        return instance;
    }
}
