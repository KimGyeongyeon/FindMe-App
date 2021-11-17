package com.example.findme;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PetInfo {
    private String date;
    private ArrayList<String> img;
    private Location location;
    private String name;
    private String profile;


    public PetInfo(){
        super();
        this.location = new Location("LocationManager#GPS_PROVIDER");
        this.img = new ArrayList<>(4);
        for(int i=0 ; i<4; i++){
            this.img.
                    add("https://firebasestorage.googleapis.com/v0/b/findme-a2f27.appspot.com/o/pet-info%2FGold%2Fgold_fail_casejpg.jpg?alt=media&token=b74d2b08-bddf-4b25-a241-c23f6237216a");
        }
    }

//    public PetInfo(PetInfo p){
//        this.date = p.getDate();
//        this.name = p.getName();
//        this.detail = p.getDetail();
//        this.location[0] = p.getLocation()[0];
//        this.location[1] = p.getLocation()[1];
//        for(int i=0 ; i<4; i++){
//            this.profile_images[i] = p.getImage()[i];
//        }
//    }
//
    public PetInfo( Date date, String[] profile_images,
                    double[] location, String name, String profile){
        super();
        this.date = String.valueOf(date);
        this.name = name;
        this.profile = profile;
        this.location = new Location("LocationManager#GPS_PROVIDER");
        this.location.setLatitude(location[0]);
        this.location.setLongitude(location[1]);
        this.img = new ArrayList<>(4);
        for(int i=0 ; i<4; i++){
            this.img.add( profile_images[i]);
        }

    }

    public PetInfo sample_data(){
        Date test_date = new Date(2021, 10, 14, 12, 27, 31);
        String[] url = {
                "https://firebasestorage.googleapis.com/v0/b/findme-a2f27.appspot.com/o/pet-info%2FGold%2Fgold_fail_casejpg.jpg?alt=media&token=b74d2b08-bddf-4b25-a241-c23f6237216a",
                "https://firebasestorage.googleapis.com/v0/b/findme-a2f27.appspot.com/o/pet-info%2FGold%2Fgold_fail_casejpg.jpg?alt=media&token=b74d2b08-bddf-4b25-a241-c23f6237216a",
                "https://firebasestorage.googleapis.com/v0/b/findme-a2f27.appspot.com/o/pet-info%2FGold%2Fgold_fail_casejpg.jpg?alt=media&token=b74d2b08-bddf-4b25-a241-c23f6237216a",
                "https://firebasestorage.googleapis.com/v0/b/findme-a2f27.appspot.com/o/pet-info%2FGold%2Fgold_fail_casejpg.jpg?alt=media&token=b74d2b08-bddf-4b25-a241-c23f6237216a"
        };
        double[] loc_arr = {36.368155, 127.364914};
        return new PetInfo(test_date, url, loc_arr,
                "Guemi","I lost him in front of E3. He is wearing red ribbon. Please find himTT ");
    }

    public String getName(){
        return name;
    }

    public void setName(){
        this.name = name;
    }

    public String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
//        Date tmp = format.format(date)
//        return format.format(date);
        return date;
    }

    public void setDate(Date date) {
        this.date = String.valueOf(date);
    }

    public String getProfile(){
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Location getLocation(){
        return location;
    }

    public void setLocation(double[] location) {
        this.location.setLatitude(location[0]);
        this.location.setLongitude(location[1]);
    }

    public ArrayList<String> getImage(){
        // image load library url을 바로 연결할 수 있음.
        return img;
    }

    public void setImg(String[] img) {
        for(int i = 0 ; i<4 ; i++) {
            this.img.add(img[i]);
        }
    }
}
