package com.example.findme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PetInfo {
    private String date;
    private String[] profile_images = {"a","a","a","a"};
    private double[] location = {36.0, 127.0};
    private String name;
    private String detail;


    public PetInfo(){

    }

    public PetInfo(PetInfo p){
        this.date = p.getDate();
        this.name = p.getName();
        this.detail = p.getDetail();
        this.location[0] = p.getLocation()[0];
        this.location[1] = p.getLocation()[1];
        for(int i=0 ; i<4; i++){
            this.profile_images[i] = p.getImage()[i];
        }
    }

    public PetInfo( Date date, String[] profile_images,
                    double[] location, String name, String detail){
        super();
        this.date = String.valueOf(date);
        this.name = name;
        this.detail = detail;
        this.location[0] = location[0];
        this.location[1] = location[1];
        for(int i=0 ; i<4; i++){
            this.profile_images[i] = profile_images[i];
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

    public String getDetail(){
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double[] getLocation(){
        return location;
    }

    public void setLocation(float[] location) {
        this.location[0] = location[0];
        this.location[1] = location[1];
    }

    public String[] getImage(){
        // image load library url을 바로 연결할 수 있음.
        return profile_images;
    }

    public void setProfile_images(String[] profile_images) {
        for(int i = 0 ; i<4 ; i++) {
            this.profile_images[i] = profile_images[i];
        }
    }
}
