package com.example.talk2me.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.talk2me.fragment.AudioFragment;
import com.example.talk2me.fragment.ChatsFragment;
import com.example.talk2me.fragment.ContactosFragment;
import com.example.talk2me.fragment.ImagesFragment;

public class TabAdapter extends FragmentStatePagerAdapter {


    private String[] tituloTabs = {"CHATS", "CONTACTS", "IMAGES", "AUDIO"};
    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position){
            case 0:                     //dependendo da posição onde estiver, faço um outro fragmento
                fragment = new ChatsFragment();
                break;
            case 1:
                fragment = new ContactosFragment();
                break;
            case 2:
                fragment = new ImagesFragment();
                break;
            case 3:
                fragment = new AudioFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tituloTabs.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tituloTabs[position];

    }
}









































