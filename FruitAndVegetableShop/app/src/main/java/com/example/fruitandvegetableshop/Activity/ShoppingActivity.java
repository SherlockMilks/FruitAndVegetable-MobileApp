package com.example.fruitandvegetableshop.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitandvegetableshop.BackgroundService.AlarmReceiver;
import com.example.fruitandvegetableshop.Model.Product;
import com.example.fruitandvegetableshop.Adapter.ProductAdapter;
import com.example.fruitandvegetableshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingActivity extends AppCompatActivity {
    private static final String LOG_TAG=ShoppingActivity.class.getName();
    private static final int KEY=16992;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private Boolean admin_e;
    private RecyclerView recyclerView;
    private ArrayList<Product> items;
    private ProductAdapter adapter;
    private int gridNumber=1;
    private HashMap<Product,Integer> basketItems;
    private EditText maxPriceET;
    private AlarmManager alarmManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            Log.i(LOG_TAG,"Hitelesített felhasználó!");
        }
        else{
            Log.i(LOG_TAG,"Nem hitelesített felhasználó!");
            finish();
        }

        Button addButton=findViewById(R.id.addButton);
        mFirestore=FirebaseFirestore.getInstance();
        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);

        mFirestore.collection("users")
                .whereEqualTo("email",user.getEmail())
                .whereEqualTo("admin",true)
                .orderBy("username")
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if (!task.getResult().isEmpty()) {
                                admin_e=true;
                                addButton.setVisibility(View.VISIBLE);
                                Log.d(LOG_TAG, "A felhaszánló admin");
                            } else {
                                admin_e=false;
                                Log.d(LOG_TAG, "A felhaszánló nem admin.");
                            }

                            recyclerView=findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new GridLayoutManager(ShoppingActivity.this, gridNumber));
                            items=new ArrayList<>();
                            basketItems=new HashMap<>();

                            adapter=new ProductAdapter(ShoppingActivity.this,items,basketItems,admin_e);
                            recyclerView.setAdapter(adapter);

                            loadItems();
                        }else{
                            Log.e(LOG_TAG, "Hiba a felhasználó lekérdezése közben: "+task.getException());
                        }
                    }
                });

        setAlarmManager();
    }

    private void loadItems() {
        items.clear();
        mFirestore.collection("products")
                .orderBy("name")
                .limit(100)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot=task.getResult();
                            for(DocumentSnapshot documentSnapshot:querySnapshot){
                                Product product=documentSnapshot.toObject(Product.class);
                                if(product!=null){
                                    product.setDocumentId(documentSnapshot.getId());
                                    items.add(product);
                                    adapter.notifyDataSetChanged();
                                    Log.d(LOG_TAG, "Termék lekérve: "+product.getName());
                                }
                            }
                        }else{
                            Log.e(LOG_TAG, "Hiba a lekérdezés közben: "+task.getException());
                        }
                    }
                });
    }

    public void priceCheck(View view){
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        maxPriceET=findViewById(R.id.maxPrice);
        if(maxPriceET.getText().toString().isBlank()){
            Toast.makeText(this, "Adjon meg egy maximum árat!", Toast.LENGTH_SHORT).show();
            return;
        }
        int maxPrice=Integer.parseInt(maxPriceET.getText().toString().trim());


        mFirestore.collection("products")
                .whereLessThan("price",maxPrice)
                .orderBy("name")
                .limit(100)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                Toast.makeText(ShoppingActivity.this, "Ennél az árnál nincs olcsóbb termék!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            items.clear();
                            QuerySnapshot querySnapshot=task.getResult();
                            for(DocumentSnapshot documentSnapshot:querySnapshot){
                                Product product=documentSnapshot.toObject(Product.class);
                                if(product!=null){
                                    items.add(product);
                                    product.setDocumentId(documentSnapshot.getId());
                                    adapter.notifyDataSetChanged();
                                    Log.d(LOG_TAG, "Termék lekérve: "+product.getName());
                                }
                            }
                        }else{
                            Log.e(LOG_TAG, "Hiba a lekérdezés közben: "+task.getException());
                        }
                    }
                });
    }

    public void addProduct(View view){
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        Intent intent=new Intent(this, AddProductActivity.class);
        intent.putExtra("KEY",KEY);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        MenuItem menuItem=menu.findItem(R.id.search_bar);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
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
        } else if (item.getItemId()==R.id.cart) {
            Log.i(LOG_TAG, "Kosár megnyitasa");
            for (Map.Entry<Product,Integer> entry:basketItems.entrySet())
            {
                Log.i(LOG_TAG,entry.getKey().getName()+entry.getValue().toString());
            }
            Intent intent=new Intent(this, BasketActivity.class);
            intent.putExtra("termekek", basketItems);
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

    private void setAlarmManager(){
        long repeatInterval=AlarmManager.INTERVAL_HALF_HOUR;
        long triggerTime= SystemClock.elapsedRealtime()+repeatInterval;

        Intent intent=new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent
        );
    }
}
