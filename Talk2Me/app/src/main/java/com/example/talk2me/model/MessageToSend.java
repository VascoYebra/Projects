package com.example.talk2me.model;

public class MessageToSend {

    private String idUser;
    private String message;
    private String time ="RIP";

    public MessageToSend() {      //constructor vazio e o que o firebase pede!
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
