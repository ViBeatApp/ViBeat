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

        Track song0 = new Track(0, "Haverot Shelach", "Omer Adam",
                "http://docs.google.com/uc?/export=download&id=1mNSs3XbUU0cEbJyVFkZ086YKWaSwUex6",
                "http://docs.google.com/uc?/export=download&id=1u4oDSRXhZ5oXc8XWCI92mrbqheHQtzJL");
        Track song1 = new Track(1, "Toy", "Neta Barzilai",
                "https://drive.google.com/open?id=11kWxFcP3X2KKg6daT27E1dcpugXx5tTc",
                "http://docs.google.com/uc?/export=download&id=1Ri_gXnYHt1pdBw1b22uVzEnqzNs0pmxr");
        Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken",
                "https://drive.google.com/open?id=1qRP5htDW7kN9o6QvWV0OAWgZjP62z5X4",
                "http://docs.google.com/uc?/export=download&id=1Q7_Re3eyYNM4D77PlP4OggZn-Q2Vfx1x");
        Track song3 = new Track(3, "Up&Up", "Coldplay",
                "https://drive.google.com/open?id=17uvBcz3tFgHssTQfME7PQlO-8LPoX8kZ",
                "http://docs.google.com/uc?/export=download&id=1dbleMyl_Gy7B3CMIdO370KoNIpf_tRC6");
        Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj",
                "https://drive.google.com/open?id=167Stn2HGFx6U4VM3IDPeM4U8uQW8Bjwn",
                "http://docs.google.com/uc?/export=download&id=1veHdZqYlIrpwcBaWrByxS8Xp4Vg39m_N");
        Track song5 = new Track(5, "My Princess", "Ivri Lider",
                "https://drive.google.com/open?id=1t8NFH2VNbheLTv8aeHLezrVxWSWfNNlU",
                "http://docs.google.com/uc?/export=download&id=1C6XWHYQSrO1sk0mrvzXkMb91OO9eKISx");
        Track song6 = new Track(6, "Lets Dance", "Chen Aharoni",
                "https://drive.google.com/open?id=19n-D8ndRJh60wBif79GgDl_cm7GTwY9t",
                "http://docs.google.com/uc?/export=download&id=1cIc58IqIat5RrafDAqJagaHADkRRvJ_p");
        Track song7 = new Track(7, "Kawaii", "Static and Ben-El",
                "https://drive.google.com/open?id=1ZYuWkYAzxVoL_XBvFwqlqe_JabGGO9YC",
                "http://docs.google.com/uc?/export=download&id=1T7DZuOOKA6sFzjxSUR8bIFeyg-0C2Ybd");
        Track song8 = new Track(8, "These Days", "Rudimental",
                "https://drive.google.com/open?id=1KXhaAfoyVUj1u3erSLCKbFcQNi1fKEDl",
                "http://docs.google.com/uc?/export=download&id=1hZcxMIZbxWjqWE7dnerTWUmo1fFAoTVo");
        Track song9 = new Track(9, "Shnei Meshugaim", "Omer Adam",
                "https://drive.google.com/open?id=1mNSs3XbUU0cEbJyVFkZ086YKWaSwUex6",
                "http://docs.google.com/uc?/export=download&id=1x2XVUtlyBxPRbi4FIPEKio1wN3X0jOzR");
        Track song10 = new Track(10, "I Like It", "Cardi B",
                "https://drive.google.com/open?id=1t-NVn1dQlErVmfAmYyjtDHbG_1riuD93",
                "http://docs.google.com/uc?/export=download&id=1ZYGVAD3a9VAcf8QziMydx6V3kNdShI6H");
        Track song11 = new Track(11, "FRIENDS", "Marshmello and Anne Marie",
                "https://drive.google.com/open?id=13D-T26TjXb9Vfz59U1hw8yohc7C763MB",
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
        parties.add(party1);
    }

    public static Track getTrack(int id){
        return tracks.get(id);
    }

    public static Track getTrackByURL(String url, int track_id){
        for(Track t : tracks){
            if( t.track_path.equals(url) )
                return new Track(track_id,t.title,t.artist,t.img_path,t.track_path);
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
