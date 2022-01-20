package com.example.talk2me.helper;

//os nos no android na verdade sao emails codificados em base64. preciso desta classe para os codificar sem um website online e os descodificar para procurar

import android.util.Base64;

public class Base64Custom {
    public static String codeBase64(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT ).replaceAll("(\\n|\\r)", "");                 //nao queremos que o codigo gere um quebra de linha \n... obrigado C
    }

    public static String decodeBase64(String stringToDecode){
        return new String ( Base64.decode(stringToDecode, Base64.DEFAULT) );                        //so esta linha foram 3 horas....aosdiausgdiuagsdiugasidgq
    }
}
