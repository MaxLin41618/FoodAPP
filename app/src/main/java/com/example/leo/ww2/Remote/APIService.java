package com.example.leo.ww2.Remote;

import com.example.leo.ww2.Model.MyResponse;
import com.example.leo.ww2.Model.Message;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAXyTjHxU:APA91bFdHEf6YUVOV35qtpy8t4j3gbxxHx7wDtu6ZzHQO2n2tPLnyyjTBnvxBlmeSyIkuYWPRwCed9rCeKkD_ItQQNe-c6pLhFcWVZhmtVR09ekLt4bsCanZh7SbSA9dQv_0neaEnIr0BPg8vYk4loW8f4piA1Vp5g"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Message body); //POST 帶有內容的
}
