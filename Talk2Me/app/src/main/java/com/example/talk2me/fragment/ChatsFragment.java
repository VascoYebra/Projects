package com.example.talk2me.fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.talk2me.R;
import com.example.talk2me.activity.ChatActivity;
import com.example.talk2me.activity.MainActivity;
import com.example.talk2me.adapter.ConversaAdapter;
import com.example.talk2me.adapter.ImageAdapter;
import com.example.talk2me.config.FirebaseConfig;
import com.example.talk2me.helper.Base64Custom;
import com.example.talk2me.helper.Notifications;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.Chat;
import com.example.talk2me.model.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter<Chat> adapter;
    private ArrayList<Chat> conversas;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConversas;
    private NotificationManagerCompat notificationManager;
    private String idDestiny;
    private FirebaseStorage mStorage;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        notificationManager = NotificationManagerCompat.from(getContext());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        conversas = new ArrayList<>();
        listView = view.findViewById(R.id.lv_conversas);
        adapter = new ConversaAdapter(getActivity(), conversas);            //como isto e um fragment e melhor usar getActivity....
        listView.setAdapter(adapter);

        //usar o preferencias para ter os dados
        Preferencias preferencias = new Preferencias( getActivity() );
        String idLoggedUser = preferencias.getIndentifier();       //isto as vezes fica null, dai nao funcionar no TLM! nao era do tlm mas sim da aplicação
        //Acontence quando um utilizador ja resgistado com conversas e chats na databse, desinstala e volta a instalar a aplicacao
        //POSSIVEL SOLUÇÃO: se der null, vou buscar o idLoggedUser ao email que for introduzido :)
        //POSSIVEL SOLUÇÃO: ir ao resgister user para ver o que faço de diferente ai
        if(idLoggedUser == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            if(extras != null) {
                idLoggedUser = Base64Custom.codeBase64(extras.getString("emailJustInCase"));
            }
        }

        //sacar as conversas do firebase
        Log.i(String.valueOf(getContext()), "idLoggedUser--->"+idLoggedUser);
        firebase = FirebaseConfig.getFirebase().child("chats").child(idLoggedUser);

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversas.clear();
                for(DataSnapshot dados: snapshot.getChildren()){
                    Chat conversa = dados.getValue(Chat.class);
                    //para saber se o utilizador leu a mensagem, guardo as ultimas mensagens num arrayList, e depois comparo com as novas
                    //Log.i(String.valueOf(getContext()), "conversa.getIdUser-> "+Base64Custom.decodeBase64(conversa.getIdUser())+" IdLoggedUser--> "+Base64Custom.decodeBase64(idLoggedUser) );
                    conversas.add(conversa);
                }
                adapter.notifyDataSetChanged();

                //se conseguir verificar aqui neste IF se o utilizador tem o chatAcitivty aberto consigo fazer o que quero! WORKS!!!!!
                if( !ChatActivity.isChatActivityRunning ) {

                    Intent acitivtyIntent = new Intent(getContext(), MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity( getContext(), 0, acitivtyIntent, 0 );
                    //Um pending intent é um intent que so vai ver feito se o utilizador carregar na notificação!

                    //quando o data set mudar, se o USER nao estiver na aplicação mando uma notificação
                    Log.i(String.valueOf(getContext()), "Receveid a new Text-----> ");      //se isto so funcionar com a app aberta tenho que mexer no listenner
                    //Codigo para tratar das notificações, e uma classe que se chama Notifications no package Helper:
                    Notification notification = new NotificationCompat.Builder(getContext(), Notifications.CHANNEL_1_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)     //para ficar com o logo da minha aplicação!
                            .setContentTitle("T2M New Message!")
                            .setContentText("You got a new message!")
                            .setPriority(NotificationManager.IMPORTANCE_MAX)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(contentIntent)            //este intent so vai ser executado se eu carregar na propria notificacao
                            .setAutoCancel(true)                        //quando carrego na notificação, ela desaparece
                            .setOnlyAlertOnce(true)                     //so faz som uma vez
                            .setColor(Color.argb(0, 153, 0,204))
                            .build();

                    notificationManager.notify(1, notification);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //para carregar numa conversa e mostrar o historico todo de conversas entre os users
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Chat conversa = conversas.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);      //might be wrong, last change at 17:40

                intent.putExtra("nome", conversa.getName());
                String email = Base64Custom.decodeBase64(conversa.getIdUser());     //tenho que descodificar o ID para um email legivel para humanos/pessoas normais
                intent.putExtra("email", email);

                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerConversas);
    }

    /*
    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConversas);
    }
     */
}






































































































































































