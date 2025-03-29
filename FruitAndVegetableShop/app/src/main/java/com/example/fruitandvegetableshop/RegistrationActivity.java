package com.example.fruitandvegetableshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG=RegistrationActivity.class.getName();
    private static final int KEY=17561;


    EditText username;
    EditText password;
    EditText passwordAgain;
    EditText phoneNumber;
    EditText address;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        int key=getIntent().getIntExtra("KEY",0);
        if(key!=17561){
            finish();
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordAgain = findViewById(R.id.passwordagain);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.postalAddress);
        email = findViewById(R.id.email);
    }

    public void register(View view) {
        String username=this.username.getText().toString();
        String password=this.password.getText().toString();
        String passwordAgain=this.passwordAgain.getText().toString();
        String phoneNumber=this.phoneNumber.getText().toString();
        String address=this.address.getText().toString();
        String email=this.email.getText().toString();


        if(!password.equals(passwordAgain)){
            Log.e(LOG_TAG,"Nem megegyező jelszó páros!");
            return;
        }

        Log.i(LOG_TAG,username+" "+email+" "+phoneNumber);
    }

    public void login(View view) {
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("KEY",KEY);

        startActivity(intent);
    }
}