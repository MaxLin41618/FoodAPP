package com.example.leo.ww2;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Database.Database;
import com.example.leo.ww2.Model.MyResponse;
import com.example.leo.ww2.Model.Notification;
import com.example.leo.ww2.Model.Order;
import com.example.leo.ww2.Model.Request;
import com.example.leo.ww2.Model.Sender;
import com.example.leo.ww2.Model.Token;
import com.example.leo.ww2.Remote.APIService;
import com.example.leo.ww2.ViewHolder.CartAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;

    TextView txtTotalPrice;
    Button buttonPlace;

    FirebaseDatabase database;
    DatabaseReference requests;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //initService
        mService = Common.getFCMService();

        //ActionBar Logo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_shopping_cart_white_24dp);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //FireBase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //initUI
        txtTotalPrice = (TextView)findViewById(R.id.PlaceOrder);
        buttonPlace = (Button)findViewById(R.id.btnPlaceOrder);

        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        buttonPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(cart.size()>0)
                showAlertDialog();
            else
                Toast.makeText(Cart.this,"空的訂單!!!",Toast.LENGTH_SHORT).show();
            }
        });

        loadListFood();
    }


    private void loadListFood(){

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        //刪除修改
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for(Order order:cart){
            //單項價格 * 數量
            total+=( Integer.parseInt(order.getPrice()) )*( Integer.parseInt(order.getQuantity()) );
        }

        Locale locale = new Locale("en","US");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(format.format(total));
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("還差一步!");
        alertDialog.setMessage("請輸入送貨地址或留言: ");
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View order_address_comment = layoutInflater.inflate(R.layout.order_address_comment,null);
        alertDialog.setView(order_address_comment);

        final EditText edtAddress = (EditText) order_address_comment.findViewById(R.id.edtAddress);
        final EditText edtComment = (EditText) order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Create new request
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                edtAddress.getText().toString(),
                                edtComment.getText().toString(),
                                Common.currentUser.getName(),
                                txtTotalPrice.getText().toString(),
                                "0",
                                cart
                );
                        //Submit to FireBase
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number).setValue(request);
                //Delete cart
                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);

                Toast.makeText(Cart.this,"感謝您的訂購",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
   }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){

                    Token serverToken = postSnapShot.getValue(Token.class);
                    //Create raw payload to send
                    Notification notification = new Notification("HEY","You have new Order"+order_number);
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    //Only run when get result
                                    if (response.code() == 200) {
                                        if (response.body().getSuccess() == 1) {
                                            Toast.makeText(Cart.this, "感謝您的訂購", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "感謝您的訂購 但Notification失敗", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //刪除Cart的ContextItemSelected
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    //Cart刪除單個Order
    private void deleteCart(int position){
        //remove form List<Order> by position, 移除單項
        cart.remove(position);
        //delete all old data from SQLite, SQL清空
        new Database(this).cleanCart();
        //update new data from List<Order> to SQLite, 剩餘的重新加回SQL
        for(Order item:cart)
            new Database(this).addToCart(item);
        //refresh
        loadListFood();
    }
}
