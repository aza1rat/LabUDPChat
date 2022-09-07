package com.azaratprogram.lab16_chatkashitsin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBChat extends SQLiteOpenHelper {
    public DBChat(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sql = "CREATE TABLE ChatHistory (id INT, name TEXT, message TEXT, ip TEXT, port TEXT, date TEXT);";
        sqlDB.execSQL(sql);

    }

    public int getMaxId()
    {
        String sql = "SELECT MAX(id) FROM  ChatHistory;";
        SQLiteDatabase sqlDB  = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
            return cursor.getInt(0);
        return 0;
    }

    public void addToHistory(int id, ChatMessage history)
    {
        String sql = "INSERT INTO ChatHistory VALUES ("+ id +", '"+ history.name +"', '" + history.message + "', '"+ history.ip +"', '"+ history.port +"', '"+ history.date +"');";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }

    public void clear(ArrayList<ChatMessage> list)
    {
        list.clear();
        String sql = "DELETE FROM ChatHistory;";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }

    public void getAllHistory(ArrayList<ChatMessage> list)//Кашицын,393
    {
        list.clear();
        String sql = "SELECT * FROM ChatHistory;";
        SQLiteDatabase sqlDB = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst() == true)
        {
            do {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.name = cursor.getString(1);
                chatMessage.message = cursor.getString(2);
                chatMessage.ip = cursor.getString(3);
                chatMessage.port = cursor.getString(4);
                chatMessage.date = cursor.getString(5);
                list.add(chatMessage);
            }while (cursor.moveToNext());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
