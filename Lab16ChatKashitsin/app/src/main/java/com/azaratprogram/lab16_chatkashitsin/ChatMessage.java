package com.azaratprogram.lab16_chatkashitsin;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    public String name;
    public String message;
    public String ip;
    public String port;
    public String date;

    public ChatMessage()
    {

    }
    public ChatMessage(String cmName, String cmMessage, InetAddress cmIP, int cmPort, Date cmDate)
    {
        name = cmName;
        message = cmMessage;
        ip = cmIP.toString();
        ip = ip.substring(1, ip.length());
        port = String.valueOf(cmPort);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        date = dateFormat.format(cmDate);
    }
    public String toString()
    {
        return name + "\n" + message + "\n(" + ip + ":" + port + " " + date + ")";

    }
}
