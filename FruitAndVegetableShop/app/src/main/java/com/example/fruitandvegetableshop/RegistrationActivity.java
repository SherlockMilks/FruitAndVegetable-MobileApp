package com.example.fruitandvegetableshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG=RegistrationActivity.class.getName();
    private static final int KEY=17561;
    private FirebaseAuth firebaseAuth;


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

        firebaseAuth=FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordAgain = findViewById(R.id.passwordagain);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.postalAddress);
        email = findViewById(R.id.email);
    }

    public void register(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        String username=this.username.getText().toString();
        String password=this.password.getText().toString();
        String passwordAgain=this.passwordAgain.getText().toString();
        String phoneNumber=this.phoneNumber.getText().toString();
        String address=this.address.getText().toString();
        String email=this.email.getText().toString();

        if(username.isBlank() || email.isBlank() || password.isBlank() || phoneNumber.isBlank() || address.isBlank()){
            Log.e(LOG_TAG,"Hiányzó adat!");
            Toast.makeText(RegistrationActivity.this,"Missing information!",Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(passwordAgain)){
            Log.e(LOG_TAG,"Nem megegyező jelszó páros!");
            Toast.makeText(RegistrationActivity.this,"The passwords don't match!",Toast.LENGTH_LONG).show();
            return;
        }

        Log.i(LOG_TAG,username+" "+email+" "+phoneNumber);


        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG,"Sikeres regisztráció");
                    login(new RegistrationActivity().getCurrentFocus());
                }
                else {
                    Log.i(LOG_TAG,"Sikertelen regisztáció");
                    Toast.makeText(RegistrationActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void login(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("KEY",KEY);

        startActivity(intent);
    }
}