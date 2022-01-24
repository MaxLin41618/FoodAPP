package com.example.leo.ww2.Model;

public class Billboard {
    private String billboard_image;
    private String billboard_userName;
    private String billboard_userPhone;
    private String billboard_title;
    private String billboard_comment;

    public Billboard() {
    }

    public Billboard(String billboard_image, String billboard_userName, String billboard_userPhone, String billboard_title, String billboard_comment) {
        this.billboard_image = billboard_image;
        this.billboard_userName = billboard_userName;
        this.billboard_userPhone = billboard_userPhone;
        this.billboard_title = billboard_title;
        this.billboard_comment = billboard_comment;
    }

    public String getBillboard_image() {
        return billboard_image;
    }

    public void setBillboard_image(String billboard_image) {
        this.billboard_image = billboard_image;
    }

    public String getBillboard_userName() {
        return billboard_userName;
    }

    public void setBillboard_userName(String billboard_userName) {
        this.billboard_userName = billboard_userName;
    }

    public String getBillboard_userPhone() {
        return billboard_userPhone;
    }

    public void setBillboard_userPhone(String billboard_userPhone) {
        this.billboard_userPhone = billboard_userPhone;
    }

    public String getBillboard_title() {
        return billboard_title;
    }

    public void setBillboard_title(String billboard_title) {
        this.billboard_title = billboard_title;
    }

    public String getBillboard_comment() {
        return billboard_comment;
    }

    public void setBillboard_comment(String billboard_comment) {
        this.billboard_comment = billboard_comment;
    }

}
