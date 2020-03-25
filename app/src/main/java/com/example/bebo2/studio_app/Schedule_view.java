package com.example.bebo2.studio_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Schedule_view extends AppCompatActivity {

    private ListView user_schedule_profile;
    private DatabaseReference tableref;
    private String receiver_user_id;
    private ArrayList<String> Userlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_view);
        receiver_user_id = getIntent().getExtras().getString("to_schedule_view_message").toString();
        tableref = FirebaseDatabase.getInstance().getReference().child("Schedule").child(receiver_user_id);


        user_schedule_profile = (ListView)findViewById(R.id.user_schedule_profile);

        tableref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Userlist = new ArrayList<String>();
                        // Result will be holded Here
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Userlist.add(String.valueOf(dsp.getValue())); //add result into array list


                        }

                        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(Schedule_view.this,android.R.layout.simple_dropdown_item_1line,Userlist);
                        user_schedule_profile.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }
}
