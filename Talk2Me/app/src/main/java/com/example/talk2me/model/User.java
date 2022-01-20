package com.example.talk2me.model;

import com.example.talk2me.config.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class User {
    private String id;
    private String nome;
    private String email;
    private String password;

    public User(){

    }

    @Exclude                                                        //quando o firebase receber o this, nao guarda este ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void save(){
        DatabaseReference referenciaFirebase = FirebaseConfig.getFirebase();
        referenciaFirebase.child("users").child(getId()).setValue(this);                    //com o this aqui escrevo os proprios valores
    }
}















































































