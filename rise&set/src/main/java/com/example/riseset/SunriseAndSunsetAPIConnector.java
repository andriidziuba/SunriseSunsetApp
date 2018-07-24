package com.example.riseset;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static com.example.riseset.MainActivity.TAG;

public class SunriseAndSunsetAPIConnector {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a");
    private final SimpleDateFormat newPattern = new SimpleDateFormat("HH:mm:ss");

    {
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+00"));
    }

    private String sunriseTime, sunsetTime, noonTime, dayLength, ctb, cte, ntb, nte, atb, ate;

    public JSONObject getTimes(Place placeForSeek, Date date) {
        GetRequest request = new GetRequest();
        request.execute("https://api.sunrise-sunset.org/json?lat=" + placeForSeek.getAddress().getLatitude() + "&lng=" + placeForSeek.getAddress().getLongitude() + "&date=" + sdf.format(date));
        try {
            JSONObject jsonObject = request.get();
            if (jsonObject == null) {
                Log.d(TAG, "onCreate: json null");
            } else
                try {
                    if (jsonObject.getString("status").equals("OK")) {
                        JSONObject childObject = jsonObject.getJSONObject("results");
                        parseJSON(childObject);
                        return childObject;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseJSON(JSONObject o) {
        try {
            sunriseTime = newPattern.format(timeFormat.parse(o.getString("sunrise")));
            sunsetTime = newPattern.format(timeFormat.parse(o.getString("sunset")));
            noonTime = newPattern.format(timeFormat.parse(o.getString("solar_noon")));
            dayLength = o.getString("day_length");
            ctb = newPattern.format(timeFormat.parse(o.getString("civil_twilight_begin")));
            cte = newPattern.format(timeFormat.parse(o.getString("civil_twilight_end")));
            ntb = newPattern.format(timeFormat.parse(o.getString("nautical_twilight_begin")));
            nte = newPattern.format(timeFormat.parse(o.getString("nautical_twilight_end")));
            atb = newPattern.format(timeFormat.parse(o.getString("astronomical_twilight_begin")));
            ate = newPattern.format(timeFormat.parse(o.getString("astronomical_twilight_end")));

        } catch (JSONException | ParseException e) {
            Log.d(TAG, "parseJSON: " + e.toString() + " " + Log.getStackTraceString(e));
            e.printStackTrace();
        }
    }

    public String getSunriseTime() {
        return sunriseTime;
    }

    public String getSunsetTime() {
        return sunsetTime;
    }

    public String getNoonTime() {
        return noonTime;
    }

    public String getDayLength() {
        return dayLength;
    }

    public String getCtb() {
        return ctb;
    }

    public String getCte() {
        return cte;
    }

    public String getNtb() {
        return ntb;
    }

    public String getNte() {
        return nte;
    }

    public String getAtb() {
        return atb;
    }

    public String getAte() {
        return ate;
    }
}
