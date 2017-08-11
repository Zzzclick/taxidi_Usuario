package com.example.zzzclcik.pruebafirebase;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.google.android.gms.location.LocationServices;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.StringTokenizer;


public class Usuario extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener, View.OnClickListener {
    private Button btnLogOut;
    private ImageView imagePerfil,imagenTaxi;
    private TextView txtName,txtPerfil;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int CAMERA_REGUEST_CODE=0;
    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private DatabaseReference mDatabase,mDatabase2,mDatabaseCoord,mDatabaseUser,mDatabaseViaje;
    private static final String LOGTAG = "android-localizacion";
    AlertDialog alert = null;
    public String latAux,lonAux="0";
    public String idUsusario,tipoUser;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1 ;
    ValidatorUtil validatorUtil = null;
    private GoogleApiClient apiClient;
    LocationManager locationManager;
    View viewLayout,viewLayout2;
    private int contador=0;
    private Button enviarTodos,noTel;
    private FirebaseAuth mAuth;
   EditText telefono ;
    Button botonRegistar;
    String telefonoCel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.right_in);
        setContentView(R.layout.activity_usuario);
        getSupportActionBar().setTitle("Inicio");
        mAuth=FirebaseAuth.getInstance();
        boolean muestra = getValuePreference(getApplicationContext());
        Toast.makeText(getApplicationContext(),"Primera vez:"+muestra, Toast.LENGTH_LONG).show();

        LayoutInflater inflater = getLayoutInflater();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Usuario.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_signin, null);

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast_sininternet,(ViewGroup)findViewById(R.id.custom_layout2));

        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        txtName = (TextView) findViewById(R.id.txtNombre);
        btnLogOut = (Button) findViewById(R.id.singOut);
        txtPerfil = (TextView) findViewById(R.id.textView3Perfil);
        imagePerfil= (ImageView) findViewById(R.id.imageView);
        imagenTaxi=(ImageView)findViewById(R.id.imageGps);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        enviarTodos=(Button)findViewById(R.id.enviarTodosButton);
        enviarTodos.setVisibility(View.INVISIBLE);

        telefono = (EditText) mView.findViewById(R.id.TelEditText);
        botonRegistar = (Button) mView.findViewById(R.id.RegistarBoton2);
        noTel = (Button) mView.findViewById(R.id.cancelBoton2);

        mBuilder.setView(mView);

        final AlertDialog dialog2 = mBuilder.create();
        dialog2.show();
        try {
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        LayoutInflater layoutInflater2 = getLayoutInflater();
        viewLayout2 = layoutInflater2.inflate(R.layout.ok_calificar,(ViewGroup)findViewById(R.id.custom_layout3));
        if(getValuePreference2(getApplicationContext()))
        {
            System.out.println("AQUIIIIIIIIII2222222222"+getValuePreference2(getApplicationContext()));
            saveValuePreference2(getApplicationContext(), false);
            System.out.println("AQUIIIIIIIIII3333333333333333"+getValuePreference2(getApplicationContext()));
            Toast toast3=Toast.makeText(this,"Toast:Gravity.TOP",Toast.LENGTH_SHORT);
            toast3.setGravity(Gravity.CENTER,0,0);
            toast3.setView(viewLayout2);
            toast3.show();
        }

        if(getValuePreference3(getApplicationContext()))
        {
            dialog2.dismiss();
        }
            botonRegistar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    char[] arrayChar = telefono.getText().toString().toCharArray();
                    if(arrayChar.length<=9)
                    {
                        telefono.setError("Mínimo 10 letras");
                    }else
                    {
                         if(!telefono.getText().toString().isEmpty() )
                         {
                             telefonoCel=telefono.getText().toString();
                             Toast.makeText(getApplicationContext(),telefono.getText().toString(), Toast.LENGTH_SHORT).show();
                             InsertarTel();
                            // saveValuePreference(getApplicationContext(), true); esto es hasta la insercion del telefono
                          dialog2.dismiss();
                         }else{telefono.setError("Por favor introduce tu número telefónico");}
                    }
                }
            });

