package com.example.fruitandvegetableshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitandvegetableshop.Model.Product;
import com.example.fruitandvegetableshop.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModifyProductActivity extends AppCompatActivity {
    private static final String LOG_TAG=ModifyProductActivity.class.getName();
    private Product originalProduct;
    private FirebaseFirestore mFirestore;
    private EditText nameET;
    private EditText priceET;
    private EditText infoET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateproduct);

        int key=getIntent().getIntExtra("KEY",0);
        if(key!=145200){
            finish();
        }
        originalProduct=(Product) getIntent().getSerializableExtra("product");

        mFirestore=FirebaseFirestore.getInstance();

        nameET=findViewById(R.id.name);
        priceET=findViewById(R.id.price);
        infoET=findViewById(R.id.info);

        nameET.setText(originalProduct.getName());
        priceET.setText(originalProduct.getPrice()+"");
        infoET.setText(originalProduct.getInfo());
    }

    public void update(View view){
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        String name=this.nameET.getText().toString();
        String price=this.priceET.getText().toString();
        String info=this.infoET.getText().toString();


        if(name.isBlank() || price.isBlank() || info.isBlank()){
            Log.d(LOG_TAG,"Hiányzó adat!");
            Toast.makeText(ModifyProductActivity.this,"Hiányzó adat!",Toast.LENGTH_LONG).show();
            return;
        }

        Product product=new Product(originalProduct.getImgres(),Integer.valueOf(price),info,name);
        mFirestore.collection("products")
                .document(originalProduct.getDocumentId())
                .update("name",product.getName(),"price",product.getPrice(),"info",product.getInfo());

        Toast.makeText(this, "Termék sikeresen módosítva!", Toast.LENGTH_SHORT).show();
    }


    public void cancel(View view){
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        Intent intent=new Intent(this,ShoppingActivity.class);

        startActivity(intent);
    }
}
