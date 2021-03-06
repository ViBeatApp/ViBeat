package com.vibeat.vibeatapp.Managers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.HelperClasses.pathNames;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import static com.vibeat.vibeatapp.HelperClasses.pathNames.DB_Publicity;
import static com.vibeat.vibeatapp.HelperClasses.pathNames.DB_Title;
import static com.vibeat.vibeatapp.HelperClasses.pathNames.DB_tracks;

public class FBManager {
    FirebaseStorage storage;
    FirebaseFirestore db;

    public FBManager() {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public List<Track> SearchSongs(String name) {
        final List<Track> result = Collections.synchronizedList( new ArrayList<Track>());
        boolean password = name.startsWith("VibeaTau");
        if(name.startsWith("VibeaTau"))
            name = name.substring(8);
        String[] parts = name.split(" ");
        List<Semaphore> semaphores = new ArrayList<>();


        for(String word : parts) {
            if(word.length() == 0 && parts.length != 1) continue;
            searchByAttribute(DB_Title.name(), word, result,semaphores, password);
            searchByAttribute(pathNames.DB_Artist.name(), word, result,semaphores, password);
        }

        for(Semaphore releaseSemaphore : semaphores) {
            try {
                releaseSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void searchByAttribute(String attribute, String name, final List<Track> result, List<Semaphore> semaphores, final boolean password) {
        final Semaphore semaphore = new Semaphore(0);
        semaphores.add(semaphore);
        Log.d("29/07", "word is " + name);
        CollectionReference tracksRef = db.collection(DB_tracks.name());
        Query query = tracksRef.whereGreaterThanOrEqualTo(attribute,name).whereLessThanOrEqualTo(attribute, name+"z");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if(((boolean)document.getData().get(DB_Publicity.name())) ^ password)
                            continue;
                        String db_id = document.getId();
                        String title = (String) document.getData().get(DB_Title.name());
                        String artist = (String) document.getData().get(pathNames.DB_Artist.name());
                        String img_path = (String) document.getData().get(pathNames.DB_Image_path.name());
                        String track_path = (String) document.getData().get(pathNames.DB_Track_path.name());
                        Track track = new Track(db_id,-1,title,artist,img_path,track_path);
                        addToResult(track,result);
                    }
                } else {
                }
                semaphore.release();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addToResult(Track track,final List<Track> result) {
        synchronized (result){
            if(!isTrackInResult(result, track))
                result.add(track);
        }
    }

    private boolean isTrackInResult(List<Track> result, Track track) {
        for (Track t : result){
            if (t.db_id.equals(track.db_id))
                return true;
        }
        return false;
    }

    public Track getTrackByDBid(String db_id, final int track_id) {
        final Semaphore semaphore = new Semaphore(0);
        final List<Track> track = new ArrayList<>();

        DocumentReference docRef = db.collection(pathNames.DB_tracks.name()).document(db_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String db_id = document.getId();
                        String title = (String) document.getData().get(DB_Title.name());
                        String artist = (String) document.getData().get(pathNames.DB_Artist.name());
                        String img_path = (String) document.getData().get(pathNames.DB_Image_path.name());
                        String track_path = (String) document.getData().get(pathNames.DB_Track_path.name());
                        track.add(new Track(db_id,track_id,title,artist,img_path,track_path));
                        semaphore.release();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("DB", "catch " + db_id + " " + track_id);
        return track.get(0);
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