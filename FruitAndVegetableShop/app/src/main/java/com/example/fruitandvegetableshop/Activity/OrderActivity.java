package com.example.fruitandvegetableshop.Activity;

import static android.util.Log.i;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitandvegetableshop.Model.Order;
import com.example.fruitandvegetableshop.Model.Product;
import com.example.fruitandvegetableshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {
    private static final String LOG_TAG=OrderActivity.class.getName();
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private ArrayList<Order> orders;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            i(LOG_TAG,"Hitelesített felhasználó!");
        }
        else{
            i(LOG_TAG,"Nem hitelesített felhasználó!");
            finish();
        }

        textView=findViewById(R.id.orders);
        mFirestore=FirebaseFirestore.getInstance();

        mFirestore.collection("orders")
                .whereEqualTo("email",user.getEmail())
                .orderBy("timeStamp")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot=task.getResult();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MMMM dd. HH:mm", Locale.getDefault());
                            StringBuilder text = new StringBuilder();

                            for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                Order order = documentSnapshot.toObject(Order.class);
                                if (order != null) {
                                    Date date = order.getTimeStamp();
                                    String formattedDate = sdf.format(date);

                                    text.append("🗓 ").append(formattedDate).append("\n");

                                    for (Map.Entry<String, Integer> product : order.getProducts().entrySet()) {

                                        text.append(" - Név: ").append(product.getKey().split(" ")[0].split(":")[1]).append("\n");
                                        text.append("   Mennyiség: ").append(product.getValue()).append(" kg\n");
                                    }

                                    text.append("\n");
                                }
                            }
                            if (text.length() > 0) {
                                textView.setText(text.toString());
                            }
                        }else{
                            Log.e(LOG_TAG, "Hiba a lekérdezés közben: "+task.getException());
                        }
                    }
                });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.log_out){
            Log.i(LOG_TAG, "Kijelentkezés");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId()==R.id.shop) {
            Log.i(LOG_TAG, "Vásárlás");
            Intent intent=new Intent(this,ShoppingActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId()==R.id.cart) {
            Log.i(LOG_TAG, "Kosár megnyitasa");
            Intent intent=new Intent(this, BasketActivity.class);
            startActivity(intent);
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }
}
