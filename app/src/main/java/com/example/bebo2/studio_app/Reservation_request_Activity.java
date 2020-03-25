package com.example.bebo2.studio_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Reservation_request_Activity extends AppCompatActivity {
    private RecyclerView requests_list;

    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference,UsersReference2;

    private FirebaseAuth mAuth;
    String online_user_id;

    private View myMainView;
    private android.support.v7.widget.Toolbar mToolbar;

    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_request);
        requests_list=(RecyclerView)findViewById(R.id.Request_list);

        mAuth= FirebaseAuth.getInstance();


        online_user_id= mAuth.getCurrentUser().getUid();

        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Reservation_Request").child(online_user_id);

        UsersReference=FirebaseDatabase.getInstance().getReference().child("Users");
        UsersReference2=FirebaseDatabase.getInstance().getReference().child("Photographer");
        FriendsDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Reservation");
        FriendsReqDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Reservation_Request");

        mToolbar = (Toolbar)findViewById(R.id.tool_reservation_accept) ;
        setSupportActionBar(mToolbar);
        requests_list.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);



        requests_list.setLayoutManager(gridLayoutManager);

    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends, ChatsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, ChatsViewHolder>(
                Friends.class,
                R.layout.all_users_display_layout,
                Reservation_request_Activity.ChatsViewHolder.class,
                FriendsReference
        ) {
            @Override
            protected void populateViewHolder(final Reservation_request_Activity.ChatsViewHolder viewHolder, Friends model, int position) {

                final String list_user_id = getRef(position).getKey();

                DatabaseReference get_type_ref=getRef(position).child("request_type").getRef();


                final DatabaseReference get_date=getRef(position).child("date").getRef();


                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            String request_type=dataSnapshot.getValue().toString();
                            if (request_type.equals("received"))
                            {

                                final String[] date = {""};
                               get_date.addValueEventListener(new ValueEventListener() {

                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                      if(dataSnapshot.exists())
                                      {

                                           date[0] =dataSnapshot.getValue().toString();
                                      }
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               }) ;



                                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {


                                        final String name = dataSnapshot.child("fullName").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("profilimage").getValue().toString();



                                        viewHolder.setUserName(name);
                                        viewHolder.setThumbImage(thumbImage,getBaseContext());

                                        viewHolder.setUserStatus( R.string.Book_time+date[0]);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Resources res = getResources();
                                                CharSequence option[]=new CharSequence[]{
                                                        res.getString(R.string.Accept_request),
                                                        res.getString(R.string.cancel_reservation)
                                                };

                                                AlertDialog.Builder builder =new AlertDialog.Builder(Reservation_request_Activity.this);
                                                builder.setTitle(R.string.Friend_request_option);
                                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if(which==0){



                                                            FriendsDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(date[0])
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            FriendsDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(date[0])
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful()){
                                                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                if (task.isSuccessful()){
                                                                                                                                    Toast.makeText(getBaseContext(), R.string.request_accepted, Toast.LENGTH_SHORT).show();
                                                                                                                            }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }


                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });



                                                        }
                                                        if(which==1){
                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                if (task.isSuccessful()){
                                                                                                    Toast.makeText(getBaseContext(), R.string.request_accepted, Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }


                                                                        }
                                                                    });
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


                            }else if(request_type.equals("sent")){


                                final String[] date = {""};
                                get_date.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {

                                            date[0] =dataSnapshot.getValue().toString();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }) ;
                                UsersReference2.child(list_user_id).addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {

                                        final String name = dataSnapshot.child("fullName").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("profilimage_photo").getValue().toString();

                                        viewHolder.setUserName(name);
                                        viewHolder.setThumbImage(thumbImage, Reservation_request_Activity.this);
                                        viewHolder.setUserStatus(date[0]);
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                Resources res = getResources();
                                                CharSequence option[]=new CharSequence[]{
                                                        res.getString(R.string.cancel_reservation),

                                                };

                                                AlertDialog.Builder builder =new AlertDialog.Builder(Reservation_request_Activity.this);
                                                builder.setTitle(R.string.Friend_request_option);
                                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if(which==0){
                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                if (task.isSuccessful()){
                                                                                                    Toast.makeText(getBaseContext(), R.string.request_canceled, Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }


                                                                        }
                                                                    });
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

                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        requests_list.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }



        public  void setUserName(String userName){
            TextView userNameDisplay=(TextView)mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }


        public  void setThumbImage(final String thumbImage, final Context ctx) {
            final CircleImageView thumb_image=(CircleImageView)mView.findViewById(R.id.all_users_profile_image);


        Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.backgroundprof).into(thumb_image);




        }



        public void setUserStatus(String userStatus) {

            TextView user_status=(TextView)mView.findViewById(R.id.all_users_status);
            user_status.setText(userStatus);
        }
    }

}
