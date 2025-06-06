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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getName();
    private static final int KEY=17561;

    private FirebaseAuth firebaseAuth;

    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*int key=getIntent().getIntExtra("KEY",0);
        if(key!=17561){
            finish();
        }*/

        firebaseAuth=FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }
    public void login(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        String email=this.email.getText().toString();
        String password=this.password.getText().toString();

        if(email.isBlank() || password.isBlank()){
            Log.e(LOG_TAG,"Hiányzó adat!");
            Toast.makeText(MainActivity.this,"Missing information!",Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG,"Sikeres bejelentkezés");
                    startShopping();

                }
                else {
                    Log.i(LOG_TAG,"Sikertelen bejelentkezés");
                    Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.i(LOG_TAG,email+" "+password);
    }

    public void register(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        Intent intent=new Intent(this,RegistrationActivity.class);
        intent.putExtra("KEY",KEY);

        startActivity(intent);
    }

    public void startShopping(){
        Intent intent=new Intent(this,ShoppingActivity.class);
        startActivity(intent);
    }
}
