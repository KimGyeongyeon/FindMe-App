package com.example.findme.detail;

import java.util.Date;

public class HereReportCard
{
    // Variable to store data corresponding
    // to firstname keyword in database
    private Date date;

    // Variable to store data corresponding
    // to lastname keyword in database
    private String userMail;

    // Variable to store data corresponding
    // to age keyword in database
    private String imgUrl;

    // Mandatory empty constructor
    // for use of FirebaseUI
    public HereReportCard() {}

    public HereReportCard(Date date, String userMail, String imgUrl){
        this.date = date;
        this.userMail = userMail;
        this.imgUrl = imgUrl;
    }

    // Getter and setter method
    public Date getDate() {return this.date;}
    public void setDate(Date date) {this.date = date;}

    public String getUserMail() {return this.userMail;}
    public void setUserMail(String userMail) {this.userMail = userMail;}

    public String getImgUrl() {return this.imgUrl;}
    public void setImgUrl(String imgUrl) {this.imgUrl = imgUrl;}

}