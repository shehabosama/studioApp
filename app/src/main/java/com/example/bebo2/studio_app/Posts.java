package com.example.bebo2.studio_app;

public class Posts {
    private String counter,date,time,description,fullname,postimage,profileimage,uid;



    public Posts() {
    }

    public Posts(String counter, String date, String time, String description, String fullname, String postimage, String profileimage, String uid) {
        this.counter = counter;
        this.date = date;
        this.time = time;
        this.description = description;
        this.fullname = fullname;
        this.postimage = postimage;
        this.profileimage = profileimage;
        this.uid = uid;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
