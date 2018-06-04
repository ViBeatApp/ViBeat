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
import java.util.List;

public class FBManager {
    FirebaseStorage storage;
    FirebaseFirestore db;

    public FBManager() {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public List<Track> SearchSongs(String name) {
        final List<Track> result = new ArrayList<>();
        name = name.toLowerCase();
        CollectionReference songs = db.collection("Tracks");
        Query query = songs.whereGreaterThanOrEqualTo("Title",name).whereLessThanOrEqualTo("Title", name+"z");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("DB", document.getId() + " => " + document.getData());
                                String title = (String) document.getData().get("Title");
                                String artist = (String) document.getData().get("Artist");
                                String img_path = (String) document.getData().get("Img_path");
                                String track_path = (String) document.getData().get("Track_path");
                                Track track = new Track(-1,title,artist,img_path,track_path);
                                result.add(track);
                            }
                            Log.d("DB", "Finish searching");
                        } else {
                            Log.w("DB", "Error getting documents.", task.getException());
                        }
                    }
                });

        query = songs.whereGreaterThanOrEqualTo("Artist",name).whereLessThanOrEqualTo("Artist", name+"z");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("DB", document.getId() + " => " + document.getData());
                        String title = (String) document.getData().get("Title");
                        String artist = (String) document.getData().get("Artist");
                        String img_path = (String) document.getData().get("Img_path");
                        String track_path = (String) document.getData().get("Track_path");
                        Track track = new Track(-1,title,artist,img_path,track_path);
                        result.add(track);
                    }
                    Log.d("DB", "Finish searching");
                } else {
                    Log.w("DB", "Error getting documents.", task.getException());
                }
            }
        });
        return result;
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