package com.example.bebo2.studio_app;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class ItemListDialogFragment_register extends BottomSheetDialogFragment {
    private Button cOnfirm_button;
    private EditText eMail,pAssword,cOnfirmPassword;
    private String Current_user_id;
    private ProgressDialog mprogress;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_list_dialog_item_register, container, false);
        progressBar =(ProgressBar) v.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#000000"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        mAuth = FirebaseAuth.getInstance();

        eMail = (EditText)v.findViewById(R.id.Email);
        pAssword = (EditText)v.findViewById(R.id.password);
        cOnfirmPassword = (EditText)v.findViewById(R.id.confirmPassword);
        cOnfirm_button = (Button)v.findViewById(R.id.createaccount);


        cOnfirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                CreateNewAccount();
            }
        });



        return v;
    }


    private void sendUsertomainActivity() {
        Intent MainIntent=new Intent(getActivity().getApplicationContext(), MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);

    }
    private void CreateNewAccount() {
        String Email=eMail.getText().toString();
        String password=pAssword.getText().toString();
        String confirmPassword=cOnfirmPassword.getText().toString();

        if(TextUtils.isEmpty(Email)){
            Toast.makeText(getActivity().getApplicationContext(),R.string.please_enter_you_Email,Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(getActivity().getApplicationContext(),R.string.please_enter_your_pass, Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(getActivity().getApplicationContext(),R.string.confirm_password,Toast.LENGTH_LONG).show();

        }else if(!password.equals(confirmPassword)){
            Toast.makeText(getActivity().getApplicationContext(),R.string.pass_not_equals,Toast.LENGTH_LONG).show();
        }else{


            mAuth.createUserWithEmailAndPassword(Email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if(task.isSuccessful()){
                                 progressBar.setVisibility(View.INVISIBLE);
                                senduserTosetupActivity();
                                Toast.makeText(getContext(), R.string.done_regist, Toast.LENGTH_LONG).show();
                            }else {
                                progressBar.setVisibility(View.INVISIBLE);

                                String message=task.toString();
                                Toast.makeText(getActivity().getApplicationContext(),R.string.email_registed+message,Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }
    }

    public void  senduserTosetupActivity()
    {
        Intent to_login_activity = new Intent(getContext(),SetupActivity_users.class);
        to_login_activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(to_login_activity);

    }
}
