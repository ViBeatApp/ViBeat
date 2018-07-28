package com.vibeat.vibeatapp.ServerConnection;

import android.util.Log;

import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.ServerSide.Command;
import com.vibeat.vibeatapp.ServerSide.ReadWriteAux;

import org.json.JSONException;

import java.util.LinkedList;


public class SenderThread extends Thread {

    MyApplication app;

    public ReadWriteAux conn;
    public LinkedList<Command> task_queue;
    public Boolean connected;

    public SenderThread(MyApplication app) {
        Log.d("Test7", "Sender builder");
        this.task_queue = new LinkedList<Command>();
        this.connected = new Boolean(true);
        this.app = app;
    }

    @Override
    public void run() {
        try {
            Log.d("Test7", "sender run");
            // conn = new ReadWriteAux("10.0.0.15"); - for locally running the server
            conn = new ReadWriteAux("52.23.168.179");
            if(conn.socket == null) {
                Log.d("Test7", "sender conn = null");
                app.gui_manager.disconnected(false);
                app.semaphoreSender.release();
                Log.d("Test7", "release semaphore sender in sender");
                return;
            }
            Log.d("Test7", "conn succeed");
            app.disconnected = false;
            app.semaphoreSender.release();
            Log.d("Test7", "release semaphore sender in sender");
            Log.d("SenderThread", "after connection ");
            app.listener_thread = new ListenerThread(app, conn);
            app.listener_thread.start();

            while(connected) {
                synchronized (task_queue) {
                    try {
                        if (task_queue.isEmpty())
                            task_queue.wait();
                        while (connected && !task_queue.isEmpty()) {
                            try {
                                Command cmd = task_queue.pop();
                                Log.d("SENDER", cmd.cmd_type.name());
                                if (conn.send(cmd) < 0) {
                                    Log.d("SENDER", "got -1");
                                    app.gui_manager.disconnected(false);
                                    connected = false;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("SENDER","at the end");

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
        app.client_manager.terminateConnection(false);

    }
}
