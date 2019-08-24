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

    Button bt1,bt2,bt3,bt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        bt3 = (Button) findViewById(R.id.bt3);
        bt4 = (Button) findViewById(R.id.bt4);

        //Init Paper
        Paper.init(this);
        //Check remember
        String user_phone = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if(user_phone != null && pwd != null){
            if(!user_phone.isEmpty() && !pwd.isEmpty()){
                login(user_phone,pwd);
            }
        }
    }

    private void login(final String user_phone, final String pwd) {
        //Login in code

        final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(user_phone).exists()){

                    User user = dataSnapshot.child(user_phone).getValue(User.class);
                    user.setPhone(user_phone);//取得user電話

                    if (user.getPassword().equals(pwd)) {
                        Intent homeIntent = new Intent(MainActivity.this,Home.class);
                        Common.currentUser = user;
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "密碼錯誤!!", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "無此帳號!!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void conus(View v){
        startActivity(new Intent(this, ContUs.class));
    }
    public void signup(View v){
        startActivity(new Intent(this, SignUp.class));
    }
    public void signin(View v){
        startActivity(new Intent(this, SignIn.class));
    }
    public void userpolicy(View v){
        startActivity(new Intent(this, UserPolicy.class));
    }

}