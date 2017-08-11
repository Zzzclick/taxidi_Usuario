package com.example.zzzclcik.pruebafirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class MapsActivityTaxi extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;


    public String escuchador1, escuchador2,telefono;
    public double latA, lonA,latUser,lonUser;
    public ImageView mapaH,mapaN,cancelar,terminar,Imagentelefono;
    String idTaxi,idUsuario,nomUsuario;
    String escuchadorUsu,escuchadorUsuP;
    ValueEventListener listener1, listener2;
    ValidatorUtil validatorUtil = null;
    AlertDialog alert = null;
    private DatabaseReference mDatabase;
    boolean aux=false,auxT=false, viajeCancelado = false, viajeTerminado = false;

    private int contador=0;


    ObtenerWebService hiloconexion;
    public boolean auxMapNormal=true;
    LocationManager locationManager;
    double longitudeBest, latitudeBest;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in, R.anim.right_in);
        try {

            setContentView(R.layout.activity_maps_taxi);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsActualizacion();


        validatorUtil = new ValidatorUtil(getApplicationContext());

        idTaxi = getIntent().getStringExtra("idTaxi");
        idUsuario=getIntent().getStringExtra("idUsuario");
        if(idTaxi==null||idUsuario==null)
        {
            System.out.println("QQQ taxi:"+idTaxi+" usuario:"+idUsuario);
        }

        System.out.println("el id de taxi es " + idTaxi);
        mapaH=(ImageView)findViewById(R.id.ImaHibrido);
        mapaN=(ImageView)findViewById(R.id.ImaNormal);
        cancelar=(ImageView)findViewById(R.id.CancelImageView);
        terminar=(ImageView)findViewById(R.id.TerminarImageView);
        Imagentelefono=(ImageView)findViewById(R.id.ImaTel);


        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra2 = getValuePreference(getApplicationContext());



        //Aqui se construye el showCaseView
        ////////////////////////////////////Fin////////////////////////////////////////////////////////////
        ////////////////////////Inicio_______///////////////////////////////////////////
        //aqui si no es la primera vez que se abre la activity se oculta el showCaseView
        if(!muestra2){

            saveValuePreference(getApplicationContext(), false);
            contador=4;


        }
        /////////////////////////Fin_______/////////////////////////////////////////////

        mapaN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!auxMapNormal)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    auxMapNormal=true;
                }else
                    {
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        auxMapNormal=false;
                    }

            }
        });

        mapaH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ///////////////////////////////////para calculo del tiempo estimado
                /////////////////////////////////////////////////////////////////////////////////
                if (validatorUtil.isOnline())
                {
                    ConectarServicio();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline()) {
                    AlertCancelarServicio();
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse en este momento",Toast.LENGTH_LONG).show();
            }
        });

        terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline()) {
                    AlertTerminarServicio();
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse en este momento " ,Toast.LENGTH_LONG).show();
            }
        });
        Imagentelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    HacerLlamada();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setTrafficEnabled(true);

        // Add a marker in Sydney and move the camera



            try {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("taxis").child(idTaxi);
                    listener1 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mMap.clear();


                            try {
                                escuchador1 = dataSnapshot.child("latitud").getValue().toString();
                                escuchador2 = dataSnapshot.child("longitud").getValue().toString();
                                telefono=dataSnapshot.child("telefono").getValue().toString();
                                String nombre=dataSnapshot.child("name").getValue().toString();
                                    latA = Double.parseDouble(escuchador1);
                                    lonA = Double.parseDouble(escuchador2);
                                System.out.println(escuchador1 + "      aquiiiiiiii    " + escuchador2);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latA, lonA), 20));

                                mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                        .title(nombre)
                                        .position(new LatLng(latA, lonA)));
                            } catch (NumberFormatException ex) {
                              //  Toast.makeText(getApplicationContext(), "Latitud y longitud no son números", Toast.LENGTH_SHORT).show();
                            }catch (NullPointerException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                try {
                    mDatabase.addValueEventListener(listener1);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        CargarPeticionesU();
        aux=true;
        viajeCancelado = false;
        viajeTerminado = false;
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            mDatabase.removeEventListener(listener1);
            mDatabase.removeEventListener(listener2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(alert != null)
        {
            alert.dismiss ();
        }
        aux=false;
    }
    @Override
    public void onBackPressed() {
        AlertCancelarServicio();
        if(alert != null)
        {
            alert.dismiss ();
        }
    }
    private void AlertCancelarServicio() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar Servicio");
        builder.setMessage("¿Está seguro de cancelar el servicio de taxi?")
                .setCancelable(false)
                .setPositiveButton("Si estoy seguro", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        TerminarViaje();

                        try {
                            mDatabase.removeEventListener(listener1);
                            mDatabase.removeEventListener(listener2);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                        viajeTerminado = false;
                        viajeCancelado = true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        viajeTerminado = false;
                        viajeCancelado = false;
                    }
                });
        alert = builder.create();
        alert.show();
    }
    private void AlertTerminarServicio() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terminar Servicio");
        builder.setMessage("¿Está seguro de terminar el servicio de taxi?")
                .setCancelable(false)
                .setPositiveButton("Si estoy seguro", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        TerminarViaje();

                        try {
                            mDatabase.removeEventListener(listener1);
                            mDatabase.removeEventListener(listener2);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }


                        auxT=true;
                        viajeTerminado = true;
                        viajeCancelado = false;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        viajeTerminado = false;
                        viajeCancelado = false;
                    }
                });
        alert = builder.create();
        alert.show();
    }
    private  void TerminarViaje()
    {
            try {
                    DatabaseReference mDatabaseV= FirebaseDatabase.getInstance().getReference().child("taxis");
                    DatabaseReference mDatabaseV2=mDatabaseV.child(idTaxi);
                    mDatabaseV2.child("ViajeA").setValue("0#vacio");
                    DatabaseReference mDatabaseV3= FirebaseDatabase.getInstance().getReference().child("users");
                    DatabaseReference mDatabaseV4=mDatabaseV3.child(idUsuario);
                    mDatabaseV4.child("estado").setValue("0");
                    mDatabaseV4.child("ViajeA").setValue("0#vacio");
            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    private void CargarPeticionesU() {

            try {
                    mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(idUsuario);
                    listener2 =new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            try {
                                escuchadorUsu=dataSnapshot.child("estado").getValue().toString();
                                escuchadorUsuP=dataSnapshot.child("ViajeA").getValue().toString();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }


                            try {
                                latUser=Double.parseDouble(dataSnapshot.child("latitud").getValue().toString());
                                lonUser=Double.parseDouble(dataSnapshot.child("longuitud").getValue().toString());
                            } catch (NullPointerException e) { e.printStackTrace();  }
                            catch (NumberFormatException e) { e.printStackTrace();  }


                            nomUsuario=dataSnapshot.child("name").getValue().toString();
                            if(escuchadorUsuP.equals("0#vacio") && aux && !auxT)
                            {
                                if(viajeCancelado) {
                                    AlertServicioCancelado("¡Has finalizado el servicio!");

                                    try {
                                        mDatabase.removeEventListener(listener2);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }


                                }
                                else if(((!viajeCancelado)&&(!viajeTerminado)))
                                {
                                    AlertServicioCancelado("La unidad solicitada ha finalizado el servicio");

                                    try {
                                        mDatabase.removeEventListener(listener2);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            else if (escuchadorUsuP.equals("0#vacio"))
                            {
                                if (viajeTerminado)
                                {
                                    AlertServicioTerminado();

                                    try {
                                        mDatabase.removeEventListener(listener2);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                try {
                    mDatabase.addValueEventListener(listener2);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

    }

    @Override
    protected void onResume() {
        super.onResume();
        viajeCancelado = false;
        viajeTerminado = false;
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void AlertServicioCancelado(String Mensaje) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Servicio finalizado");
        builder.setMessage(Mensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        try {
                            mDatabase.removeEventListener(listener1);
                            mDatabase.removeEventListener(listener2);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        if (validatorUtil.isOnline())
                        {
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                        }

                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void AlertServicioTerminado() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Servicio Terminado");
        builder.setMessage("El viaje ha finalizado con éxito")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        try {
                            mDatabase.removeEventListener(listener1);
                            mDatabase.removeEventListener(listener2);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(),CalificarTaxi.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("idTaxi",idTaxi);
                        intent.putExtra("idUsuario",idUsuario);
                        intent.putExtra("nomUsuario",nomUsuario);
                        if (validatorUtil.isOnline()) {
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.exit(0);
                        }

                    }
                });
        alert = builder.create();
        alert.show();
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ValidatorUtil validatorUtil = new ValidatorUtil(context);
            if (!validatorUtil.isOnline())
            {
                Toast.makeText(context,"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            mDatabase.removeEventListener(listener1);
            mDatabase.removeEventListener(listener2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }



    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("MapsTaxi", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("MapsTaxi", true);
    }

    ///////////////////////////////////////Para el showCaseView por primera vez___Fin///////////////////////////////////////////
    public void HacerLlamada()
    {
        try {
            System.out.println("aquiiii "+telefono);


                if (!telefono.equalsIgnoreCase("no")&&telefono.matches("[0-9]*")) {
                    Intent i =new Intent(Intent.ACTION_CALL);
                    System.out.println("QQQ "+telefono);
                    telefono=telefono.trim();
                    System.out.println("QQQ2 "+telefono);
                    if(!telefono.isEmpty()&&telefono!=null||telefono.equalsIgnoreCase("no"))
                    {
                        i.setData(Uri.parse("tel:"+telefono));
                    }
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermission();
                    }else
                    {
                        startActivity(i);
                    }
                }else if(telefono.equalsIgnoreCase("no")){Toast.makeText(getApplicationContext(),"El conductor no a proporcionado su teléfono", Toast.LENGTH_SHORT).show();}
                else if(!telefono.matches("[0-9]*")){Toast.makeText(getApplicationContext(),"El usuario no a proporcionado su teléfono correctamente", Toast.LENGTH_SHORT).show();}

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }
public void requestPermission()
{
    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
}

    //////////////////////////////////Para el calculo del tiempo
    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=38.404593,-0.529534&sensor=false
            cadena = cadena + params[0];
            cadena = cadena + "&destinations=";
            cadena = cadena + params[1];
            cadena = cadena + "&language=es&key=AIzaSyDldoJ330Eq-cBIZGD9lMs9_FzeucK5Ctc";
            System.out.println(cadena);

            String devuelve = "";

            URL url = null; // Url de donde queremos obtener información
            try {
                url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK){


                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                    // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                    // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                    // StringBuilder.

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    JSONArray resultJSON = respuestaJSON.getJSONArray("rows");   // rows es el nombre del campo en el JSON
                    JSONObject object_rows=resultJSON.getJSONObject(0);

                    JSONArray array_elements=object_rows.getJSONArray("elements");
                    JSONObject  object_elements=array_elements.getJSONObject(0);

                    JSONObject object_duration=object_elements.getJSONObject("duration");
                    JSONObject object_distance=object_elements.getJSONObject("distance");

                    double distanciaAux=Double.parseDouble(object_distance.getString("value"))/1000;


                    String TiempoAux2=object_duration.getString("text");
                    System.out.println("####################################\nDistancia: "+distanciaAux+" Tiempo:"+TiempoAux2);
                    //Vamos obteniendo todos los campos que nos interesen.
                    //En este caso obtenemos la primera dirección de los resultados.
                    String direccion="SIN DATOS PARA ESA LONGITUD Y LATITUD";

                    direccion="Tiempo "+object_duration.getString("text")+" para llegar\n\nDistancia "+object_distance.getString("text");
                    devuelve =direccion;   // variable de salida que mandaré al onPostExecute para que actualice la UI

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return devuelve;
        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            StringTokenizer st1 = new StringTokenizer(aVoid, "#");
            System.out.println("Aqui???????????????????????????????\n"+aVoid);
            Toast.makeText(getApplicationContext(),aVoid, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            // resultado.setText("");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void GpsActualizacion()
    {
        checkLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PETICION_PERMISO_LOCALIZACION);
        }
        else
        {
           // Toast.makeText(getApplicationContext(),"Permisos concedidos", Toast.LENGTH_SHORT).show();
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null)
        {
            locationManager.requestLocationUpdates(provider, 1000*3* 1, 10, locationListenerBest);
           // Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void ConectarServicio()
    {
        String Origen=null,Destino=null;
        String latitud2=String.valueOf(latA);
        String longitud2=String.valueOf(lonA);

        Origen=latitudeBest+","+longitudeBest;
        Destino=latA+","+lonA;

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\nOrige="+Origen+" Destino="+Destino);
        hiloconexion = new ObtenerWebService();
        if(latitudeBest!=0&&longitudeBest!=0)
        {
            if(latitud2!=null&&longitud2!=null&&!latitud2.equalsIgnoreCase("desconocida")&&!longitud2.equalsIgnoreCase("desconocida"))
            {
                hiloconexion.execute(Origen,Destino);   // Parámetros que recibe doInBackground
            }


        }else if(latitudeBest==0&&longitudeBest==0)
        {
            Toast.makeText(getApplicationContext(),"Esperando ubicacion intentelo en unos segundos mas tarde", Toast.LENGTH_SHORT).show();
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            latitudeBest = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // longitudeValueBest.setText(longitudeBest + "");
                    // latitudeValueBest.setText(latitudeBest + "");
                   // Toast.makeText(getApplicationContext(), "longuitid:"+longitudeBest+"\n"+"latitud:"+latitudeBest, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };
//////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PETICION_PERMISO_LOCALIZACION)
        {
            if(grantResults[0]!=PackageManager.PERMISSION_DENIED)
            {
                GpsActualizacion();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"permisos denegados", Toast.LENGTH_SHORT).show();
                GpsActualizacion();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    private static boolean esNumero(String cadena){
        try {
            Integer.parseInt(cadena);
            if (cadena.matches("[0-9]*")){}
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }
}
