package com.example.zzzclcik.pruebafirebase;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private double value1, value2;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    public String idUsusario;
    public  boolean siEstaEnActivity;
    public ImageView terreno,hibrido,mapa;
    double lat1,lon1;
    int radioBusqueda = 100;
    View viewLayout;
    ValueEventListener listener;
    ValidatorUtil validatorUtil = null;

    private ShowcaseView showcaseView;
    private int contador=0;
    private Target t1,t2,t3,t4,t5;


    ArrayList<String>idArray=new ArrayList<>();
    ArrayList<Double>latArray=new ArrayList<>();
    ArrayList<Double>lonArray=new ArrayList<>();
    ArrayList<String>nameArray=new ArrayList<>();
    ArrayList<String>fotoArray=new ArrayList<>();
    ArrayList<String>placasArray=new ArrayList<>();
    ArrayList<String>estadosArray=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_maps);


        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast_sininternet,(ViewGroup)findViewById(R.id.custom_layout2));

        validatorUtil = new ValidatorUtil(getApplicationContext());
        try {
            idUsusario=getIntent().getStringExtra("idUsuario");
            mDatabase= FirebaseDatabase.getInstance().getReference();
        } catch (Exception e) {
            e.printStackTrace();
        }
    terreno=(ImageView)findViewById(R.id.ImaTereno);
    hibrido=(ImageView)findViewById(R.id.ImaHibrido);
    mapa=(ImageView)findViewById(R.id.ImaNormal);

        cargarValues();



        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra2 = getValuePreference(getApplicationContext());


        try {
            t1= new ViewTarget(R.id.ImaNormal, this);
            t2= new ViewTarget(R.id.ImaHibrido, this);
            t3= new ViewTarget(R.id.ImaTereno, this);
            ////////////////////////////////////Inicio////////////////////////////////////////////////////////
            showcaseView=new ShowcaseView.Builder(this)
                    .setTarget(Target.NONE)
                    .setOnClickListener(this)
                    .setContentTitle("Bienvenido")
                    .setContentText("Vamos a comenzar")
                    .setStyle(R.style.Transparencia)
                    .build();
            showcaseView.setButtonText("Siguiente");
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        //Aqui se construye el showCaseView
        ////////////////////////////////////Fin////////////////////////////////////////////////////////////
        ////////////////////////Inicio_______///////////////////////////////////////////
        //aqui si no es la primera vez que se abre la activity se oculta el showCaseView
        if(!muestra2){

            saveValuePreference(getApplicationContext(), false);
            contador=3;
            try {
                showcaseView.hide();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        }
        /////////////////////////Fin_______/////////////////////////////////////////////




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        //Toast.makeText(MapsActivity.this, "Ya estas logueado " + firebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                        //mAuth.signOut();
                    }
            }
        };
       mapa.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

                   mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                   Toast.makeText(getApplicationContext(),"Modo normal", Toast.LENGTH_SHORT).show();
           }
       });
        hibrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    Toast.makeText(getApplicationContext(),"Modo hibrido", Toast.LENGTH_SHORT).show();
            }
        });
        terreno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    Toast.makeText(getApplicationContext(),"Modo terreno", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

            try {
                mDatabase.removeEventListener(listener);
            } catch (NullPointerException e) {
                e.printStackTrace();

        }
        Intent i = new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            mDatabase.removeEventListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        latArray.clear();
        lonArray.clear();
        siEstaEnActivity=false;
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        try {
            mDatabase.removeEventListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        siEstaEnActivity=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        siEstaEnActivity=true;
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        siEstaEnActivity=true;
    }

    @Override
    protected void onStart()
    {siEstaEnActivity=true;
        super.onStart();

    }

    protected void cargarValues()
    {
        if (value1 == 0 && value2 == 0) {
            try {
                Thread.sleep(0000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        String latitud2 = "null1";
        String longitud2 = "null1";

        try {
            latitud2 = getIntent().getStringExtra("latitud");
            longitud2 = getIntent().getStringExtra("longitud");
            System.out.println("11111111"+latitud2+"222222"+longitud2);
            if (latitud2!=null || longitud2!=null||!latitud2.equals("desconocida")||!longitud2.equals("desconocida"))
            {
                final double latA, lonA;
                try
                {
                    lat1 = latA = Double.parseDouble(latitud2);
                    lon1 = lonA = Double.parseDouble(longitud2);
                    System.out.println("Latitud Usuario: " + lat1 + " Longitud Usuario: " + lon1);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latA, lonA), 18));
                }catch (NumberFormatException ex)
                {
                    Toast.makeText(getApplicationContext(), "Latitud y longitud no son números", Toast.LENGTH_SHORT).show();
                }
            }else{ /*Toast.makeText(getApplicationContext(), "No entro", Toast.LENGTH_SHORT).show();*/ }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (validatorUtil.isOnline()) {
            try {
                    listener = mDatabase.child("taxis").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            mMap.clear();
                            Iterator<DataSnapshot>items=dataSnapshot.getChildren().iterator();

                            idArray.clear();
                            latArray.clear();
                            lonArray.clear();
                            nameArray.clear();
                            fotoArray.clear();
                            placasArray.clear();
                            estadosArray.clear();

                            while (items.hasNext())
                            {
                                try {
                                    DataSnapshot item=items.next();
                                    String id,nombreIten,foto,placa,estado;
                                    double lat,lon;
                                    try
                                    {
                                        id=item.child("id").getValue().toString();
                                        nombreIten=item.child("name").getValue().toString();
                                        foto=item.child("image").getValue().toString();
                                        placa=item.child("placas").getValue().toString();
                                        estado=item.child("estado").getValue().toString();
                                        lat = Double.parseDouble(item.child("latitud").getValue().toString());
                                        lon = Double.parseDouble(item.child("longitud").getValue().toString());
                                        if ((latArray.size() <= 6) || (lonArray.size() <= 6) || (idArray.size() <= 6) || (nameArray.size() <= 6) ||
                                                (fotoArray.size() <= 6) || (placasArray.size() <= 6) || (estadosArray.size() <= 6)) {
                                            latArray.add(lat);
                                            lonArray.add(lon);
                                            idArray.add(id);
                                            nameArray.add(nombreIten);
                                            fotoArray.add(foto);
                                            placasArray.add(placa);
                                            estadosArray.add(estado);
                                        }
                                    }catch (NumberFormatException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    if(!latArray.isEmpty()) {
                                        for (int x = 0; x < latArray.size(); x++) {
                                            if (estadosArray.get(x).equals("0")) {
                                                mMap.addMarker(new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                                                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                                        .title(nameArray.get(x))
                                                        .snippet(fotoArray.get(x) + "|" + placasArray.get(x) + "|" + idArray.get(x))
                                                        .position(new LatLng(latArray.get(x), lonArray.get(x))));
                                            }
                                        }
                                    }
                                    else {
                                        radioBusqueda = radioBusqueda + 50;
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Error en la base de datos " + databaseError.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        else
            {
                Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_LONG);
                customToast.setGravity(Gravity.BOTTOM,0,0);
                customToast.setView(viewLayout);
                customToast.show();
            }


        System.out.println(latArray.size()+"     Tamaño del arreglo de latitud");
        for (int x=0;x<latArray.size();x++)
        {
            System.out.println("Si esta imprimiendo Latitud:"+latArray.get(x)+" Longuitud:"+lonArray.get(x));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (validatorUtil.isOnline()) {
                    Intent i = new Intent(MapsActivity.this, DetalleTaxis.class );
                    i.putExtra("nombre",marker.getTitle());
                    i.putExtra("foto",marker.getSnippet());
                    i.putExtra("latitud",Double.toString(lat1));
                    i.putExtra("longitud",Double.toString(lon1));
                    i.putExtra("idUsuario",idUsusario);
                    finish();
                    startActivity(i);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
                else
                    {
                        Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_LONG);
                        customToast.setGravity(Gravity.BOTTOM,0,0);
                        customToast.setView(viewLayout);
                        customToast.show();
                    }

                return false;
            }
        });


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
        System.out.println("!!!!!!!!!!prueba "+mMap.isMyLocationEnabled());
    }

    public double distanciaCoordenadas(double lat1, double lng1, double lat2, double lng2) {
        double radioTierra = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distanciaKM = radioTierra * va2;

        double distanciaM = distanciaKM * 1000;

        return distanciaM;
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ValidatorUtil validatorUtil = new ValidatorUtil(context);
            if (!validatorUtil.isOnline())
            {
                Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_LONG);
                customToast.setGravity(Gravity.BOTTOM,0,0);
                customToast.setView(viewLayout);
                customToast.show();
            }
        }
    };

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
        try {
            mDatabase.removeEventListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("MapsActivity", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("MapsActivity", true);
    }

    @Override
    public void onClick(View v) {
        switch (contador) {
            case 0:
                try {
                    showcaseView.setShowcase(t1, true);
                    showcaseView.setContentTitle("Modo normal");
                    showcaseView.setContentText("Aquí podrás cambiar a modo normal el mapa ");
                    break;
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            case 1:

                try {
                    showcaseView.setShowcase(t2, true);
                    showcaseView.setContentTitle("Modo híbrido");
                    showcaseView.setContentText("Aquí podrás cambiar a modo híbrido el mapa para ver el mapa satelital ");
                    break;
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            case 2:
                try {
                    showcaseView.setShowcase(t3, true);
                    showcaseView.setContentTitle("Modo terreno");
                    showcaseView.setContentText("Aquí podrás cambiar a modo terreno el mapa viendo la elevación del terreno ");
                    showcaseView.setButtonText("Finalizar");
                    break;
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

            case 3:
                try {
                    showcaseView.hide();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                boolean muestra2 = getValuePreference(getApplicationContext());
                if(muestra2)
                {
                    saveValuePreference(getApplicationContext(), false);
                    //  Toast.makeText(getApplicationContext(),"Primera vez:"+muestra, Toast.LENGTH_LONG).show();
                }
                break;
            default:

                break;
        }

        contador++;
    }

    /////////////////Volver a mosrtrar el ShowCaseView_______Inicio//////////////////////////////////////////////
    public  void Ayuda()
    {
        contador=0;
        showcaseView.show();
        showcaseView.setTarget(Target.NONE);
        showcaseView.setContentTitle("Bienvenido");
        showcaseView  .setContentText("Vamos a comenzar");
        showcaseView.setButtonText("Siguiente");
    }
    /////////////////Volver a mosrtrar el ShowCaseView_______Final//////////////////////////////////////////////

    ///////////////////////////////////////Para el showCaseView por primera vez___Fin///////////////////////////////////////////

}
