package com.example.talk2me.model;

import com.google.firebase.database.Exclude;

public class Chat {

    private String idUser;
    private String name;
    private String message;
    private String imgUrl = null;
    private String time;
    private String mKey;

    public Chat() {
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getimgUrl() {
        return imgUrl;
    }

    public void setimgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setTime(String timeReceveid){
        time = timeReceveid;
    }

    public String getTime() {
        return time;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }

    @Exclude
    public void setKey(String key){
        mKey = key;
    }
}
