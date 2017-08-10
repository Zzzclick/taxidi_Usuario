package com.example.zzzclcik.pruebafirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ValidatorUtil {

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private Context context;
    private AlertDialog alert;

    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public ValidatorUtil(Context context)
    {
        this.context = context;
        connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    public static boolean validateEmail(String email) {

        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public boolean isOnline() {

        RunnableFuture<Boolean> futureRun = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if(networkInfo.isConnectedOrConnecting()) {
                    if ((networkInfo.isAvailable()) && (networkInfo.isConnected())) {
                        try {
                            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                            urlc.setRequestProperty("User-Agent", "Test");
                            urlc.setRequestProperty("Connection", "close");
                            urlc.setConnectTimeout(700);
                            urlc.connect();
                            return (urlc.getResponseCode() == 200);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error al comprobar la conexión a Internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(context,"¡No hay red disponible!",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        new Thread(futureRun).start();

        try {
            return futureRun.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean validarInternet()
    {
        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean
        tipoConexion1 = false,
        tipoConexion2 = false,
        hayInternet=false;


        if (ni != null)
        {
            ConnectivityManager connManager1 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected())
            {
                tipoConexion1 = true;
                hayInternet= true;
            }
            if (mMobile.isConnected())
            {
                tipoConexion2 = true;
                hayInternet= true;
            }

            if (tipoConexion1 == false || tipoConexion2 == false)
            {
               /* Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos */
                hayInternet= false;
                Toast.makeText(getApplicationContext(),"Sin conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
       /* No estas conectado a internet */
        Toast.makeText(getApplicationContext(),"Revise sus conexiónes de datos y wifi", Toast.LENGTH_SHORT).show();
        hayInternet=false;
        }
        return hayInternet;
    }




}
