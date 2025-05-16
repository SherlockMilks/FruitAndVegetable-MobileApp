package com.example.fruitandvegetableshop.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fruitandvegetableshop.BackgroundService.NotificationHandler;
import com.example.fruitandvegetableshop.Model.Product;
import com.example.fruitandvegetableshop.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddProductActivity extends AppCompatActivity {
    private static final String LOG_TAG=AddProductActivity.class.getName();
    private FirebaseFirestore mFirestore;
    private EditText nameET;
    private EditText priceET;
    private EditText infoET;
    private NotificationHandler noti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }


        int key=getIntent().getIntExtra("KEY",0);
        if(key!=16992){
            finish();
        }

        noti=new NotificationHandler(this);

        mFirestore=FirebaseFirestore.getInstance();

        nameET=findViewById(R.id.name);
        priceET=findViewById(R.id.price);
        infoET=findViewById(R.id.info);
    }

    public void add(View view){
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        String name=this.nameET.getText().toString();
        String price=this.priceET.getText().toString();
        String info=this.infoET.getText().toString();


        if(name.isBlank() || price.isBlank() || info.isBlank()){
            Log.d(LOG_TAG,"Hiányzó adat!");
            Toast.makeText(AddProductActivity.this,"Hiányzó adat!",Toast.LENGTH_LONG).show();
            return;
        }

        Product product=new Product(2131230872,Integer.valueOf(price),info,name);
        mFirestore.collection("products").add(product);

        noti.send(name+" létrehozva");

        Toast.makeText(this, "Termék sikeresen létrehozva!", Toast.LENGTH_SHORT).show();
    }


    public void cancel(View view){
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        Intent intent=new Intent(this,ShoppingActivity.class);
        noti.cancel();

        startActivity(intent);
    }
}
