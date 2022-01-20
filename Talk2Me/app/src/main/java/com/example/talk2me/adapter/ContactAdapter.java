package com.example.talk2me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.talk2me.R;
import com.example.talk2me.model.Contacto;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contacto> {
    private Context context;
    private ArrayList<Contacto> contactos;
    public ContactAdapter(@NonNull Context context , @NonNull ArrayList<Contacto> objects) {
        super(context, 0, objects);
        this.contactos = objects;               //estes objects sao os meus contactos! que sao passados quando esta classe e chamada nos ContactosFragment
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //check if list is empty
        if(contactos != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_contacts, parent, false);
            TextView nomeContacto = view.findViewById(R.id.tv_nome);
            TextView emailContacto = view.findViewById(R.id.tv_email);
            Contacto contacto = contactos.get(position);
            nomeContacto.setText(contacto.getNome() );
            emailContacto.setText(contacto.getEmail() );
        }
        return view;
    }
}












































