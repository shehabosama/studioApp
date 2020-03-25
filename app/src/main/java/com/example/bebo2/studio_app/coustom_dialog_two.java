package com.example.bebo2.studio_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class coustom_dialog_two {
    Dialog dialog;
    public void showDialog(final Activity activity, int msg){

        dialog = new Dialog(activity,R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.coustom_dialog_two);
       // dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);


        dialog.show();

    }

    public void des(final Activity activity)
    {

        dialog.dismiss();
    }
}
