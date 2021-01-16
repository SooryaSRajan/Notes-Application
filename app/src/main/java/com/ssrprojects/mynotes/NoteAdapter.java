package com.ssrprojects.mynotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ssrprojects.mynotes.DataBase.NoteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class NoteAdapter extends BaseAdapter {
private ArrayList<HashMap> mapArrayList;
private Activity activity;

    NoteAdapter(ArrayList<HashMap> mapArrayList, Activity activity){
        this.activity = activity;
        this.mapArrayList = mapArrayList;
    }
    @Override
    public int getCount() {
        return mapArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.text_list_view, null);

        ImageButton mButton = convertView.findViewById(R.id.delete_button);
        TextView noteName = convertView.findViewById(R.id.note_name);
        TextView subText = convertView.findViewById(R.id.sub_text);
        TextView date = convertView.findViewById(R.id.date);
        HashMap map = mapArrayList.get(position);

        noteName.setText(map.get("TITLE").toString());
        subText.setText(map.get("SUB").toString());
        date.setText(map.get("DATE").toString());

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Delete Note?")
                        .setMessage("Are you sure you want to delete this note")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: Button");
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NoteDatabase database = NoteDatabase.getInstance(activity);
                                        Log.d(TAG, "run: " + mapArrayList.get(position).get("KEY").toString());
                                        database.dao().deleteNote(Integer.parseInt(mapArrayList.get(position).get("KEY").toString()));
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mapArrayList.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        });

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                builder.show();
            }
        });



        return convertView;
    }
}
