package com.devstudiosng.yg.smooop;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devstudiosng.yg.smooop.helpers.SharedPref;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.getUserId() != 0) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        TextView mTitleLabel = findViewById(R.id.title_label);

        Typeface pacificoFont = Typeface.createFromAsset(getAssets(),"fonts/pacifico.ttf");
        mTitleLabel.setTypeface(pacificoFont);

        Button introBtn = findViewById(R.id.intro_button);
        introBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                throw new RuntimeException("This is a crash");

                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
//                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });


    }

}
