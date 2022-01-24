package com.example.leo.ww2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btSignIn;
    CheckBox ckbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUser = database.getReference("User");

        initView();

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check connection
                if (Common.isConnectedToInternet(getBaseContext())) {

                    //Save user & password
                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();

                    refUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());//設定user電話

                                if (!Boolean.parseBoolean(user.getBlacklist())) {

                                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                                        Intent homeIntent = new Intent(SignIn.this, Home.class);
                                        Common.currentUser = user;
                                        startActivity(homeIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignIn.this, "密碼錯誤!!", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    }
                                } else {
                                    Toast.makeText(SignIn.this, "此帳號已入黑名單!!", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }

                            } else {
                                Toast.makeText(SignIn.this, "無此帳號!!", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(SignIn.this, "請確認網路連線", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        edtPhone = (EditText) findViewById(R.id.edt1);
        edtPassword = (EditText) findViewById(R.id.edt2);
        btSignIn = (Button) findViewById(R.id.bt1);
        ckbRemember = (CheckBox) findViewById(R.id.ckbRemember);
    }
}
