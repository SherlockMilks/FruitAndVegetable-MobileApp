package com.example.fruitandvegetableshop.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fruitandvegetableshop.Activity.ModifyProductActivity;
import com.example.fruitandvegetableshop.Activity.ShoppingActivity;
import com.example.fruitandvegetableshop.Model.Product;
import com.example.fruitandvegetableshop.R;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    private static final String LOG_TAG=ProductAdapter.class.getName();
    private static final int KEY=145200;
    private ArrayList<Product> products;
    private ArrayList<Product> productsFiltered;
    private HashMap<Product, Integer> basketItems;
    private Context context;
    private Integer lastPos=-1;
    private Boolean admin_e;
    private FirebaseFirestore mFirestore;

    public ProductAdapter(Context context, ArrayList<Product> productsData, HashMap<Product, Integer> basketItems, Boolean admin_e){
        products=productsData;
        productsFiltered=productsData;
        this.context=context;
        this.basketItems=basketItems;
        this.admin_e=admin_e;
        mFirestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.product,parent,false));
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        Product currentProduct=productsFiltered.get(position);

        holder.bindTo(currentProduct);

        if(holder.getAdapterPosition()>lastPos){
            Animation animation= AnimationUtils.loadAnimation(context,R.anim.slide_in);
            holder.itemView.startAnimation(animation);
            lastPos=holder.getAdapterPosition();
        }

        holder.addToCartButton.setOnClickListener(v -> {
            Animation bounce = AnimationUtils.loadAnimation(context, R.anim.button);
            v.startAnimation(bounce);


            int quantity;
            try {
                quantity = Integer.parseInt(holder.amountEditText.getText().toString().trim());
            } catch (NumberFormatException e) {
                quantity = 0;
            }

            if (quantity <= 0) {
                Toast.makeText(v.getContext(), "Adj meg legalább 1 darabot!", Toast.LENGTH_SHORT).show();
                return;
            }

            Product product = new Product(
                    (Integer) holder.img.getTag(),
                    Integer.parseInt(holder.price.getText().toString().split(" ")[0]),
                    holder.info.getText().toString(),
                    holder.name.getText().toString()
            );


            if (basketItems.containsKey(product)) {
                basketItems.put(product, basketItems.get(product) + quantity);
            } else {
                basketItems.put(product, quantity);
            }
            Toast.makeText(v.getContext(), "Hozzáadva: " + product.getName() + " x" + quantity, Toast.LENGTH_SHORT).show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            Animation bounce = AnimationUtils.loadAnimation(context, R.anim.button);
            v.startAnimation(bounce);

            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            mFirestore.collection("products")
                    .document(currentProduct.getDocumentId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Törölve!", Toast.LENGTH_SHORT).show();

                        products.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(LOG_TAG, "Hiba törlés közben: " + e.getMessage());
                    });
        });


        holder.updateButton.setOnClickListener(v -> {
            Animation bounce = AnimationUtils.loadAnimation(context, R.anim.button);
            v.startAnimation(bounce);

            Intent intent=new Intent(context, ModifyProductActivity.class);
            intent.putExtra("KEY",KEY);
            intent.putExtra("product",currentProduct);

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return productsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Product> filteredProducts=new ArrayList<>();
            FilterResults filterResults=new FilterResults();

            if(constraint==null ||  constraint.length()==0){
                filterResults.count=products.size();
                filterResults.values=products;
            }
            else {
                String filter=constraint.toString().toLowerCase();

                for (Product product:products) {
                    if(product.getName().toLowerCase().contains(filter)){
                        filteredProducts.add(product);
                    }
                }
                filterResults.count=filteredProducts.size();
                filterResults.values=filteredProducts;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productsFiltered=(ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView info;
        private TextView price;
        private ImageView img;
        private EditText amountEditText;
        private Button addToCartButton;
        private Button deleteButton;
        private Button updateButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name);
            info=itemView.findViewById(R.id.info);
            price=itemView.findViewById(R.id.price);
            img=itemView.findViewById(R.id.img);
            amountEditText = itemView.findViewById(R.id.amount);
            addToCartButton = itemView.findViewById(R.id.add_to_cart);
            deleteButton = itemView.findViewById(R.id.delete);
            updateButton = itemView.findViewById(R.id.modify);

            if(admin_e){
                updateButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
            }else{
                updateButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            }
        }

        public void bindTo(Product currentProduct) {
            name.setText(currentProduct.getName());
            info.setText(currentProduct.getInfo());
            price.setText(currentProduct.getPrice()+" Ft/kg");
            img.setImageResource(currentProduct.getImgres());
            img.setTag(currentProduct.getImgres());

            Glide.with(context)
                    .load(currentProduct.getImgres())
                    .into(img);
        }
    }
}


