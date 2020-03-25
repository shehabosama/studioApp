package com.example.bebo2.studio_app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText userStatus;
    private EditText userName;
    private EditText user_phone_number;
    private EditText userCountry;
    private EditText userGender;
    private Button update_account_button;
    private DatabaseReference usersettingref,usersettingref2;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private static final int picgallery=1;
    private ProgressDialog lodingbar;
    private StorageReference UserprofileImageStorge;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        mtoolbar=(Toolbar)findViewById(R.id.settingtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        profileImage=(CircleImageView)findViewById(R.id.image_profile_setting);
        userStatus=(EditText) findViewById(R.id.status_setting);
        userName=(EditText)findViewById(R.id.user_name_setting);
        user_phone_number=(EditText)findViewById(R.id.phone_number);
        userCountry=(EditText)findViewById(R.id.country_setting);
        userGender=(EditText)findViewById(R.id.gender_setting);
        update_account_button=(Button)findViewById(R.id.save_change_settings);
        mAuth=FirebaseAuth.getInstance();

        current_user_id=mAuth.getCurrentUser().getUid();
        usersettingref= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        usersettingref2= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        UserprofileImageStorge= FirebaseStorage.getInstance().getReference().child("profile image");





        usersettingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username=dataSnapshot.child("fullName").getValue().toString();
                String userstatus=dataSnapshot.child("status").getValue().toString();
                String usercountry=dataSnapshot.child("country").getValue().toString();
                String usergender=dataSnapshot.child("gender").getValue().toString();
                String userphone=dataSnapshot.child("phone_number").getValue().toString();

                userName.setText(username);
                userStatus.setText(userstatus);
                userCountry.setText(usercountry);
                userGender.setText(usergender);
                user_phone_number.setText(userphone);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .setMaxCropResultSize(700,700)
                        .start(SettingActivity.this);
            }
        });


        update_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap userMap=new HashMap();
                userMap.put("fullName",userName.getText().toString());
                userMap.put("country",userCountry.getText().toString());
                userMap.put("status",userStatus.getText().toString());
                userMap.put("phone_number",user_phone_number.getText().toString());
                userMap.put("gender",userGender.getText().toString());

                usersettingref2.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getBaseContext(),"the update is successfully...",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        usersettingref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("profilimage"))
                    {
                        String Image = dataSnapshot.child("profilimage").getValue().toString();

                        Picasso.with(getBaseContext()).load(Image).placeholder(R.drawable.backgroundprof).into(profileImage);
                    }else
                    {
                        Toast.makeText(getBaseContext(), R.string.photo_not_exists, Toast.LENGTH_SHORT).show();

                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        lodingbar=new ProgressDialog(this);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resulturi=result.getUri();
                lodingbar.setTitle(R.string.changing_photo);
                lodingbar.show();
                lodingbar.setCanceledOnTouchOutside(true);
                final StorageReference filePath=UserprofileImageStorge.child(current_user_id + ".jpg");

                filePath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){


                            final String downloadUrl=task.getResult().getDownloadUrl().toString();
                            Toast.makeText(SettingActivity.this, R.string.image_uploaded_successfully, Toast.LENGTH_SHORT).show();

                            usersettingref2.child("profilimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {


                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(SettingActivity.this, R.string.image_uploaded_successfully, Toast.LENGTH_SHORT).show();
                                                lodingbar.dismiss();
                                            }
                                            else
                                            {
                                                String message=task.getException().getMessage();
                                                Toast.makeText(SettingActivity.this, R.string.Error+message, Toast.LENGTH_SHORT).show();
                                                lodingbar.dismiss();

                                            }
                                        }

                                    });
                        }
                    }
                });



            }
        }


    }



}
