package com.example.riseset;

import android.location.Address;
import android.util.Log;

import java.io.IOException;

import static com.example.riseset.MainActivity.TAG;

public class Place {

    private String description;
    private String mainText, secondaryText;
    private Address address;

    public Place(String description, String mainText, String secondaryText) {
        this.description = description;
        this.mainText = mainText;
        this.secondaryText = secondaryText;
    }

    public Place(Address address) {
        this.address = address;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    @Override
    public String toString() {
        return mainText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void retrieveAddress() {
        try {
            this.address = GeocoderSingleton.getInstance().getFromLocationName(description, 1).get(0);
        } catch (IOException | IndexOutOfBoundsException e) {
            this.address = null;
            Log.d(TAG, "Place: incorrect place");
        }
    }
}
