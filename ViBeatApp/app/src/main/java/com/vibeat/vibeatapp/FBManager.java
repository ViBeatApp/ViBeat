package com.vibeat.vibeatapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vibeat.vibeatapp.Objects.Track;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

enum pathNames{
    DB_tracks,   //Info about the track
    DB_Title,
    DB_Artist,
    DB_Image_path,
    DB_Track_path,
    STORAGE_songs,
    STORAGE_images;

}

public class FBManager {
    FirebaseStorage storage;
    FirebaseFirestore db;

    public FBManager() {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public List<Track> SearchSongs(String name) {
        final List<Track> result = Collections.synchronizedList( new ArrayList<Track>());
        name = name.toLowerCase();
        String[] parts = name.split(" ");
        List<Semaphore> semaphores = new ArrayList<>();

        for(String word : parts) {
            Log.d("DB", word);
            searchByAttribute(pathNames.DB_Title.name(), word, result,semaphores);
            searchByAttribute(pathNames.DB_Artist.name(), word, result,semaphores);
        }

        for(Semaphore releaseSemaphore : semaphores) {
            try {
                releaseSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d("DB", "result is list of size: " + result.size());
        return result;
    }

    private void searchByAttribute(String attribute, String name,final List<Track> result,List<Semaphore> semaphores) {
        final Semaphore semaphore = new Semaphore(0);
        semaphores.add(semaphore);

        CollectionReference tracksRef = db.collection(pathNames.DB_tracks.name());
        Query query = tracksRef.whereGreaterThanOrEqualTo(attribute,name).whereLessThanOrEqualTo(attribute, name+"z");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("DB", document.getId() + " => " + document.getData());
                        String title = (String) document.getData().get(pathNames.DB_Title.name());
                        String artist = (String) document.getData().get(pathNames.DB_Artist.name());
                        String img_path = (String) document.getData().get(pathNames.DB_Image_path.name());
                        String track_path = (String) document.getData().get(pathNames.DB_Track_path.name());
                        Track track = new Track(-1,title,artist,img_path,track_path);
                        addToResult(track,result);
                    }
                    Log.d("DB", "Finish searching");
                } else {
                    Log.w("DB", "Error getting documents.", task.getException());
                }
                semaphore.release();
            }
        });
    }

    private void addToResult(Track track,final List<Track> result) {
        synchronized (result){
            for (Track tmpTrack : result){
                if(tmpTrack.track_path.equals(track.img_path))
                    return;
            }
            result.add(track);
        }
    }


    public void addSongToDB(String title,String artist,String imageFilePath,String mp3FilePath){
        Log.d("DB", "path: " + imageFilePath);
        Uri imageFile = Uri.fromFile(new File(imageFilePath));
        Log.d("DB", "uri: " + imageFile.toString());
        Uri Song = Uri.fromFile(new File(mp3FilePath));
        Track track = new Track(-1, title, artist, "", "");
        String fileName = title + " " + artist;
        db.collection("songs").document(fileName).set(track);
        uploadTask("Images",imageFile,fileName);
        uploadTask("Songs",Song,fileName);

    }

    public void uploadTask(final String directory, Uri file, final String newFileName){
        final List<String> result = new ArrayList<>();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(directory + "/" + newFileName);

        UploadTask uploadTask = imageRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("DB", "onFailure: ");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    String urlDownload = (new URL(taskSnapshot.getDownloadUrl().toString())).toString();
                    String attribute = directory.equals("Images") ? "Img_path" : "Track_path";
                    db.collection("Tracks").document(newFileName).update(attribute, urlDownload);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}