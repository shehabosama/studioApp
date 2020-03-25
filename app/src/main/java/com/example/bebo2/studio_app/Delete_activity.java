package com.example.bebo2.studio_app;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Delete_activity extends AppCompatActivity {
    private DatabaseReference usersettingref,usersettingref2;
    private FirebaseAuth mAuth;
    private String current_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_activity);

        mAuth=FirebaseAuth.getInstance();

        current_user_id=mAuth.getCurrentUser().getUid();
        usersettingref= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        new AlertDialog.Builder(Delete_activity.this)
                .setMessage(R.string.Delete_account)
                .setCancelable(false)
                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {


                        usersettingref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    deleteAccount();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                })
                .show();
    }

    private void deleteAccount() {

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(Delete_activity.this, Login_Activity.class));
                    finish();
                } else {
                    Toast.makeText(Delete_activity.this, "error"+task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
