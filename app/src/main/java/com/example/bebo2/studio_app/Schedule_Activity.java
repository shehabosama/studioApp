package com.example.bebo2.studio_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Schedule_Activity extends AppCompatActivity {

    private DatabaseReference scheduleref;
    private Button  btn_schedule,edt_description;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private Calendar myCalendar;
    private String date;
    private ArrayList<String> Userlist;
    private ArrayList<String> key_list;
    private ListView user_schedule_profile;
    private ArrayAdapter<String> adapter;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        scheduleref = FirebaseDatabase.getInstance().getReference().child("Schedule").child(current_user_id);

        btn_schedule = (Button)findViewById(R.id.btn_schedule);
        edt_description = (Button)findViewById(R.id.desc_schedule);
        user_schedule_profile=(ListView)findViewById(R.id.user_schedule_profile);

        mToolbar = (Toolbar)findViewById(R.id.tool_schedule) ;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateHiring();
            }

        };


        edt_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Schedule_Activity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btn_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = edt_description.getText().toString();
                set_schedule(description);
                adapter.notifyDataSetChanged();

                Intent intent = new Intent(getApplicationContext(),Schedule_Activity.class);
                startActivity(intent);
            }
        });

        scheduleref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Userlist = new ArrayList<String>();
                        key_list = new ArrayList<String>();
                        // Result will be holded Here
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Userlist.add(String.valueOf(dsp.getValue())); //add result into array list
                            key_list.add(dsp.getKey());


                        }

                        adapter  = new ArrayAdapter<String>(Schedule_Activity.this,android.R.layout.simple_dropdown_item_1line,Userlist);
                        user_schedule_profile.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


        user_schedule_profile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                DatabaseReference myRef;
                myRef = FirebaseDatabase.getInstance().getReference();
                myRef.getRoot().child("Schedule").child(current_user_id).child(key_list.get(position)).removeValue();
                key_list.remove(position);

                Userlist.remove(position);
                adapter.notifyDataSetChanged();

            }
        });

    }
    private void updateHiring()
    {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date = sdf.format(myCalendar.getTime());
        edt_description.setText(sdf.format(myCalendar.getTime()));
        adapter.notifyDataSetChanged();
    }
    public void set_schedule(String description)
    {
        scheduleref.push().setValue(description).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(Schedule_Activity.this, "the schedule is added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
