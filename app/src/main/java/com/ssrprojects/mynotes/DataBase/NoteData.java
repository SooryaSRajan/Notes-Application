package com.ssrprojects.mynotes.DataBase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "NotesTable")
public class NoteData {

    @PrimaryKey
    @NonNull
    @ColumnInfo (name = "KEY")
    public int key;

    @ColumnInfo (name = "TIME")
    public String mTime;

    @ColumnInfo (name = "NOTE")
    public String mNote;

    @ColumnInfo (name = "TITLE")
    public String mTitle;

    @ColumnInfo (name = "SUBTITLE")
    public String mSubTitle;

    public NoteData(@NonNull int key, String mNote, String mTime, String mSubTitle, String mTitle){
        this.key = key;
        this.mTime = mTime;
        this.mNote = mNote;
        this.mTitle = mTitle;
        this.mSubTitle = mSubTitle;
    }




}
