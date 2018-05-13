package com.vibeat.vibeatapp.Objects;

public class ServerMsg {
    public MSGType msg_type;
    public boolean bool_info;
    public String str_info;
    public double double_info;
    public int int_info;


    public ServerMsg(MSGType msg_type, boolean bool_info) {
        this.msg_type = msg_type;
        this.bool_info = bool_info;
    }

    public ServerMsg(MSGType msg_type, String str_info) {
        this.msg_type = msg_type;
        this.str_info = str_info;
    }

    public ServerMsg(MSGType msg_type, double double_info) {
        this.msg_type = msg_type;
        this.double_info = double_info;
    }

    public ServerMsg(MSGType msg_type, int int_info) {
        this.msg_type = msg_type;
        this.int_info = int_info;
    }
}
