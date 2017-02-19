package com.example.zzzclcik.pruebafirebase;

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

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.AuthResult;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


    protected void cargarValues() {


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
        });

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


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(20.1453953, -98.67203239999998), 36));

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        //while (value1 == 0 && value2 == 0) {        }
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(value1, value2)));
        System.out.println(value1 + "    aqui   " + value2);

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(value1 + 0.00001, value2 - 0.00001)));
        System.out.println(value1 + 0.00001 + "    aqui   " + value2 + 0.00001);


        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(value1 + 0.00002, value2 - 0.00002)));
        System.out.println(value1 + 0.00002 + "    aqui   " + value2 + 0.00002);

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(value1 + 0.00003, value2 - 0.00003)));
        System.out.println(value1 + 0.00003 + "    aqui   " + value2 + 0.00003);


/*              mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(20.1453953, -98.66203239999998)));
*/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxidos))
                        .anchor(0.0f, 0.1f)
                        .position(latLng));

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "Has pulsado una marca", Toast.LENGTH_LONG).show();
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
