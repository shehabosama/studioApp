package com.example.bebo2.studio_app;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class Splash_screen extends AppCompatActivity {

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar_);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#000000"),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        Thread thread = new Thread()
        {

              @Override
              public void run() {

                  try{
                      sleep(5000);
                  }catch (Exception e){

                      e.printStackTrace();

                  }

                  finally
                  {
                      startActivity(new Intent(getApplicationContext(),Login_Activity.class));
                  }

          }
        };thread.start();
    }
}
