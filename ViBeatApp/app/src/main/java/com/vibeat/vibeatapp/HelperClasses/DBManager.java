package com.vibeat.vibeatapp.HelperClasses;

import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    static List<Track> tracks = new ArrayList<Track>();
    static List<User> users = new ArrayList<User>();
    static List<Party> parties = new ArrayList<Party>();


    public DBManager(){

        Track song0 = new Track(0, "Haverot Shelach", "Omer Adam",
                "/storage/emulated/0/ViBeat/omeradam.jpg",
                //"https://dl.dropboxusercontent.com/s/dy01wssr2nde32w/Omer%20Adam%20-%20Haverot%20Shelach.mp3?dl=0");
                "http://docs.google.com/uc?/export=download&id=12zPd5TK2k7gDRA2UN0qdPJe1YpPApO1R");
        Track song1 = new Track(1, "Toy", "Neta Barzilai",
                "/storage/emulated/0/ViBeat/netabrazilai.jpg",
                //"https://dl.dropboxusercontent.com/s/u8vi8erzyaism40/Netta%20-%20TOY.mp3?dl=0");
                "http://docs.google.com/uc?/export=download&id=1Ri_gXnYHt1pdBw1b22uVzEnqzNs0pmxr");
        //"http://docs.google.com/uc?/export=download&id=1E5H8Omn7FR1SeCsVRlQjJpGHKBl_2E-r");
        //"/storage/emulated/0/Music/run/The Weeknd - I Feel It Coming ft. Daft Punk.mp3");
        Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken",
                "/storage/emulated/0/ViBeat/edenbenzaken.jpg",
                //"https://dl.dropboxusercontent.com/s/usk3pksu8q6ergo/Eden%20Ben%20Zaken%20-%20Raziti.mp3?dl=0");
                "http://docs.google.com/uc?/export=download&id=1ExMWlL3JzOQXIJu3LIuqs5xdb4M8aUfA");
        Track song3 = new Track(3, "Up&Up", "Coldplay",
                "/storage/emulated/0/ViBeat/coldplay.jpg",
                //"https://dl.dropboxusercontent.com/s/xwgtk1z63ekywyw/Coldplay%20-%20Up%26Up.mp3?dl=0");
                "http://docs.google.com/uc?/export=download&id=1lrsUe4_E5Qo9k1FHaEwSdR9n1KguftAY");
        Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj",
                "/storage/emulated/0/ViBeat/nadavguedj.jpg",
                //"https://dl.dropboxusercontent.com/s/46jr104iseveztk/Nadav%20Guedj%20-%20Ulay%20Nedaber.mp3?dl=0");
                "http://docs.google.com/uc?/export=download&id=1o58CR7s4h36qBwWhkoXi9OSQRuyZaYR7");
        tracks.add(song0);
        tracks.add(song1);
        tracks.add(song2);
        tracks.add(song3);
        tracks.add(song4);

        User user1 = new User("Dana Oshri",
                "/storage/emulated/0/ViBeat/dana.jpg", 1);
        User user2 = new User("Idan Cohen",
                "/storage/emulated/0/ViBeat/idan.jpg", 2);
        User user3 = new User("Ido Abulafya",
                "/storage/emulated/0/ViBeat/ido.jpg", 3);
        User user4 = new User("Tomer Solomon",
                "/storage/emulated/0/ViBeat/tomer.jpg", 4);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);


        Party party0 = new Party(user1, user1.name, true, 0);
        party0.addConnected(user2);
        Party party1 = new Party(user3, user3.name, false, 1);
        party1.addConnected(user4);

        parties.add(party0);
        parties.add(party1);
    }

    public static Track getTrack(int id){
        return tracks.get(id);
    }

    public static User getUser(int id){
        return users.get(id);
    }

    public static Party getParty(int id){
        return parties.get(id);
    }

    public static List<Track> getTracksByString(String str){
        return tracks;
    }
}
