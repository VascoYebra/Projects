package com.example.talk2me.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {
    public static boolean checkPermissions(int requestCode, Activity activity, String[] permissions){

        if(Build.VERSION.SDK_INT >= 23){

            List<String> ListaPermissoes = new ArrayList<String>();

            for(String permissao : permissions){
                Boolean checkPermi = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if(!checkPermi){       //senao tiver esta permissao,
                    ListaPermissoes.add(permissao);
                }
            }

            if(ListaPermissoes.isEmpty()){
                return true;
            }

            String[] CorrectFormat = new String[ListaPermissoes.size()];
            ListaPermissoes.toArray(CorrectFormat);
            //Pedir as permissoes que nao tenho, estao na lista
            ActivityCompat.requestPermissions(activity, CorrectFormat, requestCode);

        }

        return true;
    }
}
































































