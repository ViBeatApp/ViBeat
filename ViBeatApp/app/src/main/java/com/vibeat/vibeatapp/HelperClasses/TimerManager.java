package com.vibeat.vibeatapp.HelperClasses;

import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TimerManager {

    // based on https://stackoverflow.com/questions/8049912/how-can-i-get-the-network-time-from-the-automatic-setting-called-use-netw/10758365#10758365
    public static long getCurrentNetworkTime() throws UnknownHostException, IOException{
        String TIME_SERVER = "time-a.nist.gov";
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(InetAddress.getByName("52.23.168.179"));
        Log.d("offset_ntp", "timeInfo is null: " + (timeInfo == null));
        long offset_from_ntp = timeInfo.getReturnTime();
        Log.d("offset_ntp", "offset from ntp server" + TIME_SERVER + ": " + offset_from_ntp);
        //long returnTime = timeInfo.getReturnTime();   //local device time
        //long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time
        //Date time = new Date(returnTime);
        return offset_from_ntp;
    }
}
