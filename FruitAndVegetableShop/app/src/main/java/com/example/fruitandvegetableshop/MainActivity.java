package com.example.fruitandvegetableshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getName();
    private static final int KEY=17561;

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*int key=getIntent().getIntExtra("KEY",0);
        if(key!=17561){
            finish();
        }*/

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }
    public void login(View view) {
        String username=this.username.getText().toString();
        String password=this.password.getText().toString();

        Log.i(LOG_TAG,username+" "+password);
    }

    public void register(View view) {
        Intent intent=new Intent(this,RegistrationActivity.class);
        intent.putExtra("KEY",KEY);

        startActivity(intent);

    }
}
