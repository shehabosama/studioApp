package com.example.bebo2.studio_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView postdescription;
    private Button editePostbutton,deletePostbutton;
    private DatabaseReference postRefe;
    private FirebaseAuth mAuth;
    private String current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        String postid=getIntent().getExtras().get("postkey").toString();
        postRefe=FirebaseDatabase.getInstance().getReference().child("Posts").child(postid);
        mAuth=FirebaseAuth.getInstance();

        current_user_id=mAuth.getCurrentUser().getUid();
        postImage=(ImageView)findViewById(R.id.post_image);
        postdescription=(TextView)findViewById(R.id.post_description);
        editePostbutton=(Button)findViewById(R.id.edite_post_button);
        deletePostbutton=(Button)findViewById(R.id.delete_post_button);



        editePostbutton.setVisibility(View.INVISIBLE);
        deletePostbutton.setVisibility(View.INVISIBLE);


        postRefe.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.exists()) {
                    String post_image = dataSnapshot.child("postimage").getValue().toString();
                    final String descriptionpost = dataSnapshot.child("description").getValue().toString();
                    String uid = dataSnapshot.child("uid").getValue().toString();

                    Picasso.with(getBaseContext()).load(post_image).into(postImage);



                    postdescription.setText(descriptionpost);

                    if (current_user_id.equals(uid)) {
                        editePostbutton.setVisibility(View.VISIBLE);
                        deletePostbutton.setVisibility(View.VISIBLE);
                    }
                    editePostbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentpost(descriptionpost);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        deletePostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletepost();
            }
        });



    }

    private void EditCurrentpost(String descriptionpost)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit post");
        final EditText posttext=new EditText(ClickPostActivity.this);
        posttext.setText(descriptionpost);
        builder.setView(posttext);

        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postRefe.child("description").setValue(posttext.getText().toString());
                Toast.makeText(ClickPostActivity.this, "update post successfully..", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        Dialog dialog=builder.create();

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);
    }


    private void deletepost()
    {

        postRefe.removeValue();

        Toast.makeText(this, R.string.deleted_Successfully, Toast.LENGTH_SHORT).show();

        SendusertoMainActivity();
    }

    private void SendusertoMainActivity()
    {
        Intent MainActivityIntent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(MainActivityIntent);

    }
}
