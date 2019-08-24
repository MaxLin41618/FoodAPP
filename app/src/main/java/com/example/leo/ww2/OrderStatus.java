package com.example.leo.ww2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Interface.ItemClickListener;
import com.example.leo.ww2.Model.Order;
import com.example.leo.ww2.Model.Request;
import com.example.leo.ww2.ViewHolder.OrderViewHolder;
import com.example.leo.ww2.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderStatus extends AppCompatActivity {

    private RecyclerView recyclerView;

    FirebaseRecyclerAdapter<Request,OrderViewHolder>adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        //FireBase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //介面Init
        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //ActionBar Logo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_access_time_white_24dp);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //
        if(getIntent() == null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));

        //currentUser's orders
        loadOrders(Common.currentUser.getPhone());
    }

    private void loadOrders(final String phone){

        Query query = requests.orderByChild("phone").equalTo(Common.currentUser.getPhone());
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(query,Request.class).build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, final int position, @NonNull final Request model) {

                holder.txtOrderId.setText("ID : "+adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText("狀態 : "+Common.convertCodeToStatus(model.getStatus()));
                holder.txtOrderAddress.setText("地址 : "+model.getAddress());
                holder.txtOrderPhone.setText("電話 : "+model.getPhone());
                holder.txtOrderDate.setText("日期 : "+Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent(OrderStatus.this,OrderDetail.class);
                        Common.currentRequest = model;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        orderDetail.putExtra("OrderDate",Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                        startActivity(orderDetail);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // 在返回這頁時發生空白，用這個補回資料
    @Override
    public void onResume(){
        super.onResume();

        loadOrders(Common.currentUser.getPhone());
    }
}
