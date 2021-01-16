package com.ssrprojects.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ssrprojects.mynotes.DataBase.NoteData;
import com.ssrprojects.mynotes.DataBase.NoteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteActivity extends AppCompatActivity {
EditText mTitle, mNote;
TextView mTime;
FloatingActionButton saveNote;
ImageButton saveTitle;
int key, flag = 0, flag2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getSupportActionBar().hide();
        AsyncTaskGetter();

        Intent intent = getIntent();
        key = intent.getIntExtra("KEY", 0);

        mTitle = findViewById(R.id.note_title);
        mNote = findViewById(R.id.note_text);

        saveNote = findViewById(R.id.save);
        saveTitle = findViewById(R.id.title_button);
        saveNote.setVisibility(View.GONE);
        saveTitle.setVisibility(View.GONE);

        saveTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String time = GetTime();
                mTitle.setCursorVisible(false);
                saveTitle.setVisibility(View.GONE);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        NoteDatabase database = NoteDatabase.getInstance(getApplicationContext());
                        if(!mTitle.getText().toString().isEmpty())
                            database.dao().InsertTitle(mTitle.getText().toString(),key);

                        database.dao().InsertDate(time, key);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTime = findViewById(R.id.date_time_text);
                                mTime.setText(time);
                            }
                        });

                    }
                });
            }
        });

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String time = GetTime();
                mNote.setCursorVisible(false);
                saveNote.setVisibility(View.GONE);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        int x;
                        NoteDatabase database = NoteDatabase.getInstance(getApplicationContext());
                        if(!mTitle.getText().toString().isEmpty()) {
                            database.dao().InsertNote(mNote.getText().toString(), key);
                            if(mNote.getText().toString().length()<116)
                                x = mNote.getText().toString().length();
                            else{
                                x = 113;
                            }
                            database.dao().InsertSubTitle(mNote.getText().toString().substring(0, x)+"...", key);
                            database.dao().InsertDate(time, key);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTime = findViewById(R.id.date_time_text);
                                    mTime.setText(time);
                                }
                            });
                        }
                    }
                });
                Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                flag++;
                if(flag>1) {
                    mTitle.setCursorVisible(true);
                    saveTitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mNote.setCursorVisible(true);
                flag2++;
                if(flag2>1) {
                    mTitle.setCursorVisible(true);
                    saveNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NoteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void AsyncTaskGetter(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                NoteDatabase database = NoteDatabase.getInstance(getApplicationContext());
                Log.e("TAG", "run: " + key);
                final NoteData data = database.dao().getNote(key);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        mTitle = findViewById(R.id.note_title);
                        mNote = findViewById(R.id.note_text);
                        mTime = findViewById(R.id.date_time_text);

                        try {
                            if (data.mNote != null)
                                    mNote.setText(data.mNote);
                                if (data.mTitle != null)
                                    mTitle.setText(data.mTitle);
                                mTime.setText(data.mTime);
                        }
                        catch (Exception e){

                        }

                    }
                });
            }
        });
    }

    public String GetTime(){
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(currentTime);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
