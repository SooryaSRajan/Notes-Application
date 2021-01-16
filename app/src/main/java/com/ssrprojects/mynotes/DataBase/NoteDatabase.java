package com.ssrprojects.mynotes.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = NoteData.class, exportSchema = false, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static final String DB_NAME = "NOTES_DATABASE";
    private static NoteDatabase instance;

    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }
    public abstract DaoMessage dao();
}
