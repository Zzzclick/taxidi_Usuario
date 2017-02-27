package com.example.zzzclcik.pruebafirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import okhttp3.*;

public class ActivityPushServer extends AppCompatActivity {
public TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_server);
        txt=(TextView) findViewById(R.id.InfoTextView);
        if(getIntent().hasExtra("result"))
        {
            txt.setText(getIntent().getStringExtra("result"));
            return;
        }
        String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Mi token es "+token);
        registerToken(token);
    }

    private void registerToken(String token)
    {
        OkHttpClient client=new OkHttpClient();
        RequestBody body=new FormBody.Builder().add("token",token).build();
       // Request request =new Request.Builder().url()
    }
}
