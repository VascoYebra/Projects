package com.example.talk2me.model;

public class Contacto {

    private String identifierUser;
    private String nome;
    private String email;

    public Contacto() {     //este constructor te
    }

    public String getIdentifierUser() {
        return identifierUser;
    }

    public void setIdentifierUser(String identifierUser) {
        this.identifierUser = identifierUser;
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
}
