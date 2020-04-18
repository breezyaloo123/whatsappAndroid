package com.example.whatsappandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MessageHelper extends SQLiteOpenHelper {
    public MessageHelper(Context context)
    {
        super(context,Message.DB_NAME,null,Message.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//Request for the creation of the local database
        String createTable=" CREATE TABLE " + Message.TABLE + "( " + Message._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Message.USERNAME + " TEXT, "+ Message.STATUS +
         " TEXT, "+Message.EMAIL + " TEXT, "+ Message.PASSWORD + " TEXT, "+ Message.MESSAGE+ " TEXT, " +
        Message.DATE + " TEXT, "+ Message.TIME+ " TEXT, "+ Message.GROUPS+ " TEXT);";

        String createGroup = " CREATE TABLE " + Message.TABLE1 + "( " + Message._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Message.GROUPS + " TEXT" +")";
        //Execute the request
        db.execSQL(createTable);
        db.execSQL(createGroup);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        String dropTable ="DROP TABLE "+Message.TABLE;
        String group = "DROP TABLE "+Message.TABLE1;

        sqLiteDatabase.execSQL(dropTable);
        sqLiteDatabase.execSQL(group);
        onCreate(sqLiteDatabase);


    }
}
