package com.example.fruitandvegetableshop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    private static final String LOG_TAG=ProductAdapter.class.getName();
    private ArrayList<Product> products;
    private ArrayList<Product> productsFiltered;
    private Context context;
    private Integer lastPos=-1;

    ProductAdapter(Context context, ArrayList<Product> productsData){
        products=productsData;
        productsFiltered=productsData;
        this.context=context;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name);
            info=itemView.findViewById(R.id.info);
            price=itemView.findViewById(R.id.price);
            img=itemView.findViewById(R.id.img);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation bounce = AnimationUtils.loadAnimation(context, R.anim.button);
                    itemView.startAnimation(bounce);
                    Log.i(LOG_TAG, "Termék kosárba helyezve!");
                }
            });
        }

        public void bindTo(Product currentProduct) {
            name.setText(currentProduct.getName());
            info.setText(currentProduct.getInfo());
            price.setText(currentProduct.getPrice());

            Glide.with(context)
                    .load(currentProduct.getImgres())
                    .into(img);
        }
    }
}


