package com.example.bebo2.studio_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Reservation_accepted_usersActivity extends AppCompatActivity {

    private RecyclerView photographer_list;
    private DatabaseReference FriendsReference,FriendsReference2;
    private DatabaseReference UsersReference;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    String online_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_accept_users);


        photographer_list=(RecyclerView)findViewById(R.id.friends_list);

        mAuth=FirebaseAuth.getInstance();


        online_user_id= mAuth.getCurrentUser().getUid();

        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Reservation").child(online_user_id);
        FriendsReference2= FirebaseDatabase.getInstance().getReference().child("Reservation");
        FriendsReference.keepSynced(true);
        UsersReference=FirebaseDatabase.getInstance().getReference().child("Photographer");

        mToolbar = (Toolbar)findViewById(R.id.reservation_bar) ;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.Reservation_titl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photographer_list.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        photographer_list.setLayoutManager(gridLayoutManager);


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.all_users_display_layout,
                FriendsViewHolder.class,
                FriendsReference
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                final String id_user=getRef(position).getKey();

                viewHolder.setDate(model.getDate());



                final String list_user_id=getRef(position).getKey();
                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        final String name=dataSnapshot.child("fullName").getValue().toString();
                        String thumbImage=dataSnapshot.child("profilimage_photo").getValue().toString();


                        viewHolder.setUserName(name);
                        viewHolder.setThumbImage(thumbImage,getBaseContext());


                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Resources res = getResources();
                                CharSequence option[]=new CharSequence[]{
                                        res.getString(R.string.Delete_reservation_request),
                                        name+" "+res.getString(R.string.s_profile)
                                };

                                AlertDialog.Builder builder =new AlertDialog.Builder(Reservation_accepted_usersActivity.this);
                                builder.setTitle("Select option");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which==0){
                                            FriendsReference2.child(online_user_id).child(list_user_id).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){
                                                                FriendsReference2.child(list_user_id).child(online_user_id).removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if(task.isSuccessful()){
                                                                                    Toast.makeText(Reservation_accepted_usersActivity.this, R.string.Delete_reservation_request, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                        if(which == 1)
                                        {
                                            Intent to_user_profile = new Intent(getApplicationContext(),User_profile.class);
                                            to_user_profile.putExtra("Visituserid",id_user);
                                            startActivity(to_user_profile);
                                        }

                                    }
                                });
                                builder.show();



                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        photographer_list.setAdapter(firebaseRecyclerAdapter);


    }



    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setDate(String date){
            TextView sinceFriendsDate=(TextView)mView.findViewById(R.id.all_users_status);
            sinceFriendsDate.setText( R.string.Book_time+date);
        }

        public  void setUserName(String userName){
            TextView userNameDisplay=(TextView)mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }


        public  void setThumbImage(final String thumbImage, final Context ctx) {
            final CircleImageView thumb_image=(CircleImageView)mView.findViewById(R.id.all_users_profile_image);


            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumbImage).into(thumb_image);


                        }
                    });

        }

    }




}
