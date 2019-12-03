package com.salma.cardiac;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;

public class coode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coode);

        RadioButton Running = findViewById(R.id.rad);
        RadioButton Relaxing = findViewById(R.id.rad2);
        RadioButton Athlete = findViewById(R.id.rad3);
        RadioButton BreathingHardly = findViewById(R.id.rad4);
        RadioButton Doingnothing = findViewById(R.id.rad5);
        RadioButton Anexeity = findViewById(R.id.radjh);

        int age = SharedPreferencesManager.getIntValue(this,"age");

        if (age < 0 && age >= 3)
        {
            // do something
        }

    }
}