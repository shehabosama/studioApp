package com.example.bebo2.studio_app;

public class All_photographer {
    public String fullName;
    public String profilimage_photo;
    public String status;


    public All_photographer(){

    }

    public All_photographer(String user_name, String user_image, String user_status) {
        this.fullName = user_name;
        this.profilimage_photo = user_image;
        this.status = user_status;
    }

    public String getUser_name() {
        return fullName;
    }

    public void setUser_name(String user_name) {
        this.fullName = user_name;
    }

    public String getUser_image() {
        return profilimage_photo;
    }

    public void setUser_image(String user_image) {
        this.profilimage_photo = user_image;
    }

    public String getUser_status() {
        return status;
    }

    public void setUser_status(String user_status) {
        this.status = user_status;
    }
}
