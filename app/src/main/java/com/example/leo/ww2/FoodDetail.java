package com.example.leo.ww2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Database.Database;
import com.example.leo.ww2.Model.Food;
import com.example.leo.ww2.Model.Order;
import com.example.leo.ww2.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fabAddToCart, fabRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;
    Button btnShowComment;
    //getExtra
    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference refFoods, refRating;
    ValueEventListener listenerRefFoods, listenerRefRating;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //init Firebase
        database = FirebaseDatabase.getInstance();
        refFoods = database.getReference("Food");
        refRating = database.getReference("Rating");

        initView();

        if (getIntent() != null)
            foodId = getIntent().getStringExtra("foodId");
        if (!foodId.isEmpty()) {
            //check Internet
            if (Common.isConnectedToInternet(this)) {
                getDetailFood(foodId);  //取得這個Food-key的detail
                getRatingFood(foodId);  //取得rating
            } else
                Toast.makeText(FoodDetail.this, "請確認網路連線", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        fabAddToCart = (FloatingActionButton) findViewById(R.id.btnCart);
        fabRating = (FloatingActionButton) findViewById(R.id.btnRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnShowComment = (Button) findViewById(R.id.btnShowComment);
        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);
        food_image = (ImageView) findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetail.this, ShowComment.class);
                intent.putExtra(Common.INTENT_FOOD_ID, foodId);
                startActivity(intent);
            }
        });

        fabRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        fabAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(
                        new Order(
                                foodId,
                                currentFood.getName(),
                                numberButton.getNumber(),
                                currentFood.getPrice()
                        ));

                Toast.makeText(FoodDetail.this, "已加入購物車", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //抓資料表裡全部這個食物的評分, 算平均評分
    private void getRatingFood(final String foodId) {
        Query foodRating = refRating.orderByChild("foodId").equalTo(foodId);

        listenerRefRating = foodRating.addValueEventListener(new ValueEventListener() {
            int sum = 0, count = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Rating rating = child.getValue(Rating.class);
                    sum += Integer.parseInt(rating.getRateValue());
                    count++;
                }

                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //fabRating
    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("傳送")
                .setNegativeButtonText("取消")
                .setNoteDescriptions(Arrays.asList("非常不滿意", "不滿意", "普通", "滿意", "非常滿意"))
                .setDefaultRating(3)
                .setTitle("請給評分!")
                .setDescription("選擇分數並給予評論")
                .setTitleTextColor(R.color.colorPrimary)
                .setHint("請寫下您的評論")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.black)
                .setCommentBackgroundColor(R.color.commentbackground)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        listenerRefFoods = refFoods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //showRatingDialog的接受評價 上傳到FireBase
    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        //接受評價 上傳到FireBase
        final Rating rating = new Rating(
                Common.currentUser.getName(),
                Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments
        );
        //一直新增uni-key
        refRating.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this, "感謝您的評分!!", Toast.LENGTH_SHORT).show();
                    }
                });
        //底下失敗在只能查到currentUser Rating
        /*
        refRating.child(Common.currentUser.getPhone()).child(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).child(foodId).exists()){
                    // Remove old Value
                    refRating.child(Common.currentUser.getPhone()).child(foodId).removeValue();
                    //Update new Value
                    refRating.child(Common.currentUser.getPhone()).child(foodId).setValue(rating);
                }
                else {
                    //Update new Value
                    refRating.child(Common.currentUser.getPhone()).child(foodId).setValue(rating);
                }
                Toast.makeText(FoodDetail.this,"感謝您的評分!!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
    }

    //showRatingDialog
    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refFoods != null) {
            refFoods.removeEventListener(listenerRefFoods);
        }
        if (refRating != null) {
            refRating.removeEventListener(listenerRefRating);
        }
    }
}
