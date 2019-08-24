package com.example.leo.ww2.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.leo.ww2.Model.Request;
import com.example.leo.ww2.Model.User;
import com.example.leo.ww2.Remote.APIService;
import com.example.leo.ww2.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static User currentUser;
    public static Request currentRequest;

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static String PHONE_TEXT = "userPhone";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    //pick Image
    public static final int PICK_IMAGE_REQUEST = 71;

    //
    public static String INTENT_FOOD_ID = "FoodId";

    //remember user
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    //status
    public static String convertCodeToStatus(String status){
        if(status.equals("0"))
            return "訂購中";
        else if(status.equals("1"))
            return "製作中";
        else
            return "完成";
    }

    public static final String DELETE = "刪除";

    //date
    public static String getDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm",calendar).toString());

        return date.toString();
    }

    //確認網路連線
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager!=null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info!=null){
                for (int i=0;i<info.length;i++){
                    if (info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

}