noTel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        dialog2.dismiss();
        saveValuePreference3(getApplicationContext(),true);
    }
});
        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra2 = getValuePreference(getApplicationContext());




        ////////////////////////////////////Inicio/////////////////////////////
        //aqui si no es la primera vez que se abre la activity se oculta el showCaseView
        if(!muestra2){

            saveValuePreference(getApplicationContext(), false);
            contador=3;
        }
        /////////////////////////Fin_______/////////////////////////////////////////////

        txtName.setTypeface(fuente);
        btnLogOut.setTypeface(fuente);
        txtPerfil.setTypeface(fuente);

        try {
                mAuth=FirebaseAuth.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String provedor=mAuth.getCurrentUser().getProviders().toString();
        if(provedor.contains("facebook.com"))
        {
            btnLogOut.setEnabled(false);
            btnLogOut.setWidth(1);
            btnLogOut.setHeight(1);
            btnLogOut.setActivated(false);
            btnLogOut.setAlpha(0);
        }

        if(provedor.contains("password"))
        {

        }
        try {
            idUsusario=getIntent().getStringExtra("idUsuario");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


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
        /********/

        imagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatorUtil.isOnline()) {
                    AlertCambiarFoto();
                }
                else
                    {
                        Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_SHORT);
                        customToast.setGravity(Gravity.CENTER,0,0);
                        customToast.setView(viewLayout);
                        customToast.show();
                    }

            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (validatorUtil.isOnline()) {
                    CerrarSesion();
                }
                else
                    {
                        Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_SHORT);
                        customToast.setGravity(Gravity.CENTER,0,0);
                        customToast.setView(viewLayout);
                        customToast.show();
                    }

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline()) {
                    CerrarSesion();
                }
                else
                    {
                        Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_SHORT);
                        customToast.setGravity(Gravity.CENTER,0,0);
                        customToast.setView(viewLayout);
                        customToast.show();
                    }

            }
        });
