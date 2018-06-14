package com.vibeat.vibeatapp.HelperClasses;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class TimerManager {

    // based on https://stackoverflow.com/questions/8049912/how-can-i-get-the-network-time-from-the-automatic-setting-called-use-netw/10758365#10758365
    public static final String TIME_SERVER = "time-a.nist.gov";
    public static long getCurrentNetworkTime() throws UnknownHostException, IOException{
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        long offset_from_ntp = timeInfo.getOffset();
        Log.d("offset_ntp", "offset from ntp server" + TIME_SERVER + ": " + offset);
        //long returnTime = timeInfo.getReturnTime();   //local device time
        //long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time
        //Date time = new Date(returnTime);
        return offset_from_ntp;
    }
}
