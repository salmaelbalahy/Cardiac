package com.salma.cardiac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText ageET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ageET = findViewById(R.id.ageEditText);

    }
    public void button(View v){
        if (ageET.getText().toString().isEmpty()){
            Toast.makeText(this,"Please Enter Your Age",Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferencesManager.setIntValue(this,"age",Integer.parseInt(ageET.getText().toString()));
        Intent intent = new Intent(MainActivity.this, age.class);
        startActivity(intent);
    }
}

