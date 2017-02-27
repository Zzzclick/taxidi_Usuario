package com.example.zzzclcik.pruebafirebase;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private double value1, value2;
    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mensajeRef1 = ref1.child("ubicacion1");
    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mensajeRef2 = ref2.child("ubicacion2");
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    ArrayList<String>latArray=new ArrayList<>();
    ArrayList<String>lonArray=new ArrayList<>();
    ArrayList<String>nameArray=new ArrayList<>();
    ArrayList<String>fotoArray=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase= FirebaseDatabase.getInstance().getReference();

        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_maps);

        cargarValues();
        /* Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        */


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Toast.makeText(MapsActivity.this, "Ya estas logueado " + firebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

                    //mAuth.signOut();
                }
            }
        };

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in,R.anim.left_out);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        latArray.clear();
        lonArray.clear();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    protected void cargarValues() {
/*

        mensajeRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value1 = dataSnapshot.getValue(double.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error value 1", Toast.LENGTH_LONG).show();
            }
        });


        mensajeRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value2 = dataSnapshot.getValue(double.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error value 2", Toast.LENGTH_LONG).show();
            }
        });*/

        if (value1 == 0 && value2 == 0) {
            try {
                Thread.sleep(1500);
                Toast.makeText(getApplicationContext(), "Corriendo hilo", Toast.LENGTH_SHORT).show();

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

         latitud2 = getIntent().getStringExtra("latitud");
        longitud2 = getIntent().getStringExtra("Longitud");
        System.out.println("11111111"+latitud2+"222222"+longitud2);
        if (latitud2!=null || longitud2!=null)
        {
        final double latA, lonA;
        latA = Double.parseDouble(latitud2);
        lonA = Double.parseDouble(longitud2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latA, lonA), 18));
        }else{                Toast.makeText(getApplicationContext(), "No entro", Toast.LENGTH_SHORT).show();
        }

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        //while (value1 == 0 && value2 == 0) {        }



/*              mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(20.1453953, -98.66203239999998)));
                */
        //////////////////////////////////////7

        mDatabase.child("taxis").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Iterator<DataSnapshot>items=dataSnapshot.getChildren().iterator();
                Toast.makeText(getApplicationContext(), "Empezando todos", Toast.LENGTH_SHORT).show();

                //entris.clear();
                latArray.clear();
                lonArray.clear();
                nameArray.clear();
                while (items.hasNext())
                {
                    DataSnapshot item=items.next();
                    String lat,lon,nombreIten,foto;
                    lat=item.child("latitud").getValue().toString();
                    lon=item.child("longitud").getValue().toString();
                    nombreIten=item.child("name").getValue().toString();
                    foto=item.child("image").getValue().toString();
                    latArray.add(lat);
                    lonArray.add(lon);
                    nameArray.add(nombreIten);
                    fotoArray.add(foto);

                    for (int x=0;x<latArray.size();x++)
                    {
                        System.out.println("Latitud:"+latArray.get(x)+" Longuitud:"+lonArray.get(x));
                        System.out.println("NOMBRE:"+nameArray.get(x));
                        System.out.println("Imagen:"+fotoArray.get(x));
                        String latAux,lonAux="0";
                        double aux1,aux2;
                        String aux3,aux4;
                        String token = FirebaseInstanceId.getInstance().getToken();
                        if(!latArray.get(x).isEmpty()||!latArray.get(x).equals("")||latArray.get(x)!=null||!lonArray.get(x).isEmpty()||!lonArray.get(x).equals("")||lonArray.get(x)!=null)
                        {
                        aux1=Double.parseDouble(latArray.get(x));
                        aux2=Double.parseDouble(lonArray.get(x));
                        aux3=nameArray.get(x);
                        aux4=fotoArray.get(x);
                        if(aux3!=null||aux3!=null)
                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                .title(aux3)
                                .snippet(aux4+"|")
                                .position(new LatLng(aux1,aux2)));
                        }else {Toast.makeText(getApplicationContext(), "Cnversion de dobles", Toast.LENGTH_LONG).show();}
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });//


        ///////////////////////////////////////7



        System.out.println(latArray.size()+"     Tamaño del arreglo de latitud");
        for (int x=0;x<latArray.size();x++)
        {
            System.out.println("Si esta imprimiendo Latitud:"+latArray.get(x)+" Longuitud:"+lonArray.get(x));
        }

        //////Para crear marcador con un click largo
        /*mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                        .anchor(0.0f, 0.1f)
                        .position(latLng));

            }
        });*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "Has pulsado una marca", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MapsActivity.this, DetalleTaxis.class );
                i.putExtra("nombre",marker.getTitle());
                i.putExtra("posicion",marker.getPosition());
                i.putExtra("foto",marker.getSnippet());

                startActivity(i);
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
}
