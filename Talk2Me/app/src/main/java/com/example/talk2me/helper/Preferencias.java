package com.example.talk2me.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "T2M.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private final String CHAVE_NOME = "nomeLoggedUser";

    public Preferencias(Context contextoParametro){
        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
    }

    public void saveData(String identificadorUser, String nameUser){
        editor.putString(CHAVE_IDENTIFICADOR, identificadorUser);
        editor.putString(CHAVE_NOME, nameUser);
        editor.commit();        //para guardar de factos os dados de cima....

    }

    public String getIndentifier(){
        return preferences.getString(CHAVE_IDENTIFICADOR,null);
    }

    public String getNome(){
        return preferences.getString(CHAVE_NOME,null);
    }

}





























