package com.example.zzzclcik.pruebafirebase;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.StringTokenizer;

import static com.google.ads.AdRequest.LOGTAG;

public class Destino extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener,View.OnClickListener{
    TextView placeNameText;
    TextView placeAddressText;
    TextView CostoTxt,TiempoTxt;
    WebView attributionText;
    TextView resultado;
    Button getPlaceButton, btnAceptar;
    AlertDialog alert = null;

    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int PLACE_PICKER_REQUEST = 1;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleApiClient apiClient;
    LocationManager locationManager;
    String latitudOrigen,longitudOrigen;
    String latitud,longitud;

    double distancia;
    String tiempoAux;
    ObtenerWebService hiloconexion;
    double latitudDestino,longitudDestino;
    public String idUsusario,union = null ,union2 = null;
    String idTaxi;
    String peticion1,peticion2,peticion3,peticion4,peticion5;
    String nomU,latU,lonU;
    public ProgressDialog pd = null;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference peticionesRef;
    private ValueEventListener listener1;
    ValidatorUtil validatorUtil = null;
    private ShowcaseView showcaseView;
    private int contador=0;
    private Target t1,t2,t3,t4,t5;
//    private  final static LatLngBounds bounds=new LatLngBounds(new LatLng(20.137797, -98.375485),new LatLng (20.131044, -98.388337));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destino);
        requestPermission();
        getSupportActionBar().setTitle("Seleccionar destino");
        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        placeNameText = (TextView) findViewById(R.id.tvPlaceName);
        placeAddressText = (TextView) findViewById(R.id.tvPlaceAddress);
        attributionText = (WebView) findViewById(R.id.wvAttribution);
        getPlaceButton = (Button) findViewById(R.id.btGetPlace);
        btnAceptar = (Button) findViewById(R.id.btnAceptar);

        resultado = (TextView)findViewById(R.id.resultado);
        TiempoTxt=(TextView) findViewById(R.id.TiempoTextView);
        CostoTxt=(TextView) findViewById(R.id.CostoTextView);

        placeNameText.setTypeface(fuente);
        placeAddressText.setTypeface(fuente);
        getPlaceButton.setTypeface(fuente);
        btnAceptar.setTypeface(fuente);
        resultado.setTypeface(fuente);
        TiempoTxt.setTypeface(fuente);
        CostoTxt.setTypeface(fuente);


        t1= new ViewTarget(R.id.btGetPlace, this);
        t2= new ViewTarget(R.id.tvPlaceName, this);
        t3= new ViewTarget(R.id.TiempoTextView, this);
        t4= new ViewTarget(R.id.CostoTextView, this);
        t5= new ViewTarget(R.id.btnAceptar, this);
        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra2 = getValuePreference(getApplicationContext());




