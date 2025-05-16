package com.example.fruitandvegetableshop.Activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.fruitandvegetableshop.R;
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

        firebaseAuth=FirebaseAuth.getInstance();

        TypedArray images = getResources().obtainTypedArray(R.array.img);

        for (int i = 0; i < images.length(); i++) {
            int resId = images.getResourceId(i, -1);
            if (resId != -1) {
                String resName = getResources().getResourceEntryName(resId);
                Log.d("IMG_ARRAY", "Elem " + i + ": " + resName+" "+resId);
            }
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }
    public void login(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        String email=this.email.getText().toString();
        String password=this.password.getText().toString();

        if(email.isBlank() || password.isBlank()){
            Log.d(LOG_TAG,"Hiányzó adat!");
            Toast.makeText(MainActivity.this,"Hiányzó adat!",Toast.LENGTH_LONG).show();
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
    }

    public void register(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        Intent intent=new Intent(this, RegistrationActivity.class);
        intent.putExtra("KEY",KEY);

        startActivity(intent);
    }

    public void startShopping(){
        Intent intent=new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }
}
