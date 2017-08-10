package com.example.zzzclcik.pruebafirebase;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.util.StringTokenizer;


public class EnviarATodos extends AppCompatActivity
{
    public Button enviarBoton;
    ValidatorUtil validatorUtil = null;
    String idUsusario="Ninguno";
    //para el gps y actualizacion_inicio/////////////////////////////////
    LocationManager locationManager;
    double longitudeBest, latitudeBest;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    //para el gps y actualizacion_fin/////////////////////////////////
    //////////prara el place picker///////////////////////////////////////////
    TextView placeNameText,placeAddressText,CostoTxt,TiempoTxt,resultado;
    WebView attributionText;
    Button getPlaceButton, btnAceptar;
    private final static int PLACE_PICKER_REQUEST = 1;
    double latitudDestino,longitudDestino;
    double distancia;
    String tiempoAux;
    String latitudOrigen,longitudOrigen;
    ObtenerWebService hiloconexion;
    //////////prara el place picker///////////////////////////////////////////
    private FirebaseAuth mAuth;
    String MiId,id_Usuario;
    private boolean UbicacionObenida=false;
    private EditText despripcion;
    private  final static LatLngBounds bounds=new LatLngBounds(new LatLng(32.6393,-68.004304),new LatLng (33.901184 ,-67.32254));
    View viewLayout,viewLayout2;
    private DatabaseReference mDatabase,mDatabaseCordenadas;
    ValueEventListener listener1, listener2,listenerCoordenadas;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_atodos);
        getSupportActionBar().setTitle("Enviar peticiones");
        mAuth=FirebaseAuth.getInstance();
         id_Usuario = mAuth.getCurrentUser().getUid();
        MiId =id_Usuario;

        //Toast.makeText(getApplicationContext(),"id de mAuth "+id_Usuario, Toast.LENGTH_SHORT).show();
        LayoutInflater layoutInflater2 = getLayoutInflater();
        viewLayout2 = layoutInflater2.inflate(R.layout.ok,(ViewGroup)findViewById(R.id.custom_layout4));

        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);
        enviarBoton=(Button)findViewById(R.id.enviarButton);
        placeNameText = (TextView) findViewById(R.id.tvPlaceName);
        placeAddressText = (TextView) findViewById(R.id.tvPlaceAddress);
        attributionText = (WebView) findViewById(R.id.wvAttribution);
        getPlaceButton = (Button) findViewById(R.id.btGetPlace);
        btnAceptar = (Button) findViewById(R.id.btnAceptar);

        resultado = (TextView)findViewById(R.id.resultado);
        TiempoTxt=(TextView) findViewById(R.id.TiempoTextView);
        CostoTxt=(TextView) findViewById(R.id.CostoTextView);
        despripcion=(EditText)findViewById(R.id.editTextDescripcionUbicacion);

        placeNameText.setTypeface(fuente);
        despripcion.setTypeface(fuente);
        placeAddressText.setTypeface(fuente);
        getPlaceButton.setTypeface(fuente);
        // btnAceptar.setTypeface(fuente);
        resultado.setTypeface(fuente);
        TiempoTxt.setTypeface(fuente);
        CostoTxt.setTypeface(fuente);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        CordenadasInicio();
        GpsActualizacion();

        enviarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    if(UbicacionObenida)
                    {
                        EnviarPeticion();
                    }else{Toast.makeText(getApplicationContext(),"espere a obtener una ubicacion", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        getPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validatorUtil.isOnline()) {
                    if(latitudOrigen!=null&&longitudOrigen!=null&&!latitudOrigen.equals("0")&&!longitudOrigen.equals("0"))
                    {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        //builder.setLatLngBounds(bounds);
                        builder.setLatLngBounds(new LatLngBounds(new LatLng(20.137797, -98.375485), new LatLng(21.131044, -98.388337)));
                        try {
                            Intent intent = builder.build(EnviarATodos.this);
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }

                    }else{Toast.makeText(getApplicationContext(),"Espere obteniendo ubicacion \nintentelo un poco mas tarde", Toast.LENGTH_SHORT).show();}
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        });
    }
////////////////////////////////////////////////////////////////////////////GPS_inicio////////////////////////////////////////////////////////////////////////////

