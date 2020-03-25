package com.example.bebo2.studio_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class coustome_dialog {

    FirebaseAuth mAuth;
    public void showDialog(final Activity activity, int msg){
        mAuth = FirebaseAuth.getInstance();
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.costome_dialoge);
       // dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent to_login = new Intent();
                to_login.setClass(activity.getApplicationContext(),Login_Activity.class);
                to_login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(to_login);
                mAuth.signOut();
            }
        });

        dialog.show();

    }

}
