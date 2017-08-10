package com.example.zzzclcik.pruebafirebase;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Zzzclcik on 21/06/2017.
 */

public class MyService extends Service {
    MediaPlayer myplayer;

    public MyService()
    {

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Aun no implementado");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(),"Petición  enviada", Toast.LENGTH_SHORT).show();
       myplayer=MediaPlayer.create(this,R.raw.tono);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myplayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myplayer.stop();
        myplayer.release();
    }
}
