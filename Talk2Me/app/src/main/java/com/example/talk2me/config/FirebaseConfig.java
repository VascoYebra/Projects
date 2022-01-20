package com.example.talk2me.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseConfig {     //classe final, não pode ser extendida!!!!!!!!!!!!!!!!!!!!
    private static DatabaseReference referenceFirebase;
    private static FirebaseAuth firebaseAuth;       //autenticacao

    public static DatabaseReference getFirebase(){          //static e para poder chamar este metodo directo sem ser preciso instanciar a classe!
        if(referenceFirebase == null){
            referenceFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return  referenceFirebase;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }
}









































