package com.example.leo.ww2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Database.Database;
import com.example.leo.ww2.Interface.ItemClickListener;
import com.example.leo.ww2.Model.Food;
import com.example.leo.ww2.Model.Order;
import com.example.leo.ww2.Model.Store;
import com.example.leo.ww2.ViewHolder.FoodViewHolder;
import com.example.leo.ww2.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference foodList;

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    FloatingActionButton btnCart;

    String storeId="";

    //For Search Function
    MaterialSearchBar searchBar;
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //ActionBar Logo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_restaurant_menu_white_24dp);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodList.this, Cart.class));
            }
        });

        //Get Home傳來的storeId
        if(getIntent()!=null)
            storeId = getIntent().getStringExtra("StoreId");
        if(!storeId.isEmpty() && storeId != null){
            if (Common.isConnectedToInternet(this))
                loadListFood(storeId);
            else
                Toast.makeText(FoodList.this,"請確認網路連線",Toast.LENGTH_SHORT).show();
        }

        searchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        //load suggestion from FireBase
        loadSuggest();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user type text, we will change suggest list
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList){
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When Search is close
                //restore original adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish
                //Show result of search adapter
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }
    //Search Confirmed
    private void startSearch(CharSequence text) {
        //抓這個名字的
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("name").equalTo(text.toString()),Food.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.food_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(holder.food_image);

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("foodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                View view = inflater.inflate(R.layout.food_item,viewGroup,false);
                return new FoodViewHolder(view);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter); //Set recyclerView adapter to this SearchAdapter
    }

    //load suggestion from FireBase
    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(storeId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    //loadFoodList
    private void loadListFood(final String storeId) {

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("menuId").equalTo(storeId),Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                    holder.food_name.setText(model.getName());
                    Picasso.with(getBaseContext())
                            .load(model.getImage())
                            .into(holder.food_image);

                    final Food local = model;
                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                            foodDetail.putExtra("foodId",adapter.getRef(position).getKey());
                            startActivity(foodDetail);
                        }
                    });
                }
                @NonNull
                @Override
                public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                    View view = inflater.inflate(R.layout.food_item,parent,false);
                    return new FoodViewHolder(view);
                }
            };
        adapter.startListening();
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

        loadListFood(storeId);
    }
}
