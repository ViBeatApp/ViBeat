package com.vibeat.vibeatapp.Managers;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.vibeat.vibeatapp.Objects.User;


public class AuthenticationManager {

    public static User getGoogleUserFromAccount(GoogleSignInAccount account){
        Uri personPhoto = account.getPhotoUrl();
        String personName = account.getDisplayName();
        Log.d("ImagePath", personPhoto.toString());
        return new User(personName, personPhoto.toString(), account.getId().hashCode(),false);
    }
    
}
