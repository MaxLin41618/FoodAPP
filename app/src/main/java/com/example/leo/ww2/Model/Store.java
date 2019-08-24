package com.example.leo.ww2.Model;

public class Store {
    private String Name;
    private String Image;

    public Store(){

    }

    public Store(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}