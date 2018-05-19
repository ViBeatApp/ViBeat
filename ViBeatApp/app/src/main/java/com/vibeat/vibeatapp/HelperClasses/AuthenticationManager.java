package com.vibeat.vibeatapp.HelperClasses;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.vibeat.vibeatapp.Objects.User;


public class AuthenticationManager {

    public static User getFacebookUser(){
        return new User("Izzy", "/storage/emulated/0/ViBeat/izzy.jpg", 0);
    }

    public static User getGoogleUserFromAccount(GoogleSignInAccount account){
        Uri personPhoto = account.getPhotoUrl();
        String personName = account.getDisplayName();
        String imgPath = personPhoto.toString();
        return new User(personName, imgPath, 0);
    }
    
}
