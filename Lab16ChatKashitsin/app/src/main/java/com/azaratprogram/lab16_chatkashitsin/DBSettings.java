package com.azaratprogram.lab16_chatkashitsin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBSettings extends SQLiteOpenHelper {

    public DBSettings(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sql = "CREATE TABLE SettingsChat (id INT, ipSend TEXT, portReceive TEXT, portSend TEXT, name TEXT);";
        sqlDB.execSQL(sql);
    }
    // INSERT INTO SettingsChat VALUES (1, 'X.X.X.X', '9000', '9000', 'Name');

    public void updateSettings(Settings settings)
    {
        String sql = "UPDATE SettingsChat SET ipSend = '" + settings.ipSend + "', portReceive = '" + settings.portReceive +"'" +
                ", portSend = '" + settings.portSend + "', name = '" + settings.name + "' WHERE id = 1;";
        SQLiteDatabase sqlDBWritable = getWritableDatabase();
        sqlDBWritable.execSQL(sql);
    }

    public void addDefault()
    {
        String sql = "INSERT INTO SettingsChat VALUES (1, 'X.X.X.X', '9000', '9000', 'Name');";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }

    public int getMaxId()
    {
        String sql = "SELECT MAX(id) FROM  SettingsChat;";
        SQLiteDatabase sqlDB  = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
            return cursor.getInt(0);
        return 0;
    }

    public Settings getSettings()
    {
        Settings settings = new Settings();
        String sql = "SELECT * FROM SettingsChat;";
        SQLiteDatabase sqlDB = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst() == true)
        {
            do {

                settings.ipSend = cursor.getString(1);
                settings.portReceive = cursor.getString(2);
                settings.portSend = cursor.getString(3);
                settings.name = cursor.getString(4);
            }while (cursor.moveToNext());
        }
        return settings;
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
