package com.example.fruitandvegetableshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fruitandvegetableshop.Activity.BasketActivity;
import com.example.fruitandvegetableshop.Model.Product;
import com.example.fruitandvegetableshop.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder>{
    private static final String LOG_TAG=BasketAdapter.class.getName();
    private List<Map.Entry<Product, Integer>> basketItems;
    private Context context;
    private Integer lastPos=-1;
    private OnBasketChangeListener listener;
    private BasketActivity basketActivity;

    public BasketAdapter(Context context, HashMap<Product, Integer> basketItems, BasketActivity basketActivity, OnBasketChangeListener listener) {
        this.context = context;
        this.basketItems = new ArrayList<>(basketItems.entrySet());
        this.basketActivity=basketActivity;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.basketitem,parent,false));
    }

    @Override
    public void onBindViewHolder(BasketAdapter.ViewHolder holder, int position) {
        Map.Entry<Product,Integer> currentProduct=basketItems.get(position);

        holder.bindTo(currentProduct);

        if(holder.getAdapterPosition()>lastPos){
            Animation animation= AnimationUtils.loadAnimation(context,R.anim.slide_in);
            holder.itemView.startAnimation(animation);
            lastPos=holder.getAdapterPosition();
        }

        holder.removeButton.setOnClickListener( v -> {
            Animation bounce = AnimationUtils.loadAnimation(context, R.anim.button);
            v.startAnimation(bounce);

            Map.Entry<Product,Integer> deletedProduct=basketItems.get(position);

            basketItems.remove(position);
            basketActivity.removeItem(deletedProduct.getKey());
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, basketItems.size());

            if(listener!=null && deletedProduct!=null){
                listener.onBasketUpdated(basketItems.size(),currentProduct);
            }
        });

        holder.plusButton.setOnClickListener(v ->{
            Animation bounce = AnimationUtils.loadAnimation(context, R.anim.button);
            v.startAnimation(bounce);

            Map.Entry<Product,Integer> product=basketItems.get(position);

            product.setValue(product.getValue()+1);
            listener.priceChange(product,1);

            notifyItemChanged(position);
        });

        holder.minusButton.setOnClickListener(v ->{
            Animation bounce = AnimationUtils.loadAnimation(context, R.anim.button);
            v.startAnimation(bounce);

            Map.Entry<Product,Integer> product=basketItems.get(position);


            if(product.getValue()<=1){
                basketItems.remove(position);
                basketActivity.removeItem(product.getKey());
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, basketItems.size());

                if(listener!=null){
                    listener.onBasketUpdated(basketItems.size(),currentProduct);
                }
            }
            else{
                product.setValue(product.getValue()-1);
                listener.priceChange(product,-1);
                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return basketItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView amount;
        private TextView info;
        private TextView price;
        private ImageView img;
        private Button removeButton;
        private Button plusButton;
        private Button minusButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.amount);
            name=itemView.findViewById(R.id.name);
            info=itemView.findViewById(R.id.info);
            price=itemView.findViewById(R.id.price);
            img=itemView.findViewById(R.id.img);
            removeButton=itemView.findViewById(R.id.remove);
            plusButton=itemView.findViewById(R.id.increase_button);
            minusButton=itemView.findViewById(R.id.decrease_button);
        }
        public void bindTo(Map.Entry<Product,Integer> currentProduct) {
            amount.setText(currentProduct.getValue()+"kg");
            name.setText(currentProduct.getKey().getName());
            info.setText(currentProduct.getKey().getInfo());
            price.setText(currentProduct.getKey().getPrice()+" Ft/kg");
            img.setImageResource(currentProduct.getKey().getImgres());
            img.setTag(currentProduct.getKey().getImgres());

            Glide.with(context)
                    .load(currentProduct.getKey().getImgres())
                    .into(img);
        }
    }

    public interface OnBasketChangeListener{
        void onBasketUpdated(int itemCount, Map.Entry<Product,Integer> basketItem);

        void priceChange(Map.Entry<Product,Integer> basketItem, int value);
    }
}



