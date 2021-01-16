package com.ssrprojects.mynotes.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DaoMessage {

    @Query("SELECT * FROM NotesTable WHERE `KEY` = :i")
    NoteData getNote(int i);

    @Query("SELECT * FROM NotesTable")
    List<NoteData> getAllData();

    @Insert
    void InsertMessage(NoteData noteData);

    @Query("UPDATE NotesTable SET NOTE = :note WHERE `KEY` = :key")
    void InsertNote(String note, int key);

    @Query("UPDATE NotesTable SET TITLE = :title WHERE `KEY` = :key")
    void InsertTitle(String title, int key);

    @Query("UPDATE NotesTable SET SUBTITLE = :subTitle WHERE `KEY` = :key")
    void InsertSubTitle(String subTitle, int key);

    @Query("UPDATE NotesTable SET TIME = :date WHERE `KEY` = :key")
    void InsertDate(String date, int key);


    @Query("DELETE FROM NotesTable")
    void deleteAll();

    @Query("DELETE FROM NotesTable WHERE `KEY` = :key")
    void deleteNote(int key);
}
