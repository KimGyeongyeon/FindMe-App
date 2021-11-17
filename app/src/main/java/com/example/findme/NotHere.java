package com.example.findme;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class NotHere {
    public Location latLng;
    public int weight;

    public NotHere(){
        this.latLng = new Location("LocationManager#GPS_PROVIDER");
        this.weight = 5;
    }

    public NotHere(Location location, int weight){
        this.latLng = new Location(location);

        this.weight = weight;
    }

    public void setLocation(Location location) {
        if(location != null){
            this.latLng = new Location(location);
            Log.d("PetInfo_NotHere class", String.valueOf(location));
        }

        else
            Log.d("PetInfo_NotHere class", "Try to set as null");
    }

    public void setWeight(int w){
        this.weight = w;
    }
}
