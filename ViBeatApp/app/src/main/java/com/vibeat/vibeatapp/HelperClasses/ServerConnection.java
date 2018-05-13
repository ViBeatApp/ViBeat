package com.vibeat.vibeatapp.HelperClasses;

import android.location.Location;

import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;

import java.util.ArrayList;
import java.util.List;

public class ServerConnection {

    //adds the created party to server DB
    public void addNewParty(Party party){    }

    //connect the user to server
    public void connectToServer(User user){    }

    //update party change to server
    public void updateParty(Party party){    }

    //get updated party from server
    public void syncParty(Party party){    }

    //send answer for request to join he party
    public void sendRequestAnswer(Party party, User requested, boolean ans){    }

    //update the saved location of he party admin
    public void updateAdminLocation(Party party, Location new_loc){     }

    //return from server the parties near the location
    public List<Party> getPartiesByLocation(Location location){

        User user1 = new User("Dana Oshri",
                "/storage/emulated/0/ViBeat/dana.jpg", 1);
        User user2 = new User("Idan Cohen",
                "/storage/emulated/0/ViBeat/idan.jpg", 2);
        User user3 = new User("Ido Abulafya",
                "/storage/emulated/0/ViBeat/ido.jpg", 3);
        User user4 = new User("Tomer Solomon",
                "/storage/emulated/0/ViBeat/tomer.jpg", 4);

        Party party0 = new Party(user1, user1.name, true);
        party0.addConnected(user2);
        Party party1 = new Party(user3, user3.name, false);
        party1.addConnected(user4);

        List<Party> parties = new ArrayList<Party>();
        parties.add(party0);
        parties.add(party1);
        return parties;
    }

    //return from server search results from tracks
    public List<Track> getTracksByString(String search_string){
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
        List<Track> songs = new ArrayList<Track>();
        songs.add(song0);
        songs.add(song1);
        songs.add(song2);
        songs.add(song3);
        songs.add(song4);
        return songs;
    }

    public void sendTrackCommand(Party party, int position) { }

    public void sendPlayPauseCommand(Party party) {

    }

    // Send to server that the user wants to leave the party.
    public void sendLeaveParty(){ }

    public void closeConnection(){ }
}
