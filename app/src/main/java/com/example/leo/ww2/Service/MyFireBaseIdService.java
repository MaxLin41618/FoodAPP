package com.example.leo.ww2.Service;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFireBaseIdService extends FirebaseInstanceIdService {
    //自動監聽改變
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken(); //拿到最新自動產生的Token
        if (Common.currentUser != null)
            updateTokenToFirebase(tokenRefreshed); //上傳到Firebase db
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference refTokens = db.getReference("Tokens");

        Token token = new Token(tokenRefreshed, false); // false because this token send from client
        refTokens.child(Common.currentUser.getPhone()).setValue(token);// 這支電話加入 token 跟 false
    }
}
