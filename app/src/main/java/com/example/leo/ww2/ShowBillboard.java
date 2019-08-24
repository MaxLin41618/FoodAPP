package com.example.leo.ww2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leo.ww2.Model.Billboard;
import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Model.User;
import com.example.leo.ww2.ViewHolder.ShowBillboardViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowBillboard extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference billboardTbl;

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Billboard,ShowBillboardViewHolder> adapter;

    SwipeRefreshLayout bSwipeRefreshLayout;

    String UserPhone = "";
    String UserName = "";

    //Ctrl + O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/of.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_billboard);

        //FireBase
        database = FirebaseDatabase.getInstance();
        billboardTbl = database.getReference("Billboard");
        //recyclerView
        recyclerView = (RecyclerView)findViewById(R.id.recyclerBillboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Swipe Layout
        bSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_Layout_billboard);
        bSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if(getIntent() != null) {
                    UserPhone = getIntent().getStringExtra("UserPhone");
                    UserName = getIntent().getStringExtra("UserName");
                }
                if(!UserPhone.isEmpty() && UserPhone != null){
                    //Query billboard ALL
                    Query query = billboardTbl;
                    FirebaseRecyclerOptions<Billboard> options = new FirebaseRecyclerOptions.Builder<Billboard>().setQuery(query,Billboard.class).build();

                    adapter = new FirebaseRecyclerAdapter<Billboard, ShowBillboardViewHolder>(options) {
                        /*//Sorting newest billboard data
                        @Override
                        public Billboard getItem(int position) {
                            return super.getItem(adapter.getItemCount() - position - 1);
                        }*/
                        @Override
                        protected void onBindViewHolder(@NonNull ShowBillboardViewHolder holder, int position, @NonNull Billboard model) {
                            Picasso.with(getBaseContext())
                                    .load(model.getBillboard_image())
                                    .into(holder.billboard_image);
                            holder.billboard_date.setText("日期 : "+Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                            holder.billboard_name.setText("姓名 : "+model.getBillboard_userName());
                            holder.billboard_title.setText("標題 : "+model.getBillboard_title());
                            holder.billboard_comment.setText("內容 : "+model.getBillboard_comment());
                        }

                        @NonNull
                        @Override
                        public ShowBillboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                            View view = inflater.inflate(R.layout.show_billboard_layout,parent,false);
                            return new ShowBillboardViewHolder(view);
                        }
                    };
                    loadBillboard();
                }
            }
        });

        //load billboard on first launch
        bSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                bSwipeRefreshLayout.setRefreshing(true);

                if(getIntent() != null){
                    UserPhone = getIntent().getStringExtra("UserPhone");
                    UserName = getIntent().getStringExtra("UserName");
                }
                if(!UserPhone.isEmpty() && UserPhone != null){
                    //Query request ALL
                    Query query = billboardTbl;
                    FirebaseRecyclerOptions<Billboard> options = new FirebaseRecyclerOptions.Builder<Billboard>().setQuery(query, Billboard.class).build();

                    adapter = new FirebaseRecyclerAdapter<Billboard, ShowBillboardViewHolder>(options) {
                    /*    //Sorting newest billboard data
                        @Override
                        public Billboard getItem(int position) {
                            return super.getItem(adapter.getItemCount() - position - 1);
                        }*/
                        @Override
                        protected void onBindViewHolder(@NonNull ShowBillboardViewHolder holder, int position, @NonNull Billboard model) {
                            Picasso.with(getBaseContext())
                                    .load(model.getBillboard_image())
                                    .into(holder.billboard_image);
                            holder.billboard_date.setText("日期 : "+Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                            holder.billboard_name.setText("姓名 : "+model.getBillboard_userName());
                            holder.billboard_title.setText("標題 : "+model.getBillboard_title());
                            holder.billboard_comment.setText("內容 : "+model.getBillboard_comment());
                        }

                        @NonNull
                        @Override
                        public ShowBillboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_billboard_layout,parent,false);
                            return new ShowBillboardViewHolder(view);
                        }
                    };
                    loadBillboard();
                }
            }
        });
    }

    private void loadBillboard() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        bSwipeRefreshLayout.setRefreshing(false);
    }
}
