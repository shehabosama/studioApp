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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser  currentuser;
    private String currentuserid;
    private DatabaseReference Userref,Photoref;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mtoolbar;
    private CircleImageView NavprofileImage;
    private TextView Navusername;
    private ProgressDialog Loginbar;
    private String DEFAULT="DN";
    private String type;
    private RecyclerView photographer_list;
    private MaterialSearchView searchView;
    private String type_search="fullName";
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentuserid=mAuth.getCurrentUser().getUid();

        SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
         type = sharedPreferences.getString("kind_person", DEFAULT);
        if (type.equals(DEFAULT))
        {
            Toast.makeText(MainActivity.this, "not found", Toast.LENGTH_SHORT).show();
        }

        Userref= FirebaseDatabase.getInstance().getReference().child("Users");
        Photoref= FirebaseDatabase.getInstance().getReference().child("Photographer");
        database = FirebaseDatabase.getInstance();
        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        mtoolbar=(Toolbar)findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(R.string.home);
        mtoolbar.setTitleTextColor(getResources().getColor(android.R.color.black));

        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        actionBarDrawerToggle= new ActionBarDrawerToggle(MainActivity.this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView=(NavigationView)findViewById(R.id.nav);
        View nav_view=navigationView.inflateHeaderView(R.layout.header);

        NavprofileImage=(CircleImageView)nav_view.findViewById(R.id.nav_profile_image);
        Navusername=(TextView)nav_view.findViewById(R.id.nav_user_full_name);
        photographer_list = (RecyclerView)findViewById(R.id.all_photo_list);

        photographer_list.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        photographer_list.setLayoutManager(gridLayoutManager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {


                SearchForPeopleAndFriends();

            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){
                    SearchForPeopleAndFrinds(newText);
                }
                return true;
            }

        });

        final ArrayList<Listitem> listitems=new ArrayList<Listitem>();
        listitems.add(new Listitem(R.string.select_type_search));
        listitems.add(new Listitem(R.string.hint_name));
        listitems.add(new Listitem(R.string.address));
        listitems.add(new Listitem(R.string.phone));
        final MyCustomeAdapter myCustomeAdapter3= new MyCustomeAdapter(listitems);
        final Spinner spin_search =(Spinner)findViewById(R.id.spin_search);
        spin_search.setAdapter(myCustomeAdapter3);
        spin_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                    type_search = "fullName";

                }
                if(position==2)
                {
                    type_search = "country";


                }
                if(position==3)
                {
                    type_search  = "phone_number";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Loginbar=new ProgressDialog(this);

        Check_user(type);
        SearchForPeopleAndFriends();

        Intent serviceIntent = new Intent(this, ExampleService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentuser=mAuth.getCurrentUser();

        if (currentuser == null)
        {
            sandtologinActivity();
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

            case R.id.nav_Reservations:

                sandtofriends_activity();
                break;

            case R.id.nav_Reservations_request:

                sandtofriend_request_activity();
                break;

            case R.id.all_advertising:

                sendtoHome_activity();
                break;
            case R.id.nav_setting:
                sandto_setting_activity();
                break;
            case R.id.nav_profile:
                sandto_profile_activity();
                break;
            case R.id.nav_delete:
                sandtoDeleteActivity();
                break;
            case R.id.logout:
                mAuth.signOut();
                sandtologinActivity();
                Toast.makeText(this, R.string.logout, Toast.LENGTH_SHORT).show();

                break;
        }
    }

    public void Check_user(String type_acc)
    {
        if(type_acc.equals("user"))
        {
            Loginbar.setTitle(R.string.login_the_account);
            Loginbar.show();
            Loginbar.setCanceledOnTouchOutside(false);

            Photoref= FirebaseDatabase.getInstance().getReference().child("Photographer");

            Photoref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if(currentuserid.equals(dataSnapshot.getKey()))
                    {
                        Loginbar.dismiss();

                        coustome_dialog dialog = new coustome_dialog();
                        dialog.showDialog(MainActivity.this,R.string.Photographer_specific);
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
        }

        if(type_acc.equals("user"))
        {
            Loginbar.setTitle(R.string.login_the_account);
            Loginbar.show();
            Loginbar.setCanceledOnTouchOutside(false);

            Userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        if(dataSnapshot.hasChild("fullName"))
                        {
                            String name = dataSnapshot.child("fullName").getValue().toString();
                            Navusername.setText(name);

                        }
                        if(dataSnapshot.hasChild("profilimage")){
                            String profilImage = dataSnapshot.child("profilimage").getValue().toString();
                            Picasso.with(getBaseContext()).load(profilImage).into(NavprofileImage);

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "profile image and name not exists", Toast.LENGTH_SHORT).show();
                        }
                        Loginbar.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else
        {
            Loginbar.dismiss();
            sandtoMainActivity();

        }

    }
    public void sandto_profile_activity()
    {
        Intent to_profile_activity = new Intent(getApplicationContext(),User_profile.class);
        to_profile_activity.putExtra("Visituserid",currentuserid);
        startActivity(to_profile_activity);
    }
    public void sandto_setting_activity()
    {
        Intent to_setting_activity = new Intent(getApplicationContext(),SettingActivity.class);
        startActivity(to_setting_activity);
    }
    public void sandtofriend_request_activity()
    {
        Intent to_request_activity = new Intent(getApplicationContext(),Reservation_request_Activity.class);
        startActivity(to_request_activity);
    }
    public void sandtofriends_activity()
    {
        Intent to_friends_activity = new Intent(getApplicationContext(),Reservation_accepted_usersActivity.class);
        startActivity(to_friends_activity);
    }
    public void sandtoDeleteActivity()
    {
        Intent to_delete_activity = new Intent(getApplicationContext(),Delete_activity.class);
        to_delete_activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(to_delete_activity);
        finish();
    }
    public void sandtologinActivity()
    {
        Intent to_login_activity = new Intent(getApplicationContext(),Login_Activity.class);
        to_login_activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(to_login_activity);
        finish();
    }
    public void sandtoMainActivity()
    {
        Intent to_login_activity = new Intent(getApplicationContext(),MainActivty_for_photographer.class);
        to_login_activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(to_login_activity);
        finish();
    }

    public void sendtoHome_activity()
    {
        Intent to_home_activity = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(to_home_activity);
    }

    private void SearchForPeopleAndFrinds(String SearchUserName) {

        Toast.makeText(this, R.string.Searching, Toast.LENGTH_SHORT).show();
        Query searchPeopleAndFriends = Photoref.orderByChild(type_search)
                .startAt(SearchUserName).endAt(SearchUserName + "\uf8ff");


        FirebaseRecyclerAdapter<All_photographer, AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<All_photographer,AllUsersViewHolder>(
                All_photographer.class,
                R.layout.all_users_display_layout,
                AllUsersViewHolder.class,
                searchPeopleAndFriends
        ) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, All_photographer model, final int position) {

                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_image(getApplicationContext(), model.getUser_image());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Resources res = getResources();
                        CharSequence option[]=new CharSequence[]{
                                res.getString(R.string.Profile_and)+" "+res.getString(R.string.Reservation_titl)
                        };

                        AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.Select_option);
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which==0){
                                    String visit_user_id = getRef(position).getKey();
                                    Intent personprofileIntent = new Intent(getApplicationContext(), PersonProfileActivty.class);
                                    personprofileIntent.putExtra("Visituserid", visit_user_id);
                                    startActivity(personprofileIntent);
                                }

                            }
                        });
                        builder.show();
                    }
                });

            }
        };

        photographer_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
    private void SearchForPeopleAndFriends() {
        FirebaseRecyclerAdapter<All_photographer, AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<All_photographer, MainActivity.AllUsersViewHolder>(
                All_photographer.class,
                R.layout.all_users_display_layout,
                MainActivity.AllUsersViewHolder.class,
                Photoref
        ) {
            @Override
            protected void populateViewHolder(final MainActivity.AllUsersViewHolder viewHolder, All_photographer model, final int position) {
                final String visit_user_id = getRef(position).getKey();
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_image(getApplicationContext(), model.getUser_image());

                viewHolder.ref3.child(visit_user_id).child("ratingbar").child(currentuserid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            final String count = dataSnapshot.getValue().toString();

                            if (count.equals("0") || count.equals("0.5") || count.equals("1") || count.equals("1.5") || count.equals("2")) {

                                viewHolder.ref3.child(visit_user_id).child("ratingbar").child(currentuserid).removeValue();

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });



               // viewHolder.reate_photo(visit_user_id);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Resources res = getResources();
                        CharSequence option[]=new CharSequence[]{
                                res.getString(R.string.Profile_and)+" "+res.getString(R.string.Reservation_titl),


                        };

                        AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.Select_option);
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which==0){
                                    String visit_user_id = getRef(position).getKey();
                                    Intent personprofileIntent = new Intent(getApplicationContext(), PersonProfileActivty.class);
                                    personprofileIntent.putExtra("Visituserid", visit_user_id);
                                    startActivity(personprofileIntent);
                                }

                            }
                        });
                        builder.show();
                    }
                });
            }
        };
        photographer_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }


    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        DatabaseReference ref;
        String currnet_user_id;
        DatabaseReference ref2;
        DatabaseReference ref3;
        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            currnet_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            ref  = FirebaseDatabase.getInstance().getReference("rating");
            ref2  = FirebaseDatabase.getInstance().getReference("rating");
            ref3  = FirebaseDatabase.getInstance().getReference("rating");


        }



        public void setUser_name(String user_name) {
            TextView name = (TextView) mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }

        public void setUser_status(String user_status) {
            TextView status = mView.findViewById(R.id.all_users_status);
            status.setText(user_status);
        }

        public void setUser_image(Context ctx, String user_image) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);


            Picasso.with(ctx).load(user_image).placeholder(R.drawable.backgroundprof).into(image);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
    public class MyCustomeAdapter extends BaseAdapter {

        ArrayList<Listitem> listitems=new ArrayList<Listitem>();

        public MyCustomeAdapter( ArrayList<Listitem> items){
            this.listitems=items;
        }

        @Override
        public int getCount() {
            return listitems.size();
        }

        @Override
        public Object getItem(int i) {
            return listitems.get(i).name;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater=getLayoutInflater();
            View view3=layoutInflater.inflate(R.layout.row_lay,null);
            final TextView textView=(TextView)view3.findViewById(R.id.textname);
            textView.setText(listitems.get(i).name);
            return view3;
        }
    }

}
