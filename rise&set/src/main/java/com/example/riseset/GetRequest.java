package com.example.riseset;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest extends AsyncTask<String, Void, JSONObject>{

    private static final String TAG = "GET_tag";

    @Override
    protected JSONObject doInBackground(String... strings) {
        HttpURLConnection connection = null;
        String response = new String();
        try {
            connection = (HttpURLConnection) new URL(strings[0]).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String _s;
            while((_s = reader.readLine()) != null) response += _s;
            reader.close();
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: " + e.toString() + " " + Log.getStackTraceString(e));
            return null;
        }
        if (response.isEmpty()) return null;
        Log.d(TAG, "doInBackground: response: " + response);
        JSONObject json = null;
        try {
            json = new JSONObject(response);
        } catch (JSONException e) {
            Log.d(TAG, "doInBackground: " + e.toString() + " " + Log.getStackTraceString(e));
            return null;
        }
        return json;
    }
}
