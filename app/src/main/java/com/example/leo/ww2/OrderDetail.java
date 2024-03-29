package com.example.leo.ww2;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView order_id, order_name, order_phone, order_address, order_total, order_comment, order_date;
    String order_id_value = "";
    String order_date_value = "";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initView();

        if (getIntent() != null) {
            order_id_value = getIntent().getStringExtra("OrderId");
        }

        order_id.setText("ID : " + order_id_value);
        order_name.setText("姓名 : " + Common.currentRequest.getName());
        order_phone.setText("電話 : " + Common.currentRequest.getPhone());
        order_total.setText("總額 : " + Common.currentRequest.getTotal());
        order_address.setText("地址 : " + Common.currentRequest.getAddress());
        order_comment.setText("留言 : " + Common.currentRequest.getComment());

        //adapter get 此訂單 的 list
        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void initView() {
        //ActionBar Logo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_restaurant_menu_white_24dp);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //initUI
        order_id = (TextView) findViewById(R.id.order_id);
        order_name = (TextView) findViewById(R.id.order_name);
        order_phone = (TextView) findViewById(R.id.order_phone);
        order_total = (TextView) findViewById(R.id.order_total);
        order_address = (TextView) findViewById(R.id.order_address);
        order_comment = (TextView) findViewById(R.id.order_comment);

        recyclerView = (RecyclerView) findViewById(R.id.listFoods);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
