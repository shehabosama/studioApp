package com.example.bebo2.studio_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivty_for_photographer extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mtoolbar;
    private CircleImageView NavprofileImage;
    private TextView Navusername;
    private FirebaseAuth mAuth;
    private DatabaseReference Userref,Photoref;
    private String currentuserid;
    private ProgressDialog Loginbar;
    private String type;
    private String DEFAULT = "DN";
    private RecyclerView requests_list;

    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference,UsersReference2,NotificationReference;

    String online_user_id;

    private View myMainView;

    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activty_for_photographer);
        mAuth = FirebaseAuth.getInstance();
        online_user_id= mAuth.getCurrentUser().getUid();
        Userref= FirebaseDatabase.getInstance().getReference().child("Users");
        Photoref= FirebaseDatabase.getInstance().getReference().child("Photographer");
        currentuserid = mAuth.getCurrentUser().getUid();
        UsersReference=FirebaseDatabase.getInstance().getReference().child("Users");
        UsersReference2=FirebaseDatabase.getInstance().getReference().child("Photographer");
        FriendsDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Reservation");
        FriendsReqDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Reservation_Request");
        NotificationReference=FirebaseDatabase.getInstance().getReference().child("Notification_two");
        String div_token = FirebaseInstanceId.getInstance().getToken();
        Photoref.child(online_user_id).child("device_token").setValue(div_token);
        mtoolbar=(Toolbar)findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(R.string.home);
        mtoolbar.setTitleTextColor(getResources().getColor(android.R.color.white));


        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer);


        actionBarDrawerToggle= new ActionBarDrawerToggle(MainActivty_for_photographer.this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView=(NavigationView)findViewById(R.id.nav);

        View nav_view=navigationView.inflateHeaderView(R.layout.header);


        NavprofileImage=(CircleImageView)nav_view.findViewById(R.id.nav_profile_image);
        Navusername=(TextView)nav_view.findViewById(R.id.nav_user_full_name);
        requests_list=(RecyclerView)findViewById(R.id.all_users_post_list);

        Loginbar=new ProgressDialog(this);

        Loginbar.setTitle(R.string.login_the_account);
        Loginbar.show();
        Loginbar.setCanceledOnTouchOutside(false);

        SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);

        type = sharedPreferences.getString("kind_person", DEFAULT);


        if (type.equals(DEFAULT)) {
            Toast.makeText(MainActivty_for_photographer.this, R.string.not_found, Toast.LENGTH_SHORT).show();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                UserMenuSelector(item);
                return false;
            }
        });

        check_user(type);



        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Reservation_Request").child(currentuserid);
        FriendsDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Reservation");
        FriendsReqDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Reservation_Request");

        requests_list.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        requests_list.setLayoutManager(gridLayoutManager);

        FirebaseMessaging.getInstance().subscribeToTopic("test");

        FirebaseInstanceId.getInstance().getToken();

        Intent serviceIntent = new Intent(this, ExampleService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Friends, Reservation_request_Activity.ChatsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, Reservation_request_Activity.ChatsViewHolder>(
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

                                        viewHolder.setUserStatus("Booking date: "+date[0]);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Resources res = getResources();
                                                CharSequence option[]=new CharSequence[]{
                                                        res.getString(R.string.Accept_request),
                                                        res.getString(R.string.cancel_reservation)
                                                };

                                                AlertDialog.Builder builder =new AlertDialog.Builder(MainActivty_for_photographer.this);
                                                builder.setTitle(R.string.Select_option);
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
                                                                                                                                    HashMap<String,String> notification=new HashMap<String, String>();
                                                                                                                                    notification.put("from",currentuserid);
                                                                                                                                    notification.put("type","request");

                                                                                                                                    NotificationReference.child(list_user_id).push().setValue(notification)
                                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                    if(task.isSuccessful()){


                                                                                                                                                    }

                                                                                                                                                }
                                                                                                                                            });
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

                                        final String userStatus = dataSnapshot.child("status").getValue().toString();

                                        viewHolder.setUserName(name);
                                        viewHolder.setThumbImage(thumbImage, MainActivty_for_photographer.this);

                                        viewHolder.setUserStatus(date[0]);


                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                CharSequence option[]=new CharSequence[]{
                                                        R.string.cancel_reservation+"",

                                                };

                                                AlertDialog.Builder builder =new AlertDialog.Builder(MainActivty_for_photographer.this);
                                                builder.setTitle(R.string.Select_option);
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




    public void check_user(String type_acc)
    {


        if(type_acc.equals("photographer"))
        {



            Userref= FirebaseDatabase.getInstance().getReference().child("Users");
            Userref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {

                        Userref.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                if(currentuserid.equals(dataSnapshot.getKey()))
                                {
                                    Loginbar.dismiss();

                                    coustome_dialog dialog = new coustome_dialog();
                                    dialog.showDialog(MainActivty_for_photographer.this,R.string.user_specific);
                                }else
                                {
                                    Loginbar.dismiss();
                                }


                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }else
                    {

                        Loginbar.dismiss();



                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        if(type_acc.equals("photographer"))
        {
            Photoref.child(currentuserid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        if(dataSnapshot.hasChild("fullName"))
                        {
                            String name = dataSnapshot.child("fullName").getValue().toString();
                            Navusername.setText(name);

                        }
                        if(dataSnapshot.hasChild("profilimage_photo")){
                            String profilImage = dataSnapshot.child("profilimage_photo").getValue().toString();
                            Picasso.with(getBaseContext()).load(profilImage).into(NavprofileImage);

                        }
                        else
                        {
                            Toast.makeText(MainActivty_for_photographer.this, R.string.photo_not_exists, Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void UserMenuSelector(MenuItem item) {

        switch(item.getItemId()){

            case R.id.new_post:
                sandtopost_activity();

                break;
            case R.id.nav_Reservations:
                sandtofriends_activity();
                break;
            case R.id.nav_Reservations_request:
                sandtofriend_request_activity();
                break;
            case R.id.nav_profile:
                sandtopmyost_activity();
                break;
            case R.id.nav_setting:
                sandto_setting_activity();
                break;
            case R.id.nav_delete:
                sandtoDeleteActivity();
                break;
            case R.id.nav_schedule:
                send_to_Schedule_activity();
                break;
            case R.id.logout:
                mAuth.signOut();
                sandtologinActivity();
                Toast.makeText(this, R.string.logout, Toast.LENGTH_SHORT).show();

                break;

        }

    }

    public void send_to_Schedule_activity()
    {
     Intent to_Schedule_activity = new Intent(getApplicationContext(),Schedule_Activity.class);
     startActivity(to_Schedule_activity);
    }

    public void sandto_setting_activity()
    {
        Intent to_setting_activity = new Intent(getApplicationContext(),SettingActivity_photographer.class);
        startActivity(to_setting_activity);
    }
    public void sandtofriend_request_activity()
    {
        Intent to_request_activity = new Intent(getApplicationContext(),Reservation_request_Activity.class);
        startActivity(to_request_activity);
    }
    public void sandtofriends_activity()
    {
        Intent to_friends_activity = new Intent(getApplicationContext(),Reservation_accepted_Activity.class);
        startActivity(to_friends_activity);
    }
    public void sandtopmyost_activity()
    {
        Intent to_Post_activity = new Intent(getApplicationContext(),Photographer_page.class);
        startActivity(to_Post_activity);
    }
    public void sandtopost_activity()
    {
        Intent to_Post_activity = new Intent(getApplicationContext(),PostActivity.class);
        startActivity(to_Post_activity);

    }
    public void sandtologinActivity()
    {
        Intent to_login_activity = new Intent(getApplicationContext(),Login_Activity.class);
        to_login_activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(to_login_activity);
        finish();
    }
    public void sandtoDeleteActivity()
    {
        Intent to_delete_activity = new Intent(getApplicationContext(),Delete_photo_activity.class);
        to_delete_activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(to_delete_activity);
        finish();
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
