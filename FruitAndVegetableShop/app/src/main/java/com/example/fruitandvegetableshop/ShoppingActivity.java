package com.example.fruitandvegetableshop;

import static android.util.Log.i;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {
    private static final String LOG_TAG=ShoppingActivity.class.getName();
    private FirebaseAuth auth;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private ArrayList<Product> items;
    private ProductAdapter adapter;
    private int gridNumber=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            i(LOG_TAG,"Hitelesített felhasználó!");
        }
        else{
            i(LOG_TAG,"Nem hitelesített felhasználó!");
            finish();
        }

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        items=new ArrayList<>();

        adapter=new ProductAdapter(this,items);
        recyclerView.setAdapter(adapter);

        exampleItems();
    }

    private void exampleItems() {
        String[] name=getResources().getStringArray(R.array.name);
        String[] info=getResources().getStringArray(R.array.info);
        String[] price=getResources().getStringArray(R.array.price);
        TypedArray img=getResources().obtainTypedArray(R.array.img);

        items.clear();

        for (int i = 0; i < name.length; i++) {
            items.add(new Product(img.getResourceId(i,0),price[i],info[i],name[i]));
        }

        img.recycle();
        adapter.notifyDataSetChanged();

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
            finish();
            return true;
        } else if (item.getItemId()==R.id.cart) {
            Log.i(LOG_TAG, "Kosár");
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }
}
