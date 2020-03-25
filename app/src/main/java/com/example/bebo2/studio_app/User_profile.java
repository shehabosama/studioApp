package com.example.bebo2.studio_app;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_profile extends AppCompatActivity {
    private TextView userName,userStatus,userCountry,userGender;
    private ImageView user_location_profile,user_phone,user_message;
    private DatabaseReference FriendRequestReference,vistRef;
    private String sender_user_id;
    private FirebaseAuth mAuth;
    private String receiver_user_id;
    CircleImageView userProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth= FirebaseAuth.getInstance();
        receiver_user_id =getIntent().getExtras().getString("Visituserid").toString();
        sender_user_id=mAuth.getCurrentUser().getUid();
        vistRef= FirebaseDatabase.getInstance().getReference().child("Users");

        userName=(TextView)findViewById(R.id.user_name_profile);
        userStatus=(TextView)findViewById(R.id.user_status_profile);
        userCountry=(TextView)findViewById(R.id.user_country_profile);
        userGender=(TextView)findViewById(R.id.user_gender_profile);
        user_location_profile = (ImageView)findViewById(R.id.user_location_profile);
        user_phone = (ImageView)findViewById(R.id.image_phone);
        user_message = (ImageView)findViewById(R.id.image_message);
        userProfileImage=(CircleImageView)findViewById(R.id.profile_image);

        user_location_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String location = dataSnapshot.child("location").getValue().toString();

                        Uri uri = Uri.parse("google.navigation:q="+location);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        user_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String phone = dataSnapshot.child("phone_number").getValue().toString();

                        Intent call = new Intent(Intent.ACTION_DIAL);
                        call.setData(Uri.parse("tel:"+phone));
                        startActivity(call);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        user_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String phone_number = dataSnapshot.child("phone_number").getValue().toString();
                        String name = dataSnapshot.child("fullName").getValue().toString();

                        Uri uri = Uri.parse("smsto:" + phone_number);
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        intent.putExtra("sms_body", "Dear :"+name);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


        vistRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userprofilename=dataSnapshot.child("fullName").getValue().toString();
                String userstatus=dataSnapshot.child("status").getValue().toString();
                String usergender=dataSnapshot.child("gender").getValue().toString();
                String usercountry=dataSnapshot.child("country").getValue().toString();
                String phone_number=dataSnapshot.child("phone_number").getValue().toString();
                String profileImage=dataSnapshot.child("profilimage").getValue().toString();
                Resources res = getResources();
                userName.setText(userprofilename);
                userStatus.setText(userstatus);
                userGender.setText(res.getString(R.string.gender)+usergender);
                userCountry.setText(res.getString(R.string.hint_country)+usercountry);

                Picasso.with(getBaseContext()).load(profileImage).into(userProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
