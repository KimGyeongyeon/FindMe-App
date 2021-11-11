package com.example.findme;

import java.util.ArrayList;
import java.util.Date;

public class Group {
    private String gName;
    private int gNumber;
    private Date gCreateDate;
    private Date gActionDate;

    public Group(String name, int number, Date date1, Date date2) {
        gName = name;
        gNumber = number;
        gCreateDate = date1;
        gActionDate = date2;
    }

    public Date getgActionDate() {
        return gActionDate;
    }

    public Date getgCreateDate() {
        return gCreateDate;
    }

    public void setgName (String name) {
        this.gName = name;
    }

    public void setgActionDate (Date gActionDate) {
        this.gActionDate = gActionDate;
    }

    private static int lastGroupId = 0;

}
