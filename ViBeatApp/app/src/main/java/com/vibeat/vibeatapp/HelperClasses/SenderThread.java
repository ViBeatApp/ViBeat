package com.vibeat.vibeatapp.HelperClasses;

import android.util.Log;

import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.ServerSide.Command;
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
            conn = new ReadWriteAux("52.23.168.179");

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
                                    app.gui_manager.disconnected();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("SENDER","at the end");
        /*if (app.listener_thread != null) {
            app.listener_thread.interrupt();
            try {
                app.listener_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

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
