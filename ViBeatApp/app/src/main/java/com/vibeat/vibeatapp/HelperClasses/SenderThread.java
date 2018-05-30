package com.vibeat.vibeatapp.HelperClasses;

import android.util.Log;

import com.vibeat.vibeatapp.ServerSide.Command;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.ServerSide.ReadWriteAux;

import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedList;


public class SenderThread extends Thread {

    MyApplication app;

    public ReadWriteAux conn;
    public LinkedList<Command> task_queue;
    public boolean connected;

    public SenderThread(MyApplication app) {
        this.task_queue = new LinkedList<Command>();
        this.connected = true;
        this.app = app;
    }

    @Override
    public void run() {
        try {
            Log.d("remark", "choose your own IpAddress ");
            conn = new ReadWriteAux("172.17.172.27");

            app.listener_thread = new ListenerThread(app, conn);
            app.listener_thread.start();

            while(connected) {
                synchronized (task_queue) {
                    try {
                        // Calling wait() will block this thread until another thread
                        // calls notify() on the object.
                        Log.e("SENDER","waiting");
                        task_queue.wait();
                        Log.e("SENDER","got interrupted");
                        while (connected && !task_queue.isEmpty()) {
                            try {
                                Log.e("SENDER","doing pop to send queue");
                                conn.send(task_queue.pop());
                            } catch (JSONException e1) {
                                Log.e("SENDER","send failed");
                                e1.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("SENDER","got interrupted");
                        while (connected && !task_queue.isEmpty()) {
                            try {
                                Log.e("SENDER","doing pop to send queue");
                                conn.send(task_queue.pop());
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCmd(Command cmd){
        synchronized(task_queue) {
            Log.e("SENDER","doing add command synchpnized");
            task_queue.add(cmd);
            if(task_queue.size() >= 1) {
                task_queue.notify();
                Log.e("SENDER","after notify");
            }
        }
    }

    public void logout(){
        synchronized (task_queue){
            this.connected = false;
            task_queue.notify();
        }
    }
}