        ////////////////////////////////////Inicio////////////////////////////////////////////////////////
        showcaseView=new ShowcaseView.Builder(this)
                .setTarget(Target.NONE)
                .setOnClickListener(this)
                .setContentTitle("Bienvenido")
                .setContentText("Vamos a comenzar")
                .setStyle(R.style.Transparencia)
                .build();
        showcaseView.setButtonText("Siguiente");
        //Aqui se construye el showCaseView
        ////////////////////////////////////Fin////////////////////////////////////////////////////////////
        ////////////////////////Inicio_______///////////////////////////////////////////
        //aqui si no es la primera vez que se abre la activity se oculta el showCaseView
        if(!muestra2){

            saveValuePreference(getApplicationContext(), false);
            contador=5;
            showcaseView.hide();

        }
        /////////////////////////Fin_______/////////////////////////////////////////////
        try {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("taxis");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            idTaxi = getIntent().getStringExtra("idTaxi");
            idUsusario = getIntent().getStringExtra("idUsuario");
            latitud = getIntent().getStringExtra("nombre");
            longitud = getIntent().getStringExtra("foto");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        getPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validatorUtil.isOnline()) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                 //   builder.setLatLngBounds(bounds);
                    try {
                        Intent intent = builder.build(Destino.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline()) {
                    if (!placeAddressText.getText().toString().equals("Dirección")) {
                        String direccion = placeAddressText.getText().toString();
                        if(direccion.contains("Unnamed Road"))
                        {
                            Toast.makeText(getApplicationContext(),"La dirección seleccionada no es válida, favor de revisarla\n ",Toast.LENGTH_LONG).show();
                        }
                        else {
                            pd = ProgressDialog.show(Destino.this, "Eviando petición", "Espere unos segundos");
                            EsperaSolicitud();
                            btnAceptar.setEnabled(false);
                        }
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Seleccione un destino",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        });

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /****Mejora****/
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnAceptar.setEnabled(true);
        if (validatorUtil.isOnline()) {
            try {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("taxis");
                    peticionesRef = databaseReference.child(idTaxi);
                    listener1 = peticionesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            peticion1 = dataSnapshot.child("peticion1").getValue().toString();
                            peticion2 = dataSnapshot.child("peticion2").getValue().toString();
                            peticion3 = dataSnapshot.child("peticion3").getValue().toString();
                            peticion4 = dataSnapshot.child("peticion4").getValue().toString();
                            peticion5 = dataSnapshot.child("peticion5").getValue().toString();

                            if(union2 != null)
                            {
                                String cadenaUsuario = union2;
                                if (cadenaUsuario.equals(dataSnapshot.child("peticion1").getValue().toString())
                                        || cadenaUsuario.equals(dataSnapshot.child("peticion2").getValue().toString())
                                        || cadenaUsuario.equals(dataSnapshot.child("peticion3").getValue().toString())
                                        || cadenaUsuario.equals(dataSnapshot.child("peticion4").getValue().toString())
                                        || cadenaUsuario.equals(dataSnapshot.child("peticion5").getValue().toString()))
                                {
                                    if(pd.isShowing())
                                        pd.dismiss();

                                    Toast.makeText(getApplicationContext(), "Petición exitosa", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),EsperaServicio.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.putExtra("idTaxi",idTaxi);
                                    intent.putExtra("union",union2);
                                    union = null;
                                    union2 = null;
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    AlertFalloPeticion();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Error Base", Toast.LENGTH_SHORT).show();
                        }
                    });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            ObtenerDatosUsuario();
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
    }

    private void AlertFalloPeticion() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error al enviar petición");
        builder.setMessage("¿Deseas volver a intentar?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        EsperaSolicitud();
                        btnAceptar.setEnabled(false);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        union = null;
                        union2 = null;
                        dialog.cancel();
                        if (listener1 != null) {
                            mDatabase.removeEventListener(listener1);
                        }
                        Intent i = new Intent(getApplicationContext(),MapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }
    private void AlertNoGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(Destino.this, data);


                latitudDestino=place.getLatLng().latitude;
                longitudDestino=place.getLatLng().longitude;
                System.out.println(latitudDestino+"\n"+(longitudDestino*-1));
                if(latitudDestino>=20.044997&&latitudDestino<=20.141020)
                {
                    System.out.println("\n\n\n\nEntro al primer IF");
                    if((longitudDestino*-1)>=(98.681905)&&(longitudDestino*-1)<=(98.812860))
                    {
                        System.out.println("\n\n\n\nEntro al segundo IF");
                        placeNameText.setText(place.getName());
                        placeAddressText.setText(place.getAddress());
                        ConectarServicio();
                        System.out.println("!!!!!!!!!! "+distancia);
                        CostoTxt.setText(String.valueOf(calcularTarifa(distancia)));
                        TiempoTxt.setText(tiempoAux);

                        if (place.getAttributions() == null) {
                            attributionText.loadData("no attribution", "text/html; charset=utf-8", "UFT-8");
                        } else {
                            attributionText.loadData(place.getAttributions().toString(), "text/html; charset=utf-8", "UFT-8");
                        }
                    }else
                        {
                            Toast.makeText(getApplicationContext(),"Ubicacion seleciona fuera de rango\nPor favor selecione un ubicacion dentro de pachuca", Toast.LENGTH_SHORT).show();
                        }
                }else
                    {
                        Toast.makeText(getApplicationContext(),"Ubicacion seleciona fuera de rango\nPor favor selecione un ubicacion dentro de pachuca", Toast.LENGTH_SHORT).show();
                    }

            }
        }
    }
    public void ConectarServicio()
    {
        String Origen,Destino;
        Origen=latitudOrigen+","+longitudOrigen;
        Destino=latitudDestino+","+longitudDestino;
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\nOrige="+Origen+" Destino="+Destino);
        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(Origen,Destino);   // Parámetros que recibe doInBackground
    }
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }
    private void updateUI(Location loc) {
        if (loc != null) {

            latitudOrigen=String.valueOf(loc.getLatitude());
            longitudOrigen=String.valueOf(loc.getLongitude());
            System.out.println("Latitud "+latitudOrigen+"  Longitud "+longitudOrigen);

        } else {
            latitudOrigen="Latitud: (desconocida)";
            longitudOrigen="Longitud: (desconocida)";
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Toast.makeText(Destino.this, "Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Toast.makeText(Destino.this, "Error grave al conectar con Google Play Services", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
                DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference currentUserBD2=mDatabase2.child(idUsusario);
                currentUserBD2.child("latitud").setValue(String.valueOf(location.getLatitude()));
                currentUserBD2.child("longitud").setValue(String.valueOf(location.getLongitude()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


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

                    distancia=Double.parseDouble(object_distance.getString("value"))/1000;

                    tiempoAux=object_duration.getString("text");
                    System.out.println("####################################\nDistancia: "+distancia+" Tiempo:"+tiempoAux);
                    //Vamos obteniendo todos los campos que nos interesen.
                    //En este caso obtenemos la primera dirección de los resultados.
                    String direccion="SIN DATOS PARA ESA LONGITUD Y LATITUD";

                    direccion="Tiempo "+object_duration.getString("text")+"\nDistancia "+object_distance.getString("text")+"#"+distancia+"#"+tiempoAux;
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
            resultado.setText(st1.nextToken());
            Double dis=calcularTarifa(Double.parseDouble(st1.nextToken()));  //
            CostoTxt.setText(dis.toString());
            TiempoTxt.setText(st1.nextToken());
        }

        @Override
        protected void onPreExecute() {
            resultado.setText("");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    public double calcularTarifa(double distancia) {
        double costo = 0, tarifaMinima = 30.0, kmExtra = 2.50, distaciaMinima = 4.0, diferenciaDistancia = 0, auxiliarD = 0;
        int auxiliarI = 0;

        if (distancia <= distaciaMinima) {
            costo = tarifaMinima;
        }
        else {
            diferenciaDistancia = distancia - distaciaMinima;
            auxiliarI = (int) diferenciaDistancia;
            auxiliarD = diferenciaDistancia - auxiliarI;
            if ((auxiliarD > 0)) {
                auxiliarI++;
                costo = tarifaMinima + (auxiliarI * kmExtra);
            } else {
                costo = tarifaMinima + (auxiliarI * kmExtra);
            }
        }
        return costo;
    }

    public void ObtenerDatosUsuario()
    {
        try {
                mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
                mDatabase.child(idUsusario).addListenerForSingleValueEvent(new ValueEventListener()
                {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        nomU=dataSnapshot.child("name").getValue().toString();
                        latU=dataSnapshot.child("latitud").getValue().toString();
                        lonU=dataSnapshot.child("longitud").getValue().toString();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {Toast.makeText(getApplicationContext(),"error Base", Toast.LENGTH_SHORT).show();}
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertarPeticion(String auxUnion, String numPeticion)
    {
        if (validatorUtil.isOnline()) {
            try {
                    peticionesRef = databaseReference.child(idTaxi);
                    DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("users");
                    DatabaseReference currentUserBD22=mDatabase2.child(idUsusario);

                    peticionesRef.child(numPeticion).setValue(auxUnion);
                    currentUserBD22.child("estado").setValue("1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
    }

    public boolean MandarPeticion()
    {
        boolean bandera = false;

        btnAceptar.setEnabled(false);

        if(!placeAddressText.getText().toString().trim().equals("Dirección"))
        {
            union = nomU + "¥" + latU + "¥" + lonU + "¥" + idUsusario + "¥" + ("\n"+placeAddressText.getText().toString().trim());
        }

        if (peticion1.equals("123"))
        {
            union2="1¥"+union;
            InsertarPeticion(union2, "peticion1");
            bandera = true;
        }
        else if (peticion2.equals("123"))
        {
            union2="2¥"+union;
            InsertarPeticion(union2, "peticion2");
            bandera = true;
        }
        else if (peticion3.equals("123"))
        {
            union2="3¥"+union;
            InsertarPeticion(union2, "peticion3");
            bandera = true;
        }
        else if (peticion4.equals("123"))
        {
            union2="4¥"+union;
            InsertarPeticion(union2, "peticion4");
            bandera = true;
        }
        else if (peticion5.equals("123"))
        {
            union2="5¥"+union;
            InsertarPeticion(union2, "peticion5");
            bandera = true;
        }
        else
        {
            bandera = false;
        }

        return bandera;
    }

    public int TiempoEspera()
    {
        Random random = new Random(System.currentTimeMillis());
        int op = random.nextInt(10);
        int time = 0;
        switch (op)
        {
            case 0:
                time = 500;
                break;
            case 1:
                time = 1000;
                break;
            case 2:
                time = 1500;
                break;
            case 3:
                time = 2000;
                break;
            case 4:
                time = 2500;
                break;
            case 5:
                time = 3000;
                break;
            case 6:
                time = 3500;
                break;
            case 7:
                time = 4000;
                break;
            case 8:
                time = 4500;
                break;
            case 9:
                time = 5000;
                break;
        }
        return time;
    }

    public void EsperaSolicitud()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(TiempoEspera());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!MandarPeticion())
                        {
                            Toast.makeText(getApplicationContext(), "Éste taxi tiene muchas peticiones", Toast.LENGTH_SHORT).show();
                            btnAceptar.setEnabled(true);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (listener1 != null) {
            mDatabase.removeEventListener(listener1);
        }
        Intent i = new Intent(getApplicationContext(),DetalleTaxis.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("nombre",latitud);
        i.putExtra("foto",longitud);
        i.putExtra("idUsuario",idUsusario);
        finish();
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
        if (listener1 != null) {
            mDatabase.removeEventListener(listener1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener1 != null) {
            mDatabase.removeEventListener(listener1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener1 != null) {
            mDatabase.removeEventListener(listener1);
        }
    }



    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("Destino", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("Destino", true);
    }

    @Override
    public void onClick(View v) {
        switch (contador) {
            case 0:
                showcaseView.setShowcase(t1, true);
                showcaseView.setContentTitle("Elegir destino");
                showcaseView.setContentText("Aqui podras cambiar tu estado disponibe y no disponible\nrecuerda para aparecer en el mapa ");
                break;
            case 1:
                showcaseView.setShowcase(t2, true);
                showcaseView.setContentTitle("Dirección");
                showcaseView.setContentText("Es la direcion del destino");
                break;
            case 2:
                showcaseView.setShowcase(t3, true);
                showcaseView.setContentTitle("Tiempo");
                showcaseView.setContentText("Tiempo aproximado que va durar su viaje una vez arriba del taxi");
                break;
            case 3:
                showcaseView.setShowcase(t4, true);
                showcaseView.setContentTitle("Costo");
                showcaseView.setContentText("Costo aproximado del viaje");
                break;
            case 4:
                showcaseView.setShowcase(t5, true);
                showcaseView.setContentTitle("Aceptar");
                showcaseView.setContentText("presione para enviar la peticion");
                showcaseView.setButtonText("Finalizar");
                break;

            case 5:
                showcaseView.hide();
                boolean muestra2 = getValuePreference(getApplicationContext());
                if(muestra2)
                {
                    saveValuePreference(getApplicationContext(), false);
                    //  Toast.makeText(getApplicationContext(),"Primera vez:"+muestra, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                showcaseView.hide();
                boolean muestra21 = getValuePreference(getApplicationContext());
                if(muestra21)
                {
                    saveValuePreference(getApplicationContext(), false);
                    //  Toast.makeText(getApplicationContext(),"Primera vez:"+muestra, Toast.LENGTH_LONG).show();
                }
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

    //////////////////////////////////Metodos para guardar y obtener datos cuando se voltea el cel_______INICIO/////////////////
    @Override
    protected void onSaveInstanceState(Bundle estado) {
        estado.putString("valor1",placeAddressText.getText().toString());
        estado.putString("valor2",TiempoTxt.getText().toString());
        estado.putString("valor3",CostoTxt.getText().toString());
        estado.putString("valor4",resultado.getText().toString());
        super.onSaveInstanceState(estado);

    }

    @Override
    protected void onRestoreInstanceState(Bundle estado) {
        super.onRestoreInstanceState(estado);
        placeAddressText.setText(estado.getString("valor1"));
        TiempoTxt.setText(estado.getString("valor2"));
        CostoTxt.setText(estado.getString("valor3"));
        resultado.setText(estado.getString("valor4"));
    }
//////////////////////////////////Metodos para guardar y obtener datos cuando se voltea el cel_______FIN////////////////////

}