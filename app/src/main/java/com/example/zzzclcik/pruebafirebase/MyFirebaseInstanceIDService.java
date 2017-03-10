package com.example.zzzclcik.pruebafirebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Zzzclcik on 24/02/2017.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{/*
    @Override
    public void onTokenRefresh() {
        String token= FirebaseInstanceId.getInstance().getToken();
        registerToken(token);
        System.out.println("Si lo hace5555555555555555");
    }

    private void registerToken(String token) {
        OkHttpClient client=new OkHttpClient();
        RequestBody body=new FormBody.Builder().add("Token",token).build();
        Request request=new Request.Builder().url("candymecha.webcindario.com/register.php").post(body).build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {

            System.out.println("aqui888888888"+ e);
        }

    }*/
}