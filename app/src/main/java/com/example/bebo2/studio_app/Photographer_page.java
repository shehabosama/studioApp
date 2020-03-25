package com.example.bebo2.studio_app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class Photographer_page extends AppCompatActivity {
    private TextView userName,userStatus,userCountry,userGender;
    private CircleImageView userProfileImage;
    private DatabaseReference userRef;
    private String current_user_id;
    private FirebaseAuth mAuth;
    private Button Postbutton;

    private DatabaseReference postRef;
    private int countPost;
    private RecyclerView post_List;
    String currentuserid;
    private Toolbar mtoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_page);
        userName=(TextView)findViewById(R.id.user_name_profile);
        userStatus=(TextView)findViewById(R.id.user_status_profile);
        userCountry=(TextView)findViewById(R.id.user_country_profile);
        userGender=(TextView)findViewById(R.id.user_gender_profile);
        userProfileImage=(CircleImageView)findViewById(R.id.profile_image);
        Postbutton =(Button)findViewById(R.id.button_post);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        currentuserid=mAuth.getCurrentUser().getUid();

        mtoolbar=(Toolbar)findViewById(R.id.tool_bar_profile);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(R.string.myProfile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userRef= FirebaseDatabase.getInstance().getReference().child("Photographer").child(current_user_id);
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");


        post_List=(RecyclerView)findViewById(R.id.all_users_post_list);
        post_List.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        post_List.setLayoutManager(linearLayoutManager);
        Displayalluserposts();


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userprofilename=dataSnapshot.child("fullName").getValue().toString();
                String userstatus=dataSnapshot.child("status").getValue().toString();
                String usergender=dataSnapshot.child("gender").getValue().toString();
                String usercountry=dataSnapshot.child("country").getValue().toString();
                String phone_number=dataSnapshot.child("phone_number").getValue().toString();
                String profileImage=dataSnapshot.child("profilimage_photo").getValue().toString();
                userName.setText(userprofilename);
                userStatus.setText(userstatus);
                Resources res = getResources();
                userGender.setText(res.getString(R.string.gender)+": "+usergender);
                userCountry.setText(res.getString(R.string.hint_country)+": "+usercountry);

                Picasso.with(getBaseContext()).load(profileImage).into(userProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        postRef.orderByChild("uid").startAt(current_user_id)
                .endAt(current_user_id +"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            Resources res = getResources();
                            countPost=(int) dataSnapshot.getChildrenCount();
                            Postbutton.setText(countPost +" "+res.getString(R.string.post));
                        }
                        else
                        {
                            Resources res = getResources();
                            Postbutton.setText("0 "+res.getString(R.string.post));
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        Postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myPostIntent=new Intent(getApplicationContext(),MyPostActivity.class);
                startActivity(myPostIntent);
            }
        });
    }


    private void Displayalluserposts()
    {

        Query mypost=postRef.orderByChild("uid").startAt(currentuserid)
                .endAt(currentuserid +"\uf8ff");


        FirebaseRecyclerAdapter<Posts,PostsViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(
                        Posts.class,
                        R.layout.all_post_layout,
                        PostsViewHolder.class,
                        mypost
                )
                {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position) {

                        final String idlist=getRef(position).getKey();


                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setPostprofileimag(getBaseContext(),model.getProfileimage());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setPostimage(getBaseContext(),model.getPostimage());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent onClickIntent=new Intent(getApplicationContext(),ClickPostActivity.class);
                                onClickIntent.putExtra("postkey",idlist);
                                startActivity(onClickIntent);
                            }
                        });


                    }
                };
        post_List.setAdapter(firebaseRecyclerAdapter);
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        String currentuserid;
        CircleImageView postprofileImage;
        TextView name;




        public PostsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;


            postprofileImage=(CircleImageView) mView.findViewById(R.id.postprofileImage);
            name=(TextView)mView.findViewById(R.id.postfullname);

            currentuserid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }





        public void setFullname(String fullname)
        {
            name=(TextView)mView.findViewById(R.id.postfullname);
            name.setText(fullname);
        }

        public void setPostprofileimag(Context context, String postprofileimag)
        {
            postprofileImage=(CircleImageView) mView.findViewById(R.id.postprofileImage);

            Picasso.with(context).load(postprofileimag).into(postprofileImage);

        }

        public void setDate(String date)
        {
            TextView textdate=(TextView)mView.findViewById(R.id.textdatepost);
            textdate.setText(date);
        }

        public void setTime(String time)
        {
            TextView textTime=(TextView) mView.findViewById(R.id.textTimepost);

            textTime.setText(time);
        }

        public void setDescription(String description)
        {
            TextView textDescription=(TextView)mView.findViewById(R.id.textdescriptionpost);
            textDescription.setText(description);
        }

        public void setPostimage(Context context,String postimage)
        {
            ImageView postImage=(ImageView)mView.findViewById(R.id.postImage);
            Picasso.with(context).load(postimage).into(postImage);
        }


    }
}
