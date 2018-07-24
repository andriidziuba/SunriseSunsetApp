package com.example.riseset;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CustomAdapter extends ArrayAdapter<Place> implements Filterable {
    private List<Place> items;
    private int viewResourceId;

    public CustomAdapter(Context context, int viewResourceId) {
        super(context, viewResourceId);
        this.viewResourceId = viewResourceId;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public Place getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        Place place = items.get(position);
        if (place != null) {
            TextView mainTextLabel = (TextView) v.findViewById(R.id.mainText);
            TextView secondaryLabel = (TextView) v.findViewById(R.id.secondaryText);
            if (mainTextLabel != null) {
                mainTextLabel.setText(place.getMainText());
            }
            if (secondaryLabel != null) {
                secondaryLabel.setText(place.getSecondaryText());
            }
        }
        return v;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Place> places = findPlaces(constraint.toString());
                    filterResults.values = places;
                    filterResults.count = places.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    items = (List<Place>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    private String apiKey = "your key";

    private List<Place> findPlaces(String s) {
        GetRequest request = new GetRequest();
        request.execute("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + s + "&key=" + apiKey);
        List<Place> receivedItems = new ArrayList<Place>();
        try {
            JSONObject object = request.get();
            if(object.getString("status").equals("OK")) {
                JSONArray predictions = object.getJSONArray("predictions");
                for(int i = 0; i < predictions.length(); i++) {
                    JSONObject _o = predictions.getJSONObject(i);
                    receivedItems.add(new Place(_o.getString("description"),
                            _o.getJSONObject("structured_formatting").getString("main_text"),
                            _o.getJSONObject("structured_formatting").getString("secondary_text")));
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return receivedItems;
    }
}
