package com.vibeat.vibeatapp.Managers;

import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    static List<Track> tracks = new ArrayList<Track>();
    static List<User> users = new ArrayList<User>();
    static List<Party> parties = new ArrayList<Party>();


    public static void startDBManager(){
/*
        Track song0 = new Track(0, "Haverot Shelach", "Omer Adam",
                "https://firebasestorage.googleapis.com/v0/b/vibeatapp-2eb70.appspot.com/o/STORAGE_images%2Fomeradam.jpg?alt=media&token=975a433d-a122-4472-9dcf-4c1a9e3d92b1"
                "https://firebasestorage.googleapis.com/v0/b/vibeatapp-2eb70.appspot.com/o/STORAGE_songs%2F%D7%A2%D7%95%D7%9E%D7%A8%20%D7%90%D7%93%D7%9D%20-%20%D7%97%D7%91%D7%A8%D7%95%D7%AA%20%D7%A9%D7%9C%D7%9A.mp3?alt=media&token=312004ec-a155-4ddb-bdd1-9ab48e33223c");
        Track song1 = new Track(1, "Toy", "Neta Barzilai",
                "https://dl.dropboxusercontent.com/s/l05zkicj3pbnvav/netabrazilai.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=1Ri_gXnYHt1pdBw1b22uVzEnqzNs0pmxr");
        Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken",
                "https://dl.dropboxusercontent.com/s/vfm22xz740qtd07/edenbenzaken.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=1Q7_Re3eyYNM4D77PlP4OggZn-Q2Vfx1x");
        Track song3 = new Track(3, "Up&Up", "Coldplay",
                "https://dl.dropboxusercontent.com/s/xvhxrx8pt2t6nqe/coldplay1.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=1dbleMyl_Gy7B3CMIdO370KoNIpf_tRC6");
        Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj",
                "https://dl.dropboxusercontent.com/s/s42ecqyert04ybl/nadavguedj.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=1veHdZqYlIrpwcBaWrByxS8Xp4Vg39m_N");
        Track song5 = new Track(5, "My Princess", "Ivri Lider",
                "https://dl.dropboxusercontent.com/s/lvmfga6grymwcj5/ivri_lider.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=1C6XWHYQSrO1sk0mrvzXkMb91OO9eKISx");
        Track song6 = new Track(6, "Lets Dance", "Chen Aharoni",
                "https://dl.dropboxusercontent.com/s/lnx62j7fi5tcor4/chenaharoni.png?dl=0",
                "http://docs.google.com/uc?/export=download&id=1cIc58IqIat5RrafDAqJagaHADkRRvJ_p");
        Track song7 = new Track(7, "Kawaii", "Static and Ben-El",
                "https://dl.dropboxusercontent.com/s/jrzj4b7c2hwgx6d/static%26benel.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=1T7DZuOOKA6sFzjxSUR8bIFeyg-0C2Ybd");
        Track song8 = new Track(8, "These Days", "Rudimental",
                "https://dl.dropboxusercontent.com/s/l28ro1vr81s0r9i/rudimental.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=1hZcxMIZbxWjqWE7dnerTWUmo1fFAoTVo");
        Track song9 = new Track(9, "Shnei Meshugaim", "Omer Adam",
                "https://dl.dropboxusercontent.com/open?id=1mNSs3XbUU0cEbJyVFkZ086YKWaSwUex6",
                "http://docs.google.com/uc?/export=download&id=1x2XVUtlyBxPRbi4FIPEKio1wN3X0jOzR");
        Track song10 = new Track(10, "I Like It", "Cardi B",
                "https://dl.dropboxusercontent.com/s/l4vhxz41oukxmen/cardib.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=1ZYGVAD3a9VAcf8QziMydx6V3kNdShI6H");
        Track song11 = new Track(11, "FRIENDS", "Marshmello and Anne Marie",
                "https://dl.dropboxusercontent.com/s/ctvagcxtm8ejheq/marshmelloannemarie.jpg?dl=0",
                "http://docs.google.com/uc?/export=download&id=18An6iRrChsRmY5d0bcjHp5fmKRQOOhEU");
        tracks.add(song0);
        tracks.add(song1);
        tracks.add(song2);
        tracks.add(song3);
        tracks.add(song4);
        tracks.add(song5);
        tracks.add(song6);
        tracks.add(song7);
        tracks.add(song8);
        tracks.add(song9);
        tracks.add(song10);
        tracks.add(song11);

        User user1 = new User("Dana Oshri",
                "/storage/emulated/0/ViBeat/dana.jpg", 1, false);
        User user2 = new User("Idan Cohen",
                "/storage/emulated/0/ViBeat/idan.jpg", 2,false);
        User user3 = new User("Ido Abulafya",
                "/storage/emulated/0/ViBeat/ido.jpg", 3,false);
        User user4 = new User("Tomer Solomon",
                "/storage/emulated/0/ViBeat/tomer.jpg", 4,false);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);


        Party party0 = new Party(user1, user1.name, true, 0);
        party0.addConnected(user2);
        Party party1 = new Party(user3, user3.name, false, 1);
        party1.addConnected(user4);

        parties.add(party0);
        parties.add(party1);*/
    }

    public static Track getTrack(int id){
        return tracks.get(id);
    }

    public static Track getTrackByURL(String url, int track_id){
        for(Track t : tracks){
            if( t.track_path.equals(url) )
                return new Track(null,track_id,t.title,t.artist,t.img_path,t.track_path);
        }
        return null;
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
