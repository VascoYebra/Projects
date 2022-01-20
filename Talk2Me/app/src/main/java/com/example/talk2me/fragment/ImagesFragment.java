package com.example.talk2me.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talk2me.activity.MainActivity;
import com.example.talk2me.adapter.ImageAdapter;
import com.example.talk2me.config.FirebaseConfig;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import com.example.talk2me.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImagesFragment extends Fragment implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private FirebaseStorage mStorage;

    private ValueEventListener mDBlistener;

    public ImagesFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_images, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //encontrar a barra de loading:
        mProgressCircle = view.findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(getContext(), mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(ImagesFragment.this);

        //o Utilizador só pode ver as imagens correspondentes ao seu próprio nó
        Preferencias preferencias = new Preferencias(getActivity());
        String loggedUserIdentifier = preferencias.getIndentifier();

        mStorage = FirebaseStorage.getInstance();
        // mDatabaseRef =  FirebaseConfig.getFirebase().child("uploads");       //THIS IS WORKING, sort of
        mDatabaseRef =  FirebaseConfig.getFirebase().child("uploads").child(loggedUserIdentifier);     //ISTO DIZ ONDE VOU BUSCAR AS IMAGEMS PARA VER NO FRAGMENTO IMAGES. esta informacao esta na REALTIME Database
        /*
        uploads:
            +vasco.cruz97@gmail.com
                    +imagem
                    +image
                    +imagem
         */
        Log.i(String.valueOf(getContext()), "loggedUserInterface Images Fragment-->"+loggedUserIdentifier);

        mDBlistener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();       //MIGHT GIVE BUG
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());       //gravar a key para este item, preciso disto para depois o apagar
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "Normal click at positio: "+position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());       //fico a saber onde esta o item que quero remover
        Log.i(String.valueOf(getContext()), "URL da imagem que vou remover da storage-> "+imageRef);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
        //Toast.makeText(getContext(), "Delete click at positio: "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDatabaseRef.removeEventListener(mDBlistener);
    }
}
















































































































































