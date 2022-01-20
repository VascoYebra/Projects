package com.example.talk2me.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.talk2me.R;
import com.example.talk2me.adapter.TabAdapter;
import com.example.talk2me.config.FirebaseConfig;
import com.example.talk2me.helper.Base64Custom;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.helper.SlidingTabLayout;
import com.example.talk2me.model.Contacto;
import com.example.talk2me.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Toolbar toolbar;                //erro aqui
    private FirebaseAuth userAutenticacao;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String contactIdentifier;
    private DatabaseReference firebaseReference;
    private String contactToRemove;
    //edits:
    private DatabaseReference databaseReferenceVIP;
    private String idUserSender;
    private String nomeUserSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userAutenticacao = FirebaseConfig.getFirebaseAuth();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Talk2Me");
        setSupportActionBar(toolbar);

        slidingTabLayout = findViewById(R.id.stl_tabs);
        viewPager = findViewById(R.id.vp_pagina);

        slidingTabLayout.setDistributeEvenly(true);     //IMPORTANTISSIMO! FICO COM AS TABS DISTRIBUIDAS COM O MESMO TAMANHO

        //configurar adaptador
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager() );
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);

        //Isto e para nao dar crash quando acedo as sharedPreferences para saber para quem mando notificações:
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();          //cria um inflater com ocontexto desta aplicacao, para mostrar menus, desvantagem de mostrar mais do que e suposto ASOPAIOSHOAIHSOUAHS
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.item_sair:
                userLogout();
                return true;
            case R.id.action_settings:
                showSettings();
                return true;
            //funcao de ir para as settings, definicoes, informacoes do Dev...
            case R.id.item_adicionar:
                openRegisterContact();
                return true;
            case R.id.item_removeUser:
                removeUser();
                return true;
            case R.id.goVip:
                putThisUserVIP();
                return true;
            case R.id.goNormal:
                putThisUserNormal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void putThisUserNormal() {
        databaseReferenceVIP = FirebaseDatabase.getInstance().getReference("VIPUsers");
        Preferencias preferencias = new Preferencias(MainActivity.this);
        idUserSender = preferencias.getIndentifier();
        nomeUserSender = preferencias.getNome();
        //Toast.makeText(MainActivity.this, "current User-->"+idUserSender, Toast.LENGTH_LONG ).show();
        databaseReferenceVIP.child(idUserSender).setValue(false);
    }

    private void putThisUserVIP() {
        //TECNICA: Com flags e botoes estaticos nao da pois o onCreate fica sempre por cima de tudo o que eu faça
        //ou seja, tenho que criar mais uma entrada no firebase a dizer se o user e VIP ou não, se for, no Chat Activity ponho esse botao a true
        //tem a vantagem de ser super seguro, e assim, não corro o risco da versão VIP da aplicacao ser lekada na net e perder dinheiro
        //AGORA: Criar no firebase a entrada dos VIPS, com o user actual
        //Caso a entrada não esteja criada, ou nao tenha ninguem, tenho que a criar
        databaseReferenceVIP = FirebaseDatabase.getInstance().getReference("VIPUsers");
        Preferencias preferencias = new Preferencias(MainActivity.this);
        idUserSender = preferencias.getIndentifier();
        nomeUserSender = preferencias.getNome();
        //Toast.makeText(MainActivity.this, "current User-->"+idUserSender, Toast.LENGTH_LONG ).show();
        databaseReferenceVIP.child(idUserSender).setValue(true);

    }


    private void removeUser() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        alertDialog.setTitle("Remove Chat");
        alertDialog.setMessage("user E-mail");
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(MainActivity.this);
        alertDialog.setView(editText);                              //posso adicionar botoes tambem da mesma maneira a uma alertDialog!

        alertDialog.setPositiveButton("Remove the chat from this user", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String contactEmail = editText.getText().toString();
                if(contactEmail.isEmpty()){
                    Toast.makeText(MainActivity.this, "Empty Email address", Toast.LENGTH_LONG );
                }
                else{
                    contactIdentifier = Base64Custom.codeBase64(contactEmail);

                    //fazer getInstance
                    //por enquanto -> dmFzY28uY3J1ejk3QGdtYWlsLmNvbQ== <- esta hardcoded e é o user do vasco, assim remove sempre dos contactos do vasco
                    //sacar o identificador do usser loggado (base64)
                    Preferencias preferencias = new Preferencias(MainActivity.this);
                    String loggedinUserIndentifier = preferencias.getIndentifier();

                    firebaseReference = FirebaseConfig.getFirebase().child("chats").child(loggedinUserIndentifier).child(contactIdentifier);

                    firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue() != null){
                                firebaseReference.removeValue();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "User isnt even Registered...", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    private void openRegisterContact() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        alertDialog.setTitle("New Contact");
        alertDialog.setMessage("user E-mail");
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(MainActivity.this);
        alertDialog.setView(editText);                              //posso adicionar botoes tambem da mesma maneira a uma alertDialog!

        alertDialog.setPositiveButton("Add New Contact", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String contactEmail = editText.getText().toString();

                if(contactEmail.isEmpty()){
                    Toast.makeText(MainActivity.this, "Empty Email address", Toast.LENGTH_LONG );
                }
                else{
                    //Verificar se o contact (email) ja existe no firebase
                    contactIdentifier = Base64Custom.codeBase64(contactEmail);

                    //fazer getInstance
                    firebaseReference = FirebaseConfig.getFirebase().child("users").child(contactIdentifier);

                    firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue() != null){
                                //guardar dados do user que depois vao ser gravados mais a frente
                                User utilizadorContacto = snapshot.getValue(User.class);

                                //sacar o identificador do usser loggado (base64)
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String loggedinUserIndentifier = preferencias.getIndentifier();

                                firebaseReference = FirebaseConfig.getFirebase();
                                firebaseReference = firebaseReference.child("contacts")
                                        .child(loggedinUserIndentifier)
                                        .child(contactIdentifier);

                                Contacto contacto = new Contacto();
                                contacto.setIdentifierUser(contactIdentifier);
                                contacto.setEmail( utilizadorContacto.getEmail() );
                                contacto.setNome( utilizadorContacto.getNome());


                                firebaseReference.setValue(contacto);               //dados do contacto; olhar para a estrutura de baixo!

                                        /*
                                        +contacts
                                            +email@gmail.com        //identificador
                                                +emailFRIEND@gmail.com
                                                    -contactIdentifier
                                                    -email
                                                    -nome
                                                +emailAnotherFRIEND@gmail.com
                                                    -contactIdentifier
                                                    -email
                                                    -nome
                                         */
                            }else{
                                Toast.makeText(MainActivity.this, "User isnt Registered !", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    private void userLogout() {
        userAutenticacao.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}







































































































































































