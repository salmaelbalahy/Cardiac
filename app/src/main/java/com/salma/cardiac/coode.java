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
        EditText age =findViewById(R.id.editText);
        RadioButton Running=findViewById(R.id.rad);
        RadioButton Relaxing=findViewById(R.id.rad2);
        RadioButton Athlete=findViewById(R.id.rad3);
        RadioButton BreathingHardly=findViewById(R.id.rad4);
        RadioButton Doingnothing=findViewById(R.id.rad5);
        RadioButton Anexeity=findViewById(R.id.radjh);
        final String a=age.getText().toString();
        SharedPreferencesManager.getIntValue(this,"age");


        int ageValue = Integer .valueOf(a);
        if (ageValue < 0 && ageValue>=3)


            SharedPreferencesManager.getIntValue(this,"age");

        {




    }
}
