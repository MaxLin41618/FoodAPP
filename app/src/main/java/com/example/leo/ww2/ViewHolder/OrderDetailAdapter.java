package com.example.leo.ww2.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leo.ww2.Model.Order;
import com.example.leo.ww2.R;

import java.util.List;
//ViewHolder=========================
class  MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name,quantity,price;

    public MyViewHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.product_name);
        quantity = (TextView)itemView.findViewById(R.id.product_quantity);
        price = (TextView)itemView.findViewById(R.id.product_price);
    }
}
//Adapter================================
public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.name.setText(String.format("商品名稱 : %s",myOrders.get(position).getProductName()));
        holder.quantity.setText(String.format("數量 : %s",myOrders.get(position).getQuantity()));
        holder.price.setText(String.format("價格 : %s",myOrders.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
