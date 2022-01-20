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
import com.example.talk2me.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ConversaAdapter extends ArrayAdapter<Chat> {
    private ArrayList<Chat> conversas;
    private Context context;
    public ConversaAdapter(@NonNull Context context,  @NonNull ArrayList<Chat> objects) {
        super(context, 0, objects);
        this.context = context;
        this.conversas = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(conversas != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_conversas, parent, false);

            TextView nome = view.findViewById(R.id.tv_tituolo);
            TextView lastMessage = view.findViewById(R.id.tv_subtitulo);

            Chat conversa = conversas.get(position);
            nome.setText(conversa.getName() );          //este getName foi hardcore
            lastMessage.setText(conversa.getMessage() );
        }

        return view;
    }
}




















