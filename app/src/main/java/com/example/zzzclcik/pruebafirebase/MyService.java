package com.example.zzzclcik.pruebafirebase;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Zzzclcik on 21/06/2017.
 */

public class MyService extends Service {
    MediaPlayer myplayer;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private Context context;
    private AlertDialog alert;

    public MyService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Aun no implementado");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Petición  enviada", Toast.LENGTH_SHORT).show();
        myplayer = MediaPlayer.create(this, R.raw.tono);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        validarInternet();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myplayer.stop();
        myplayer.release();
    }

    public boolean validarInternet() {
        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean
                tipoConexion1 = false,
                tipoConexion2 = false,
                hayInternet = false;


        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected()) {
                tipoConexion1 = true;
                hayInternet = true;
            }
            if (mMobile.isConnected()) {
                tipoConexion2 = true;
                hayInternet = true;
            }

            if (tipoConexion1 == false || tipoConexion2 == false) {
               /* Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos */
                hayInternet = false;
                Toast.makeText(getApplicationContext(), "Sin conexion a internet", Toast.LENGTH_SHORT).show();
            }
        } else {
       /* No estas conectado a internet */
            Toast.makeText(getApplicationContext(), "Revise sus conexiónes de datos y wifi", Toast.LENGTH_SHORT).show();
            hayInternet = false;
        }
        return hayInternet;

    }

}
