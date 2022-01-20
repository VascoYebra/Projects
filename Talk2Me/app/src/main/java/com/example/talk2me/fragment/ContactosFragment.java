package com.example.talk2me.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.talk2me.activity.ChatActivity;
import com.example.talk2me.activity.MainActivity;
import com.example.talk2me.adapter.ContactAdapter;
import com.example.talk2me.config.FirebaseConfig;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.Contacto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Objects;

import com.example.talk2me.R;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contacto> contactos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContacts;

    public ContactosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //Instânciar objetos
        contactos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contactos, container, false);

        //Monta listview e adapter
        listView = view.findViewById(R.id.lv_contacts);
        //este adapter e para visualizar! com este adaptador standard so consigo ver o nome...mas quero mostrar o email também!
        /*adapter = new ArrayAdapter(
                getContext(),
                R.layout.list_contacts,     //layout custom meu
                contactos
        );*/
        adapter = new ContactAdapter(getActivity(), contactos);

        listView.setAdapter( adapter );

        Preferencias preferencias = new Preferencias(getActivity());
        String loggedUserIdentifier = preferencias.getIndentifier();
        Log.i(String.valueOf(this), "getIdentifier ContactosFragment---> "+loggedUserIdentifier);
        Bundle extras = getActivity().getIntent().getExtras();
        String hopeThereEmailHere = extras.getString("emailJustInCase");
        Log.i(String.valueOf(this), "emailJustInCase do Get intent (hopeThereEmailHere)--->"+hopeThereEmailHere);
        firebase = FirebaseConfig.getFirebase().child("contacts").child(hopeThereEmailHere);      //recuperar os contactos deste utilizador!
        //MIGHT BE BUGGY! ALGUMA CENA TROCAR hopeThereEmailHere POR loggedUserIdentifier
        //nao te esquecas que cada user na parte dos contacts da firebase tem outros users, mas que serão lidos como contactos!
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {          //se um utilziador adicionar um contacto, a lista no firebase e automaticamente actualizada porque este dataChange e chamado
                contactos.clear();  //tenho que começar do zero sempre que começe para nao acumular contactos....ou seja ficar o 1, depois o 1 e o 2, depois 1, 2 e 3, etc.....
                for(DataSnapshot data: snapshot.getChildren()){
                    Contacto contacto = data.getValue(Contacto.class);          //preciso de passar o Contacto, para ficar com um objecto do tipo Contacto! mais facil de ler/manipular
                    contactos.add(contacto);
                }
                adapter.notifyDataSetChanged();     //avisar que os dados mudaram
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //quando carregar num item da lista, vou automaticamente para a activity chat
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //sacar o nome e email:
                Contacto contacto = contactos.get(position);

                //guardar nome e email na intent para depois os ir buscar e fazer display do nome no topo do nome da conversa
                intent.putExtra("nome", contacto.getNome() );
                intent.putExtra("email", contacto.getEmail() );
                startActivity(intent);
            }
        });

        return view;

    }

}



































