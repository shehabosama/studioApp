package com.example.bebo2.studio_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetpasswordActivity extends AppCompatActivity {

    private EditText textEmail;
    private Button send_button;
    FirebaseAuth mAuth;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        mAuth=FirebaseAuth.getInstance();


        mToolbar = (Toolbar)findViewById(R.id.toolbarreset) ;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.i_forgot_pass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textEmail=(EditText)findViewById(R.id.Email_input_text);
        send_button=(Button)findViewById(R.id.sendToEmail_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.sendPasswordResetEmail(textEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent LoginIntent=new Intent(getApplicationContext(),Login_Activity.class);
                            startActivity(LoginIntent);
                            Toast.makeText(getBaseContext(),R.string.send_viryfy,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
