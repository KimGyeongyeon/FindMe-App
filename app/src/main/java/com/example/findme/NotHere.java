package com.example.findme;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class NotHere {
    public GeoPoint latLng;
    public int weight;

    public NotHere(){
        this.weight = 5;
    }

    public NotHere(GeoPoint location, int weight){
        this.latLng = new GeoPoint(location.getLatitude(), location.getLongitude());

        this.weight = weight;
    }

    public void setLocation(GeoPoint location) {
        if(location != null){
            this.latLng = new GeoPoint(location.getLatitude(), location.getLongitude());
            Log.d("PetInfo_NotHere class", String.valueOf(location));
        }

        else
            Log.d("PetInfo_NotHere class", "Try to set as null");
    }

    public void setWeight(int w){
        this.weight = w;
    }
}
