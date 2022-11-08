package com.example.findme.detail;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotHere {
    public String userMail;
    public GeoPoint latLng;
    public int weight;
    public Date date;

    public NotHere(){
        this.weight = 5;
    }

    public NotHere(String email, GeoPoint location, int weight, Date date){
        this.userMail = email;
        this.latLng = new GeoPoint(location.getLatitude(), location.getLongitude());
        this.weight = weight;
        this.date = date;
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
