package com.example.leo.ww2.Model;

public class User {
    private String Name;
    private String Password;
    private String Email;
    private String Phone;
    private String IsStaff;
    private String Blacklist;

    public User() {
    }

    public User(String name, String password, String email) {
        Name = name;
        Password = password;
        Email = email;
        IsStaff = "false";
        Blacklist = "false";
    }

    public String getBlacklist() {
        return Blacklist;
    }

    public void setBlacklist(String blacklist) {
        Blacklist = blacklist;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}

