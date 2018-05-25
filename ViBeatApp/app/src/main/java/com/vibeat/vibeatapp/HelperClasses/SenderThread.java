package com.vibeat.vibeatapp.HelperClasses;

import com.vibeat.vibeatapp.Command;
import com.vibeat.vibeatapp.readWriteAux;

import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedList;


public class SenderThread extends Thread {

    public readWriteAux conn;
    public LinkedList<Command> task_queue;
    public boolean connected;

    public SenderThread() {
        this.task_queue = new LinkedList<Command>();
        this.connected = true;
    }

    @Override
    public void run() {
        try {
            conn = new readWriteAux();
            while(connected) {
                synchronized (task_queue) {
                    try {
                        // Calling wait() will block this thread until another thread
                        // calls notify() on the object.
                        task_queue.wait();
                    } catch (InterruptedException e) {
                        while (connected && !task_queue.isEmpty()) {
                            try {
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
            task_queue.add(cmd);
            if(task_queue.size() == 1)
                task_queue.notify();
        }
    }

    public void logout(){
        synchronized (task_queue){
            this.connected = false;
            task_queue.notify();
        }
    }
}
