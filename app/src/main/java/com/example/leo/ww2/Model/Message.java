package com.example.leo.ww2.Model;

public class Message {
    private String to; //must set it 'to' due to http, json issues
    private Notification notification;

    public Message() {
    }

    public Message(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}