private boolean checkLocation()
{
if (!isLocationEnabled())
showAlert();
return isLocationEnabled();
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

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void GpsActualizacion()
    {
        checkLocation();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PETICION_PERMISO_LOCALIZACION);
        }
        else
        {
          //  Toast.makeText(getApplicationContext(),"Permisos concedidos", Toast.LENGTH_SHORT).show();
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final android.location.LocationListener locationListenerBest = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            latitudeBest = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    latitudOrigen=String.valueOf(latitudeBest);
                    longitudOrigen=String.valueOf(longitudeBest);
                    Toast.makeText(getApplicationContext(),"Ubicacion obtenida"+ "\nlonguitid:"+longitudeBest+"\n"+"latitud:"+latitudeBest, Toast.LENGTH_SHORT).show();
                    UbicacionObenida=true;
                    try {
                        mDatabaseCordenadas.removeEventListener(listenerCoordenadas);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    try
                    {
                        if (latitudOrigen != null&&longitudOrigen!=null) {


                            if (id_Usuario != null) {

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                DatabaseReference currentUserBD = mDatabase.child(id_Usuario);
                                currentUserBD.child("latitud").setValue(latitudOrigen.toString());
                                currentUserBD.child("longitud").setValue(longitudOrigen.toString());
                            }
                        } else {
                        }
                    }catch (NullPointerException e)
                    {
                        e.printStackTrace();
                    }
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
////////////////////////////////////////////////////////////////////////////GPS_Fin////////////////////////////////////////////////////////////////////////////
/////////////////////////PlacePicker y calculo de tarifa_Inicio////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(EnviarATodos.this, data);
                placeNameText.setText(place.getName());
                placeAddressText.setText(place.getAddress());

                latitudDestino=place.getLatLng().latitude;
                longitudDestino=place.getLatLng().longitude;
                System.out.println("CCC "+latitudDestino+" CCC "+longitudDestino);
                if(latitudDestino>=20.044997&&latitudDestino<=20.141020)
                {
                    System.out.println("\n\n\n\nEntro al primer IF");
                    if((longitudDestino*-1)>=(98.681905)&&(longitudDestino*-1)<=(98.812860))
                    {
                        System.out.println("\n\n\n\nEntro al segundo IF");

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
    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=38.404593,-0.529534&sensor=false
            cadena = cadena + params[0];
            cadena = cadena + "&destinations=";
            cadena = cadena + params[1];
            cadena = cadena + "&language=es&key=AIzaSyDldoJ330Eq-cBIZGD9lMs9_FzeucK5Ctc";
            System.out.println("\n\n\n La url es\n"+cadena+"\n\n\n");

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
/////////////////////////PlacePicker y calculo de tarifa_Fin////////////////////////////////////////////////////////////////////////////////////////////////
public void EnviarPeticion()
{
    try{
        if (latitudOrigen!=null&&longitudOrigen!=null)
        {
            if(!despripcion.getText().toString().equals(""))
            {
                String union;
                String tiempo = TiempoTxt.getText().toString();

                System.out.println("Aqui "+TiempoTxt.getText());
                union = latitudOrigen + "_" + longitudOrigen + "_" +  despripcion.getText().toString() + "_" + tiempo;
                System.out.println("\n\n" + union + "\n\n");
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("peticiones");
                mDatabase.child(id_Usuario).setValue(union);
                Toast toast3=Toast.makeText(this,"Toast:Gravity.TOP",Toast.LENGTH_SHORT);
                toast3.setGravity(Gravity.CENTER,0,0);
                toast3.setView(viewLayout2);
                toast3.show();

                Intent intent = new Intent(getApplicationContext(),EsperaTaxis.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }else{Toast.makeText(getApplicationContext(),"Por favor describe tu ubicacion", Toast.LENGTH_SHORT).show();despripcion.setError("Describe el lugar donde te encuentras");}
        }
    else{Toast.makeText(getApplicationContext(),"ubicacion desconocida intentelo de nuevo\nmas tarde", Toast.LENGTH_SHORT).show();}
    }catch (NullPointerException e)
    {

    }
}

    public void CordenadasInicio()
    {

        DatabaseReference currentUserBD = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseCordenadas =currentUserBD.child(MiId);
        System.out.println("Entro\n "+mDatabaseCordenadas+"\n"+latitudeBest+"\n"+longitudeBest);
        listenerCoordenadas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try {
                    latitudeBest=Double.parseDouble(dataSnapshot.child("latitud").getValue().toString());
                    longitudeBest=Double.parseDouble(dataSnapshot.child("longitud").getValue().toString());
                    //Toast.makeText(getApplicationContext(),"Entro\n "+mDatabaseCordenadas+"\n"+latitudeBest+"\n"+longitudeBest, Toast.LENGTH_SHORT).show();
                    UbicacionObenida=true;
                    }catch (NullPointerException e) { e.printStackTrace();  }
                catch (NumberFormatException e) { e.printStackTrace();  }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabaseCordenadas.addValueEventListener(listenerCoordenadas);
    }

}

