package com.example.leo.ww2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Model.Request;
import com.example.leo.ww2.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderStatus extends AppCompatActivity {
    private static final String TAG = "OrderStatus";

    private RecyclerView recyclerView;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference refRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        //FireBase
        database = FirebaseDatabase.getInstance();
        refRequests = database.getReference("Requests");

        initView();

        //currentUser's orders
        if (getIntent() == null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void initView() {
        //介面Init
        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //ActionBar Logo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_access_time_white_24dp);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    //load currentUser's orders
    private void loadOrders(final String phone) {

        Query query = refRequests.orderByChild("phone").equalTo(Common.currentUser.getPhone());
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(query, Request.class).build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Request model) {

                holder.txtOrderId.setText("ID : " + adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText("狀態 : " + Common.convertCodeToStatus(model.getStatus()));
                holder.txtOrderAddress.setText("地址 : " + model.getAddress());
                holder.txtOrderPhone.setText("電話 : " + model.getPhone());
                holder.txtOrderDate.setText("日期 : " + Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = model;
                        intent_orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        intent_orderDetail.putExtra("OrderDate", Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                        startActivity(intent_orderDetail);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null)
            adapter.stopListening();
    }

    // 在返回這頁時發生空白，用這個補回資料
    @Override
    public void onResume() {
        super.onResume();

        loadOrders(Common.currentUser.getPhone());
    }
}
