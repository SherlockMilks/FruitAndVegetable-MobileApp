package com.example.fruitandvegetableshop.Activity;

import static android.util.Log.i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.fruitandvegetableshop.Adapter.BasketAdapter;
import com.example.fruitandvegetableshop.Model.Order;
import com.example.fruitandvegetableshop.Model.Product;
import com.example.fruitandvegetableshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BasketActivity extends AppCompatActivity {
    private static final String LOG_TAG=BasketActivity.class.getName();
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private RecyclerView recyclerView;
    private HashMap<Product,Integer> basketItems;
    private BasketAdapter adapter;
    private int price=0;
    private int gridNumber=1;
    private TextView osszar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            i(LOG_TAG,"Hitelesített felhasználó!");
        }
        else{
            i(LOG_TAG,"Nem hitelesített felhasználó!");
            finish();
        }

        mFirestore=FirebaseFirestore.getInstance();

        basketItems = new HashMap<>();
        loadBasketFromPreferences();
        HashMap<Product,Integer> intentItems = (HashMap<Product, Integer>) getIntent().getSerializableExtra("termekek");
        if(intentItems!=null){
            basketItems.putAll(intentItems);
        }


        if(basketItems!=null){
            for (Map.Entry<Product,Integer> elem:basketItems.entrySet()) {
                price+=elem.getValue()*elem.getKey().getPrice();
            }
        }
        osszar=findViewById(R.id.total_price);
        osszar.setText(price+"Ft");


        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        TextView emptyText = findViewById(R.id.empty_text);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayout bottombar = findViewById(R.id.bottom_bar);

        adapter=new BasketAdapter(this, basketItems,this, new BasketAdapter.OnBasketChangeListener() {
            @Override
            public void onBasketUpdated(int itemCount, Map.Entry<Product, Integer> basketItem){
                price-=basketItem.getValue()*basketItem.getKey().getPrice();
                osszar.setText(price+"Ft");

                if(itemCount==0){
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    bottombar.setVisibility(View.GONE);
                } else{
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    bottombar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void priceChange(Map.Entry<Product, Integer> basketItem, int value) {
                price+=value*basketItem.getKey().getPrice();
                osszar.setText(price+"Ft");
            }
        });
        recyclerView.setAdapter(adapter);

        if (basketItems.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            bottombar.setVisibility(View.GONE);
        } else{
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            bottombar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.basket_menu, menu);
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
        } else if (item.getItemId()==R.id.orders) {
            Log.i(LOG_TAG, "Rendelések megnyitasa");
            Intent intent=new Intent(this, OrderActivity.class);
            startActivity(intent);
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveBasketToPreferences();
    }

    public void saveBasketToPreferences() {
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();

        List<Map<String, String>> basketMap = new ArrayList<>();
        for (Map.Entry<Product,Integer> elem:basketItems.entrySet()) {
            Map<String,String> product=new HashMap<>();
            product.put("name",elem.getKey().getName());
            product.put("info",elem.getKey().getInfo());
            product.put("price",elem.getKey().getPrice()+"");
            product.put("imgres",elem.getKey().getImgres()+"");
            product.put("amount",elem.getValue()+"");
            basketMap.add(product);
        }

        String basketJson = gson.toJson(basketMap);
        Log.d(LOG_TAG,"Kilep: "+basketJson);
        editor.putString("basketItems", basketJson);
        editor.apply();
    }


    private void loadBasketFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String basketJson = prefs.getString("basketItems", null);

        if (basketJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, String>>>() {}.getType();

            List<Map<String, String>> savedItems = gson.fromJson(basketJson,type);
            basketItems.clear();

            for (Map<String, String> entry : savedItems) {
                Product product = new Product(Integer.valueOf(entry.get("imgres")),Integer.valueOf(entry.get("price")),entry.get("info"),entry.get("name"));
                basketItems.put(product, Integer.valueOf(entry.get("amount")));
                Log.d(LOG_TAG,entry.get("name")+" "+entry.get("amount"));
            }

            if(adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void removeItem(Product product){
        basketItems.remove(product);
    }

    public void order(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        HashMap<String,Integer> converted=new HashMap<>();
        for (Map.Entry<Product,Integer> item:basketItems.entrySet()) {
            converted.put(item.getKey().toString(),item.getValue());
        }

        Order order=new Order(user.getEmail(),converted);

        mFirestore.collection("orders").add(order);

        Toast.makeText(BasketActivity.this,"Rendelés sikeresen feladva!",Toast.LENGTH_LONG).show();
        basketItems.clear();
        adapter.notifyDataSetChanged();

        if (basketItems.isEmpty()) {
            TextView emptyText = findViewById(R.id.empty_text);
            LinearLayout bottombar = findViewById(R.id.bottom_bar);

            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            bottombar.setVisibility(View.GONE);
            osszar.setText("0 Ft");
        }

        SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
        editor.remove("basketItems");
        editor.apply();
    }
}
