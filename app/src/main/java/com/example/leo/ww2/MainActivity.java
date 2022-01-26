package com.example.leo.ww2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btn_logIn, btn_user_policy, btn_signUp, btn_conUs;
    //Firebase
    FirebaseDatabase database;
    DatabaseReference refUser;
    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        refUser = database.getReference("User");

        initView();

        //Init Paper
        Paper.init(this);
        //Check remember 自動登入
        String user_phone = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user_phone != null && pwd != null) {
            if (!user_phone.isEmpty() && !pwd.isEmpty()) {
                login(user_phone, pwd); //登入
            }
        }
    }

    private void initView() {
        btn_logIn = (Button) findViewById(R.id.btn_logIn);
        btn_user_policy = (Button) findViewById(R.id.btn_user_policy);
        btn_signUp = (Button) findViewById(R.id.btn_signUp);
        btn_conUs = (Button) findViewById(R.id.btn_conUs);
    }

    private void login(final String user_phone, final String pwd) {

        final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        eventListener = refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(user_phone).exists()) {

                    User user = dataSnapshot.child(user_phone).getValue(User.class);
                    user.setPhone(user_phone);//自動登入時取得user電話

                    if (user.getPassword().equals(pwd)) {
                        Intent homeIntent = new Intent(MainActivity.this, Home.class);
                        Common.currentUser = user;
                        startActivity(homeIntent);
                        mDialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "密碼錯誤!!", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "無此帳號!!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void conUs(View v) {
        startActivity(new Intent(this, ContUs.class));
    }

    public void signUp(View v) {
        startActivity(new Intent(this, SignUp.class));
    }

    public void signIn(View v) {
        startActivity(new Intent(this, SignIn.class));
    }

    public void userPolicy(View v) {
        startActivity(new Intent(this, UserPolicy.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            refUser.removeEventListener(eventListener);
        }
    }
}