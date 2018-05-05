package com.vibeat.vibeatapp.HelperClasses;

import com.vibeat.vibeatapp.Objects.User;

public class AuthenticationManager {

    public static User getFacebookUser(){
        return new User("Izzy", "/storage/emulated/0/ViBeat/izzy.jpg", 0);
    }

    public static User getGoogleUser(){ return null; }
    
}
