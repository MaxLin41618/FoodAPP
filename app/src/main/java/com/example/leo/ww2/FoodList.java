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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Interface.ItemClickListener;
import com.example.leo.ww2.Model.Food;
import com.example.leo.ww2.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    private static final String TAG = "FoodList";

    FirebaseDatabase database;
    DatabaseReference refFood;
    ValueEventListener eventListener;

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FloatingActionButton btnCart;
    //getExtra
    String storeId = "";

    //For Search Function
    MaterialSearchBar searchBar;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        database = FirebaseDatabase.getInstance();
        refFood = database.getReference("Food");

        initView();

        //Get Home傳來的storeId
        if (getIntent() != null)
            storeId = getIntent().getStringExtra("StoreId");
        if (!storeId.isEmpty() && storeId != null) {
            if (Common.isConnectedToInternet(this))
                loadListFood(storeId);
            else
                Toast.makeText(FoodList.this, "請確認網路連線", Toast.LENGTH_SHORT).show();
        }

        //load suggestion from FireBase, put food_name to suggestList, maxCount 10
        loadSuggest();
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        //searchBar.setLastSuggestions(suggestList);
        //suggestList 建議欄
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user type text and it contains in suggestList, will change the suggest list
                List<String> suggests = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                        suggests.add(search);
                        Log.d(TAG, "onTextChanged: " + suggests);
                        Log.d(TAG, "onTextChanged: " + searchBar.getText());
                    }
                }
                //searchBar.setLastSuggestions(suggests); //change the suggest list
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //When Search Confirmed
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
                Log.d(TAG, "onSearchConfirmed: ");
                //When search finish. Show result of search adapter
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                searchBar.setText(searchBar.getLastSuggestions().get(position).toString());
                startSearch(searchBar.getLastSuggestions().get(position).toString());
                searchBar.hideSuggestionsList();
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                ArrayList newList = new ArrayList<>(searchBar.getLastSuggestions());
                newList.remove(position);

                searchBar.updateLastSuggestions(newList);
            }
        });
    }

    //initView
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //ActionBar Logo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_restaurant_menu_white_24dp);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodList.this, Cart.class));
            }
        });
    }

    //Search Confirmed
    private void startSearch(CharSequence text) {
        //抓這個名字的
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(refFood.orderByChild("name").equalTo(text.toString()), Food.class)
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
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("foodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                View view = inflater.inflate(R.layout.food_item, viewGroup, false);
                return new FoodViewHolder(view);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    //load suggestion from FireBase, put food_name to suggestList, maxCount 10
    private void loadSuggest() {
        eventListener = refFood.orderByChild("menuId").equalTo(storeId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int counter = 1;
                        // put food_name to suggestList
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Food food = ds.getValue(Food.class);
                            suggestList.add(food.getName());
                            Log.d(TAG, "onDataChange:loadSuggest count " + counter);
                            counter++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    //loadFood from db to adapter
    private void loadListFood(final String storeId) {

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(refFood.orderByChild("menuId").equalTo(storeId), Food.class)
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
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("foodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.food_item, parent, false);
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
    public void onResume() {
        super.onResume();

        loadListFood(storeId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            refFood.removeEventListener(eventListener);
        }
    }
}
