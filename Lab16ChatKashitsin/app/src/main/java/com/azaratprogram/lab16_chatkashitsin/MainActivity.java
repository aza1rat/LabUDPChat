package com.azaratprogram.lab16_chatkashitsin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    ListView chat;
    EditText ipSend;
    EditText portSend;
    EditText message;

    DatagramSocket socket;
    DatagramPacket sendPacket;
    DatagramPacket receivePacket;

    byte[] sendBuffer = new byte[100];
    byte[] receiveBuffer = new byte[100];

    public Settings settings;
    Boolean cycleReceive;
    Thread threadOnSend;
    Runnable receive;
    ArrayList<ChatMessage> arrayList;
    ArrayAdapter<ChatMessage> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//Кашицын,493
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipSend = findViewById(R.id.input_ip);
        portSend = findViewById(R.id.input_portSend);
        chat = findViewById(R.id.lv_chat);
        message = findViewById(R.id.input_message);

        DBMessages.history = new DBChat(this, "chatHistory.db", null, 1);
        DBMessages.settings = new DBSettings(this, "chatSettings.db", null, 1);
        if (DBMessages.settings.getMaxId() == 0)
            DBMessages.settings.addDefault();
        arrayList = new ArrayList<ChatMessage>();
        arrayAdapter = new ArrayAdapter<ChatMessage>(this,android.R.layout.simple_list_item_1, arrayList);
        chat.setAdapter(arrayAdapter);
        DBMessages.history.getAllHistory(arrayList);
        settings = DBMessages.settings.getSettings();
        ipSend.setText(settings.ipSend);
        portSend.setText(settings.portSend);

        ipSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                settings.ipSend = ipSend.getText().toString();
                DBMessages.settings.updateSettings(settings);

            }
        });

        portSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                settings.portSend = portSend.getText().toString();
                DBMessages.settings.updateSettings(settings);
            }
        });

        try{
            InetAddress myNetwork = InetAddress.getByName("0.0.0.0");
            SocketAddress myAdress = new InetSocketAddress(myNetwork, Integer.parseInt(settings.portReceive));
            socket = new DatagramSocket(null);
            socket.bind(myAdress);
            cycleReceive = true;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

         receive = new Runnable() {
            @Override
            public void run() {
                while (cycleReceive)
                {
                    receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);//Кашицын,493
                    try {
                        socket.receive(receivePacket);
                        String receiveMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        String[] messageDeconstr = receiveMessage.split(":", 2);
                        String nameMessage = messageDeconstr[1].substring(0, Integer.parseInt(messageDeconstr[0]));
                        String textMessage = messageDeconstr[1].substring(Integer.parseInt(messageDeconstr[0]), messageDeconstr[1].length());
                        ChatMessage gettedMessage = new ChatMessage(nameMessage, textMessage, receivePacket.getAddress(),
                                receivePacket.getPort(), Calendar.getInstance().getTime());
                        DBMessages.history.addToHistory(DBMessages.history.getMaxId(), gettedMessage);

                        runOnUiThread(() -> {arrayList.add(gettedMessage); arrayAdapter.notifyDataSetChanged();});



                    } catch (IOException e) {
                        e.printStackTrace();
                        }
                    }
                }


        };
        Thread threadOnReceive = new Thread(receive);
        threadOnReceive.start();


    }

    public void SendMessage(View v)//Кашицын,493
    {
        try {
            String nameStr = settings.name;
            String messageStr = message.getText().toString();
            if (nameStr.isEmpty() || messageStr.isEmpty())
            {
                Toast.makeText(getApplicationContext() ,"Вы ничего не ввели", Toast.LENGTH_LONG).show();
                return;
            }
            if (nameStr.length() + messageStr.length() + 3 > 100)
            {
                Toast.makeText(getApplicationContext() ,"Ваше сообщение доставиться не полностью", Toast.LENGTH_LONG).show();
            }

            sendBuffer =  (String.valueOf(nameStr.length()) + ":" + nameStr + messageStr).getBytes();
            InetAddress sendAddress = InetAddress.getByName(ipSend.getText().toString());
            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, sendAddress, Integer.parseInt(portSend.getText().toString()));
        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(getApplicationContext() ,"Произошла ошибка при подготовке к отправке", Toast.LENGTH_SHORT).show();
            return;
        }

        Runnable send = new Runnable() {
            @Override
            public void run() {
                try {
                    socket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {Toast.makeText(getApplicationContext() ,"Произошла ошибка при отправлении", Toast.LENGTH_SHORT).show();});
                    return;
                }
            }
        };
        threadOnSend = new Thread(send);
        threadOnSend.start();

    }

    AlertDialog makeDialog(AlertDialog dlg, String str)//Кашицын,393
    {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //        AlertDialog dlg = builder.create();
        dlg.setTitle(str);
        LayoutInflater linflater = getLayoutInflater();
        View dialogView = linflater.inflate(R.layout.dialog_settings, null);
        EditText name = dialogView.findViewById(R.id.input_name);
        EditText port = dialogView.findViewById(R.id.input_receivePort);
        name.setText(settings.name);
        port.setText(settings.portReceive);
        Button buttonOK = dialogView.findViewById(R.id.but_OK);
        Button buttonCancel = dialogView.findViewById(R.id.but_Cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.cancel();
            }
        });
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(port.getText().toString()) != socket.getPort())
                {
                    socket.close();
                    InetAddress myNetwork = null;
                    try {
                        myNetwork = InetAddress.getByName("0.0.0.0");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    SocketAddress myAdress;
                    try {
                     myAdress = new InetSocketAddress(myNetwork, Integer.parseInt(port.getText().toString()));}
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ошибка при изменении порта", Toast.LENGTH_LONG).show();
                        dlg.cancel();
                        return;
                    }
                    try {
                        socket = new DatagramSocket(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ошибка при изменении порта", Toast.LENGTH_LONG).show();
                        dlg.cancel();
                        return;
                    }
                    try {
                        socket.bind(myAdress);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ошибка при изменении порта", Toast.LENGTH_LONG).show();
                        dlg.cancel();
                        return;
                    }
                }
                settings.name = name.getText().toString();
                settings.portReceive = port.getText().toString();
                DBMessages.settings.updateSettings(settings);
                dlg.cancel();
            }
        });

        dlg.setView(dialogView);
        return  dlg;
    }

    public void onSettingsClick(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
       AlertDialog dlg = builder.create();
       makeDialog(dlg, "Настройки").show();
    }

    public void onCycleClick(View v)
    {
        if (cycleReceive)
        {
            cycleReceive = false;

        }
        else
        {
            cycleReceive = true;
            if (threadOnSend.isAlive() == false)
            {
                threadOnSend = new Thread(receive);
                threadOnSend.start();
            }

        }
    }

    public void onClearClick(View v)
    {
        DBMessages.history.clear(arrayList);
        arrayAdapter.notifyDataSetChanged();
    }

}