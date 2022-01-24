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

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Model.Rating;
import com.example.leo.ww2.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference refRating;

    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;

    String foodId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/of.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_show_comment);

        //FireBase
        database = FirebaseDatabase.getInstance();
        refRating = database.getReference("Rating");

        initView();

        //refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadComment(foodId);
            }
        });

        //load comment on first launch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                loadComment(foodId);
            }
        });
    }

    private void initView() {
        //recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerComment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        //Swipe Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Layout);
    }

    private void loadComment(String foodId) {

        if (getIntent() != null)
            foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);

        if (!foodId.isEmpty() && foodId != null) {
            //Query request
            Query query = refRating.orderByChild("foodId").equalTo(foodId);

            FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>().setQuery(query, Rating.class).build();

            adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                    holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                    holder.txtComment.setText(model.getComment());
                    holder.txtUserName.setText(model.getUserName());
                }

                @NonNull
                @Override
                public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                    View view = inflater.inflate(R.layout.show_comment_layout, parent, false);
                    return new ShowCommentViewHolder(view);
                }
            };

            adapter.startListening();
            recyclerView.setAdapter(adapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    //Ctrl+O, font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null)
            adapter.stopListening();
    }
}