////////////////////////////////////////////////////////////
        try {
                progressDialog=new ProgressDialog(this);

                mAuth=FirebaseAuth.getInstance();
               String miId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if(miId!=null)
                        {
                            mStorage= FirebaseStorage.getInstance().getReference();
                            mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
                            mDatabase.child(miId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    try {
                                        txtName.setText(dataSnapshot.child("name").getValue().toString());
                                        String imageUrl=dataSnapshot.child("image").getValue().toString();
                                        if(!imageUrl.equals("default")|| TextUtils.isEmpty(imageUrl))
                                        {
                                            Picasso.with(Usuario.this).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).into(imagePerfil);
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else{
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            finish();
                            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                             }



        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        imagenTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (validatorUtil.isOnline()) {
                    if(getValuePreference3(getApplicationContext()))
                    {
                        Intent i = new Intent(getApplicationContext(),EnviarATodos.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra("latitud",latAux);
                        i.putExtra("longitud",lonAux);
                        i.putExtra("idUsuario",idUsusario);
                        //finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }else
                        {
                            Toast.makeText(getApplicationContext(),"Debe registrar su  numero celular", Toast.LENGTH_SHORT).show();
                            dialog2.show();
                        }
                } else
                {
                    Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_SHORT);
                    customToast.setGravity(Gravity.CENTER,0,0);
                    customToast.setView(viewLayout);
                    customToast.show();
                }

            }
        });


  enviarTodos.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          //mDatabase.removeEventListener(even);

          if (validatorUtil.isOnline()) {
              Intent i = new Intent(Usuario.this, EnviarATodos.class );
              i.putExtra("latitud",latAux);
              i.putExtra("longitud",lonAux);
              i.putExtra("idUsuario",idUsusario);
              startActivity(i);
              overridePendingTransition(R.anim.left_in, R.anim.left_out);
          }

      }
  });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intent, "Seleciona una foto de perfil"), CAMERA_REGUEST_CODE);
                    }
                } else {

                }
                return;
            }
        }
    }

    public String getRamdomString()
    {
        SecureRandom random =new SecureRandom();
        return new BigInteger(130,random).toString(32);
    }
    @Override
    protected void onStart()
    {
        /*///////PARA SACAR USUARIO QUE NO ESTE EN LA REGION_INICIO
        if(latAux>=20.194997&&latAux<=19.987116)
        {
            if((longuitud*-1)>=98.891369&&(longuitud*-1)<=98.251915)
            {

            }
            else {Sacar al usuario
            }
        }
        else {
                //Sacar al usuario
                }
        *//////////PARA SACAR USUARIO QUE NO ESTE EN LA REGION_FIN
        super.onStart();
        EscuchadoViaje();
        boolean muestra4 = getValuePreference4(getApplicationContext());
        System.out.println("valor de terminos "+muestra4);
        if(!muestra4)
        {
            AceptarTerminos();
            saveValuePreference4(getApplicationContext(), true);
        }

        VerificarTipo();
            try {

                    mDatabase=FirebaseDatabase.getInstance().getReference().child("users");
                    final DatabaseReference currentUserBD=mDatabase.child(mAuth.getCurrentUser().getUid());
                    currentUserBD.child("latitud").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            latAux=value;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    currentUserBD.child("longitud").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            lonAux=value;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS está desactivado, ¿Deseas activarlo?")
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
    protected void onDestroy(){
        super.onDestroy();
        if(alert != null)
        {
            alert.dismiss ();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            try {
                if(requestCode==CAMERA_REGUEST_CODE&&resultCode==RESULT_OK)
                {
                    if(mAuth.getCurrentUser()==null)
                    return;
                    progressDialog.setMessage("Subiendo imagen");
                    progressDialog.show();
                    final Uri uri=data.getData();
                    if(uri==null)
                    {
                        progressDialog.dismiss();
                        return;
                    }
                    if(mAuth.getCurrentUser()==null)
                        return;
                    if(mStorage==null)
                        mStorage=FirebaseStorage.getInstance().getReference();
                    if(mDatabase==null)
                        mDatabase=FirebaseDatabase.getInstance().getReference().child("users");
                    final StorageReference filepath=mStorage.child("photo").child(getRamdomString());
                    final DatabaseReference currentUserBD=mDatabase.child(mAuth.getCurrentUser().getUid());

                        currentUserBD.child("image").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                            String image=dataSnapshot.getValue().toString();
                                try {
                                    if(!image.equals("default")&&!image.isEmpty())
                                    {

                                        Task<Void> task=FirebaseStorage.getInstance().getReferenceFromUrl(image).delete();
                                        task.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(Usuario.this, "Foto antigua eliminada correctamente", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(Usuario.this, "Ocurrió un error al eliminar la foto antigua", Toast.LENGTH_SHORT).show();
                                            }

                                        });



                                    }
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }


                                currentUserBD.child("image").removeEventListener(this);
                                filepath.putFile(uri).addOnSuccessListener(Usuario.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressDialog.dismiss();
                                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                                        Toast.makeText(Usuario.this, "Finalizado", Toast.LENGTH_SHORT).show();
                                        Picasso.with(Usuario.this).load(uri).fit().centerCrop().into(imagePerfil);
                                        DatabaseReference currentUserDB=mDatabase.child(mAuth.getCurrentUser().getUid());
                                        currentUserDB.child("image").setValue(downloadUri.toString());
                                    }
                                }).addOnFailureListener(Usuario.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Usuario.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Toast.makeText(Usuario.this, "Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Toast.makeText(Usuario.this, "Error grave al conectar con Google Play Services", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(Location loc) {
        try {
                if (loc != null) {
                    mAuth=FirebaseAuth.getInstance();
                    String user_id = mAuth.getCurrentUser().getUid();
                    if (user_id != null) {
                        DatabaseReference mDataBaseCoord = mDatabaseCoord.child(user_id);
                        mDataBaseCoord.child("latitud").setValue(String.valueOf(loc.getLatitude()));
                        mDataBaseCoord.child("longitud").setValue(String.valueOf(loc.getLongitude()));
                    }

                } else {
                }
        } catch (NullPointerException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_phrase:

                Intent i = new Intent(Usuario.this,CalificarTaxi.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try
        {
                if (location != null) {
                    mAuth=FirebaseAuth.getInstance();
                    String user_id = mAuth.getCurrentUser().getUid();
                    if (user_id != null) {
                        DatabaseReference mDatabaseCoord2 = mDatabaseCoord.child(user_id);
                        mDatabaseCoord2.child("latitud").setValue(String.valueOf(location.getLatitude()));
                        mDatabaseCoord2.child("longitud").setValue(String.valueOf(location.getLongitude()));
                    }
                } else {
                }
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }
    private void CerrarSesion()
    {
        if(mAuth.getCurrentUser().getUid()!=null)
        {
            LoginManager.getInstance().logOut();
            mAuth.signOut();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    private void AlertCambiarFoto() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar foto de perfil");
        builder.setMessage("¿Desea cambiar su foto de perfil?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if(ActivityCompat.checkSelfPermission(Usuario.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(Usuario.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                        else {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(Intent.createChooser(intent, "Seleciona una foto de perfil"), CAMERA_REGUEST_CODE);
                            }
                        }
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

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            validatorUtil = new ValidatorUtil(context);
            if (!validatorUtil.isOnline())
            {
                Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_SHORT);
                customToast.setGravity(Gravity.CENTER,0,0);
                customToast.setView(viewLayout);
                customToast.show();
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
    }


    public void VerificarTipo() {

        try {
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("tipo");
            mDatabaseUser.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        System.out.println("WWWW"+mDatabaseUser);
                        tipoUser = dataSnapshot.getValue().toString();
                        Toast.makeText(getApplicationContext(), "Tipo Usuario:" + tipoUser, Toast.LENGTH_SHORT).show();

                        if (tipoUser.equals("usuario"))
                        {
                            Toast.makeText(getApplicationContext(), "Usuario correcto:" , Toast.LENGTH_SHORT).show();
                        }
                        else if(tipoUser.equals("taxi"))
                        {

                            Toast.makeText(getApplicationContext(),"Esta cuenta ya está como conductor\n por favor use otra cuenta para usar la aplicacion",Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),"Debe usar otra cuenta",Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }else{
                            System.out.println(" QQQ_Error en tipo de usuario");
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.out.println("|||||||||||||||||||||Trono aqui En Tipo de usuario");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
        }

    }
///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("usuario", mostrar);
        editor.commit();
    }
    public void saveValuePreference2(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("SiCalifico", mostrar);
        editor.commit();
    }
    public void saveValuePreference3(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("SiTelefono", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("usuario", true);
    }
    public boolean getValuePreference2(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("SiCalifico",false);
    }
    public boolean getValuePreference3(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("SiTelefono",false);
    }

    @Override
    public void onClick(View v) {


        contador++;
    }

    ///////////////////////////////////////Para el showCaseView por primera vez___Fin///////////////////////////////////////////
public void InsertarTel()
{
    try {
            mAuth=FirebaseAuth.getInstance();
            String user_id = mAuth.getCurrentUser().getUid();
            if (user_id != null) {
                if(!telefonoCel.isEmpty())
                {
                DatabaseReference currentUserBD = mDatabase.child(user_id);
                currentUserBD.child("telefono").setValue(telefonoCel);
                saveValuePreference3(getApplicationContext(),true);
                }
            }

    } catch (NullPointerException e) {e.printStackTrace();}
    catch (Exception e) {e.printStackTrace();}


}


    public void AceptarTerminos()
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Esta aplicación  no es responsable de del mal y de los daños o derivados que puede suceder durante el viaje y uso de la aplicación ");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Estoy de acuerdo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar();
            }
        });
        dialogo1.setNegativeButton("No acepto", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();
            }
        });
        dialogo1.show();
    }
    public void aceptar() {
        Toast t=Toast.makeText(this,"Bienvenido", Toast.LENGTH_SHORT);
        t.show();
        // doLogin();
    }

    public void cancelar() {
        finish();
        Toast t=Toast.makeText(this,"Lo sentimos pero no puedes usar la apliacion ", Toast.LENGTH_SHORT);
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);


    }


    public void saveValuePreference4(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("terminos", mostrar);
        editor.commit();
    }



    public boolean getValuePreference4(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("terminos", false);
    }
    public void EscuchadoViaje()
    {
        try {
            mDatabaseViaje = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabaseViaje= mDatabaseViaje.child(mAuth.getCurrentUser().getUid()).child("ViajeA");
            mDatabaseViaje.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {

                       String idUsuario=mAuth.getCurrentUser().getUid();
                        idUsuario=idUsuario.replace("$","");
                        System.out.println("WWWW2"+mDatabaseViaje);
                        String escuchadorViaje = dataSnapshot.getValue().toString();
                        StringTokenizer token = new StringTokenizer(escuchadorViaje, "#");
                        String idTaxi="nada",estado;
                        estado=token.nextToken();
                        idTaxi=token.nextToken();
                        System.out.println("estado="+estado+"\nid="+idTaxi);
                        if (!escuchadorViaje.equals("0#vacio"))
                        {
                            if (validatorUtil.isOnline()) {
                                Intent intent = new Intent(getApplicationContext(),MapsActivityTaxi.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("idTaxi",idTaxi);
                                intent.putExtra("idUsuario",idUsuario);
                                System.out.println("Cargando datos de viaje22\n"+idTaxi+" \n"+idUsuario);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"Cargando datos de viaje", Toast.LENGTH_SHORT).show();
                            }else Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                            System.out.println("QQQ pasa pasa");
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.out.println("|||||||||||||||||||||Trono aqui En cargar Viaje");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
        }
    }

}
