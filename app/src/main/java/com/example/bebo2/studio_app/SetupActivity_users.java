package com.example.bebo2.studio_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity_users extends AppCompatActivity {

    private EditText phone_number_edit, UserFullname;
    private Button saveInformationButoon;
    private CircleImageView userProfileImage;
    private DatabaseReference UserRefer, photoRef,UserRefer2,photoRef2,Photoref3,Userref2;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ProgressDialog lodingbar;
    private StorageReference UserprofileImageStorge, photgrprofileImageStorge;
    private String DEFAULT = "DN";
    Context context;
    private String type = "";
    private String tye_chacker;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = MainActivity.class.getSimpleName();
    String location;
    private Spinner countryNmae;
    private String strcountry;
    StorageReference filePath;
    Uri resulturi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();

        current_user_id = mAuth.getCurrentUser().getUid();
        UserRefer = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        photoRef = FirebaseDatabase.getInstance().getReference().child("Photographer").child(current_user_id);
        UserRefer2 = FirebaseDatabase.getInstance().getReference().child("Users");
        photoRef2 = FirebaseDatabase.getInstance().getReference().child("Photographer");
        UserprofileImageStorge = FirebaseStorage.getInstance().getReference().child("profile image");
        photgrprofileImageStorge = FirebaseStorage.getInstance().getReference().child("profile image photographer");

        Locale[] locale = Locale.getAvailableLocales();
        final ArrayList<String> countries = new ArrayList<>();
         String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        phone_number_edit = (EditText) findViewById(R.id.setup_userphone);
        UserFullname = (EditText) findViewById(R.id.setup_fullName);
        countryNmae = (Spinner) findViewById(R.id.setup_countryName);
        saveInformationButoon = (Button) findViewById(R.id.setup_information_button);
        userProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        countryNmae = (Spinner) findViewById(R.id.setup_countryName);
        CustomAdapter_Department adapter = new CustomAdapter_Department(SetupActivity_users.this, countries);
        countryNmae.setAdapter(adapter);

        countryNmae.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strcountry = countryNmae.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveInformationButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SaveaccountsetupInformation();

            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setMaxCropResultSize(700, 700)
                        .start(SetupActivity_users.this);

            }
        });

        final ArrayList<Listitem> listitems = new ArrayList<Listitem>();
        listitems.add(new Listitem(R.string.select_type_account));
        listitems.add(new Listitem( R.string.user));
        listitems.add(new Listitem(R.string.photographer_or_studio));
        final MyCustomeAdapter myCustomeAdapter3 = new MyCustomeAdapter(listitems);
        final Spinner spin_type = (Spinner) findViewById(R.id.type_login);
        spin_type.setAdapter(myCustomeAdapter3);
        spin_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    type = "user";
                    tye_chacker = "user";

                }
                if (position == 2) {
                    type = "photographer";
                    tye_chacker = "photographer";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        type = sharedPreferences.getString("kind_person", DEFAULT);
        if (type.equals(DEFAULT)) {
            Toast.makeText(SetupActivity_users.this, "not found", Toast.LENGTH_SHORT).show();
        }

        lodingbar = new ProgressDialog(this);
        lodingbar.setTitle(R.string.login_the_account);
        lodingbar.show();
        lodingbar.setCanceledOnTouchOutside(false);
        Check_user(type);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }

        if(type.equals("user"))
        {
            UserRefer2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(current_user_id))
                    {

                        senduserToMainActivity();

                    }else
                    {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            photoRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(current_user_id))
                    {
                        senduserToMainActivity_photo();

                    }else
                    {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void Check_user(String type_acc)
    {
        if(type_acc.equals("user"))
        {


            Photoref3= FirebaseDatabase.getInstance().getReference().child("Photographer");

            Photoref3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        Photoref3.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                if(current_user_id.equals(dataSnapshot.getKey()))
                                {
                                    lodingbar.dismiss();

                                    coustome_dialog dialog = new coustome_dialog();
                                    dialog.showDialog(SetupActivity_users.this,R.string.Photographer_specific);
                                }else
                                {
                                    lodingbar.dismiss();
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
                          lodingbar.dismiss();
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else
        {
            Userref2= FirebaseDatabase.getInstance().getReference().child("Users");
            UserRefer2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        Userref2.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                if(current_user_id.equals(dataSnapshot.getKey()))
                                {
                                    lodingbar.dismiss();

                                    coustome_dialog dialog = new coustome_dialog();
                                    dialog.showDialog(SetupActivity_users.this,R.string.user_specific);
                                }else
                                {

                                    lodingbar.dismiss();
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
                        lodingbar.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resulturi = result.getUri();


                userProfileImage.setImageURI(resulturi);



            }
        }



    }

    private void SaveaccountsetupInformation() {
        final String phone_number = phone_number_edit.getText().toString();
        final String fullName = UserFullname.getText().toString();



        if (TextUtils.isEmpty(phone_number)) {
            Toast.makeText(this, R.string.phone, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, R.string.hint_name, Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(strcountry)) {
            Toast.makeText(this, R.string.hint_country, Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(tye_chacker)) {
            Toast.makeText(this, R.string.select_type_account, Toast.LENGTH_LONG).show();
        }
        else if (resulturi == null) {
            Toast.makeText(this, "select photo please", Toast.LENGTH_LONG).show();
        }else {
            lodingbar.setTitle(R.string.registering_your_information);
            lodingbar.show();
            lodingbar.setCanceledOnTouchOutside(true);


            if (type.equals("user")) {
                filePath = UserprofileImageStorge.child(current_user_id + ".jpg");
            } else {
                filePath = photgrprofileImageStorge.child(current_user_id + ".jpg");
            }


            filePath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("kind_person", type);
                        editor.commit();

                        final String downloadUrl = task.getResult().getDownloadUrl().toString();
                        Toast.makeText(SetupActivity_users.this, R.string.image_uploaded_successfully, Toast.LENGTH_SHORT).show();

                        if (type.equals("user")) {
                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            HashMap userMap = new HashMap();
                            userMap.put("phone_number", phone_number);
                            userMap.put("fullName", fullName);
                            userMap.put("country", strcountry);
                            userMap.put("status", type);
                            userMap.put("gender", "none");
                            userMap.put("location", location);
                            userMap.put("id", current_user_id);
                            userMap.put("profilimage", downloadUrl);
                            userMap.put("device_token", device_token);

                            UserRefer.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()) {
                                        senduserToMainActivity();
                                        Toast.makeText(getBaseContext(), R.string.done_regist, Toast.LENGTH_LONG).show();
                                        lodingbar.dismiss();
                                    } else {
                                        String message = task.toString();
                                        Toast.makeText(SetupActivity_users.this, R.string.Error + message, Toast.LENGTH_SHORT).show();
                                        lodingbar.dismiss();
                                    }
                                }
                            });
                        } else {
                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            HashMap userMap = new HashMap();
                            userMap.put("phone_number", phone_number);
                            userMap.put("fullName", fullName);
                            userMap.put("country", strcountry);
                            userMap.put("status", type);
                            userMap.put("gender", "none");
                            userMap.put("location", location);
                            userMap.put("id", current_user_id);
                            userMap.put("profilimage_photo", downloadUrl);
                            userMap.put("device_token", device_token);

                            photoRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()) {
                                        senduserToMainActivity();
                                        Toast.makeText(getBaseContext(), R.string.done_regist, Toast.LENGTH_LONG).show();
                                        lodingbar.dismiss();
                                    } else {
                                        String message = task.toString();
                                        Toast.makeText(SetupActivity_users.this, R.string.Error + message, Toast.LENGTH_SHORT).show();
                                        lodingbar.dismiss();
                                    }
                                }
                            });
                        }

                    }
                }
            });

        }

    }

    private void senduserToMainActivity() {
        Intent MainIntent = new Intent(SetupActivity_users.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void senduserToMainActivity_photo() {
        Intent MainIntent = new Intent(SetupActivity_users.this, MainActivty_for_photographer.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }
    public class MyCustomeAdapter extends BaseAdapter {

        ArrayList<Listitem> listitems = new ArrayList<Listitem>();

        public MyCustomeAdapter(ArrayList<Listitem> items) {
            this.listitems = items;
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
            LayoutInflater layoutInflater = getLayoutInflater();
            View view3 = layoutInflater.inflate(R.layout.row_lay, null);
            final TextView textView = (TextView) view3.findViewById(R.id.textname);

            textView.setText(listitems.get(i).name);

            return view3;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.

            }
        }
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(SetupActivity_users.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");


        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                           location=String.format(Locale.ENGLISH, "%s%f",
                                    "",
                                    mLastLocation.getLatitude())+String.format(Locale.ENGLISH, "%s,%f",
                                    "",
                                    mLastLocation.getLongitude());


                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());

                        }
                    }
                });
    }

}
