package com.example.bebo2.studio_app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference postRef,LikesRef;
    private RecyclerView homistpost;
    private ImageView imageView;
    String post;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth= FirebaseAuth.getInstance();

        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");

        imageView = (ImageView)findViewById(R.id.image);
        homistpost=(RecyclerView)findViewById(R.id.homlistPost);


        mToolbar = (Toolbar)findViewById(R.id.toolbar_home);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.edit_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        homistpost.setLayoutManager(linearLayoutManager);

        Displayalluserposts();

    }


    private void Displayalluserposts()
    {



            FirebaseRecyclerAdapter<Posts,PostsViewHolder> firebaseRecyclerAdapter=
                    new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(
                            Posts.class,
                            R.layout.all_post_layout,
                            HomeActivity.PostsViewHolder.class,
                            postRef
                    )
                    {


                        @Override
                        protected void populateViewHolder(HomeActivity.PostsViewHolder viewHolder, Posts model, int position) {

                            final String idlist=getRef(position).getKey();



                            viewHolder.setFullname(model.getFullname());

                            viewHolder.setProfileimage(getBaseContext(),model.getProfileimage());

                            viewHolder.setDate(model.getDate());

                            viewHolder.setTime(model.getTime());

                            viewHolder.setDescription(model.getDescription());

                            viewHolder.setPostimage(getBaseContext(),model.getPostimage());





                        }
                    };
            homistpost.setAdapter(firebaseRecyclerAdapter);





    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        CircleImageView postprofileImage;
        TextView name;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            postprofileImage=(CircleImageView) mView.findViewById(R.id.postprofileImage);
            name=(TextView)mView.findViewById(R.id.postfullname);
        }


        public void setFullname(String fullname)
        {
            name=(TextView)mView.findViewById(R.id.postfullname);
            name.setText(fullname);
        }

        public void setProfileimage(Context context,String profileimage)
        {
            postprofileImage=(CircleImageView) mView.findViewById(R.id.postprofileImage);

            Picasso.with(context).load(profileimage).into(postprofileImage);

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
