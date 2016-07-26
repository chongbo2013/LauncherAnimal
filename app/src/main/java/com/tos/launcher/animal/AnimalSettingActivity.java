package com.tos.launcher.animal;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AnimalSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SizeUtils.reset(this);
        setContentView(R.layout.activity_animal_setting);
    }
}
