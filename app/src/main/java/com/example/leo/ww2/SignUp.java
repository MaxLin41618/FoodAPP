package com.example.leo.ww2;

import android.app.ProgressDialog;
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

public class SignUp extends AppCompatActivity {

    EditText edtPhone,edtName,edtPassword,edtEmail;
    Button btnRegister;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference table_user = database.getReference("User");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        edtPhone = (EditText) findViewById(R.id.editTextPhone);
        edtName = (EditText) findViewById(R.id.editTextName);
        edtPassword = (EditText) findViewById(R.id.editTextPassword);
        edtEmail = (EditText) findViewById(R.id.editTextEmail);



        btnRegister.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (Common.isConnectedToInternet(getBaseContext())) {

                        final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                        mDialog.setMessage("Please waiting...");
                        mDialog.show();

                        if (edtPhone.length() == 10 && edtName.length() >= 2 && edtPassword.length() > 0 && edtEmail.length() > 0) {

                            table_user.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                        mDialog.dismiss();
                                        Toast.makeText(SignUp.this, "已經註冊!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mDialog.dismiss();
                                        //建構方法User
                                        User user = new User(
                                                edtName.getText().toString(),
                                                edtPassword.getText().toString(),
                                                edtEmail.getText().toString()
                                        );

                                        table_user.child(edtPhone.getText().toString()).setValue(user); //user底下的child
                                        Toast.makeText(SignUp.this, "註冊成功!!", Toast.LENGTH_SHORT).show();
                                        /* finish();*/
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else if (edtPhone.length() < 10) {
                            Toast.makeText(SignUp.this, "電話輸入錯誤", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else if (edtName.getText().toString().isEmpty() || edtName.length() < 2) {
                            Toast.makeText(SignUp.this, "請輸入姓名(2字以上)", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else if (edtPassword.getText().toString().isEmpty()) {
                            Toast.makeText(SignUp.this, "請輸入密碼", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else if (edtEmail.getText().toString().isEmpty()) {
                            Toast.makeText(SignUp.this, "請輸入Email", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                    else{
                        Toast.makeText(SignUp.this,"請確認網路連線",Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }
}