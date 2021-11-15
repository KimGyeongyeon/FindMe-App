package com.example.findme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PetInfo {
    private String name;
    private Date date;
    private String detail;
    private String[] location = {"36","127"};
    private String[] profile_images = {"a","a","a","a"};

    public PetInfo(){}

    public String getName(){
        return name;
    }

    public String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        return format.format(date);
    }

    public String getDetail(){
        return detail;
    }

    public String[] getLocation(){
        return location;
    }

    public String[] getImage(){
        // image load library url을 바로 연결할 수 있음.
        return profile_images;
    }


}
