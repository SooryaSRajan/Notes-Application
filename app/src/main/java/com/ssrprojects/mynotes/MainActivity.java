package com.ssrprojects.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MotionEventCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ssrprojects.mynotes.DataBase.NoteData;
import com.ssrprojects.mynotes.DataBase.NoteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArrayList<HashMap> list, copyList;
    ArrayList<NoteData> noteData;
    NoteAdapter adapter;
    int count = 0;
    String NOTE = "NEW NOTE";
    GridView noteListView;
    Boolean flag = false;
    Toast toast;
    String TAG = "Main Activity";

    @SuppressLint({"ShowToast", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toast = Toast.makeText(this, "No Notes Found", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        list = new ArrayList<>();
        copyList = new ArrayList<>();

        SharedPreferences preferences = this.getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        if(preferences.contains("count")){
            count = preferences.getInt("count", 0);
        }

        noteListView = findViewById(R.id.note_list_view);
        adapter = new NoteAdapter(list, MainActivity.this);
        noteListView.setAdapter(adapter);

        AsyncDatabaseInflater();

        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                flag = true;
                Log.e("Main", "onItemLongClick: " );
                flag = false;
                return false;
            }
        });

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Main", "onItemClick: " + flag);
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("KEY", (Integer) list.get(position).get("KEY"));
                if(!flag)
                startActivity(intent);
            }
        });

        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("Main", "onQueryTextSubmit: " + query );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                list = getResultsForFilter(newText);
                adapter = new NoteAdapter(list, MainActivity.this);
                noteListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        noteListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                editor.putInt("count",count);
                editor.apply();
                AsyncDatabase(count);
            }
        });
    }
    public String GetTime(){
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(currentTime);
    }


    void AsyncDatabase(final int count){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String time = GetTime();
                    NoteDatabase database = NoteDatabase.getInstance(getApplicationContext());
                    NoteData data = new NoteData(count, null, time, null, NOTE);
                    database.dao().InsertMessage(data);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            map.put("TITLE", NOTE);
                            map.put("SUB", "");
                            map.put("DATE", time);
                            map.put("KEY", count);
                            ListViewUpdater(map);
                            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                            intent.putExtra("KEY", count);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                catch (Exception e){
                    Log.e("MainActivityAsyncError", "run: " + e.toString());
                }
            }
        });
    }
    void AsyncDatabaseInflater(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                NoteDatabase database = NoteDatabase.getInstance(getApplicationContext());
                noteData = (ArrayList<NoteData>) database.dao().getAllData();
                if(list != null)
                list.clear();

                for(NoteData data: noteData) {
                    if (data.mSubTitle != null) {
                        HashMap map = new HashMap();
                        map.put("DATE", data.mTime);
                        map.put("TITLE", data.mTitle);
                        map.put("KEY", data.key);
                        map.put("SUB", data.mSubTitle);
                        list.add(map);
                        copyList.add(map);
                    }
                    else {
                       database.dao().deleteNote(data.key);
                    }
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    if(!list.isEmpty()){
                        Collections.sort(copyList, new Comparator<HashMap>() {
                            @Override
                            public int compare(HashMap o1, HashMap o2) {
                                return o1.get("DATE").toString().compareTo(o2.get("DATE").toString());
                            }
                        });


                        Collections.sort(list, new Comparator<HashMap>() {
                            @Override
                            public int compare(HashMap o1, HashMap o2) {
                                return o1.get("DATE").toString().compareTo(o2.get("DATE").toString());
                            }
                        });

                        Collections.reverse(list);
                        Collections.reverse(copyList);

                        adapter = new NoteAdapter(list, MainActivity.this);
                        noteListView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    }
                });
            }
        });
    }

    void ListViewUpdater(HashMap map){
        list.add(map);
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private ArrayList<HashMap> getResultsForFilter(String constraint){
        ArrayList<HashMap> mList = new ArrayList<>();
        Log.e("Main", "getFilteredResults " );
        for(HashMap map : copyList){
            Log.e("Main ", "getFilteredResults: " +  map.get("TITLE").toString());
            if(map.get("TITLE").toString().toLowerCase().contains(constraint.toLowerCase())) {
                mList.add(map);
                Log.e("TAG Main", "getFilteredResults Found" );
            }
        }
        if(mList.size() == 0){
            toast.show();
        }
        return mList;
    }
}
