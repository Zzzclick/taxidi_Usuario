package com.example.zzzclcik.pruebafirebase;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView infoTextView,resetClave;
    private Boolean aux=false;
    public String idUsuario;
    boolean SiEstaEnViaje=false;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    ValueEventListener listener;
    DatabaseReference ref;
    DatabaseReference mensajeRef;
    View viewLayout,viewLayout2;
    DatabaseReference ref2;
    DatabaseReference mensajeRef2;
    private int contador=0;
    ValidatorUtil validatorUtil = null;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String TAG = "NOTICIAS";

    private CallbackManager mCallbackManager;

    String id,emailF,nameF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Iniciar Sesión");
        validatorUtil = new ValidatorUtil(getApplicationContext());

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast_sininternet,(ViewGroup)findViewById(R.id.custom_layout2));

        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);



        try {
                ref = FirebaseDatabase.getInstance().getReference();
                mensajeRef = ref.child("mensaje");

                ref2 = FirebaseDatabase.getInstance().getReference();
                mensajeRef2 = ref2.child("ubicacion");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
                FirebaseInstanceId.getInstance().getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressDialog=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();


        try {

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra = getValuePreference(getApplicationContext());




        if(!muestra){

            saveValuePreference(getApplicationContext(), false);
            contador=5;

        }
        /////////////////////////Fin_______/////////////////////////////////////////////
       




        String token = null;
        try {
            token = FirebaseInstanceId.getInstance().getToken();

        } catch (Exception e) {
            e.printStackTrace();
        }



////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////
        if (getIntent().hasExtra("logout")) {
            LoginManager.getInstance().logOut();
        }
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        loginButton.setTypeface(fuente);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.d("", "facebook:onSuccess:" + loginResult);
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
            }
        });

        try {
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());

                        Intent intent = new Intent(MainActivity.this, Usuario.class);
                        String email = user.getEmail();
                        String name = user.getDisplayName();

                        System.out.println("55555555\n"+email+"\n"+name+"\n"+firebaseAuth.getCurrentUser().getUid());
                        System.out.println("888"+user.getProviderId());
                        intent.putExtra("idUsuario",user.getUid());

                    } else {
                        Log.d("TG", "SIGNED OUT");
                    }
                }
            };
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
/////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////







        try {
                mAuthListener=new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                        if(firebaseAuth.getCurrentUser()!=null)
                        { String email = firebaseAuth.getCurrentUser().getEmail();
                            String name = firebaseAuth.getCurrentUser().getDisplayName();
                            id=firebaseAuth.getCurrentUser().getUid();
                            emailF=firebaseAuth.getCurrentUser().getEmail();
                            nameF=firebaseAuth.getCurrentUser().getDisplayName();
                            System.out.println("AQUI222222222222222222222   "+id);

                            Intent i = new Intent(getApplicationContext(),Usuario.class);
                            overridePendingTransition(R.anim.left_in,R.anim.left_out);
                            i.putExtra("idUsuario",id);
                            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));finish();

                            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
                            idUsuario=firebaseAuth.getCurrentUser().getUid();
                            System.out.println("Paso   QQQQQ");
                            mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String ValorViaje="";
                                    try {
                                        ValorViaje = (dataSnapshot.child("ViajeA").getValue().toString());

                                        if(ValorViaje!=null||!ValorViaje.equals(""))
                                        {
                                            StringTokenizer token = new StringTokenizer(ValorViaje, "#");
                                            String idTaxi="nada",estado;
                                            estado=token.nextToken();
                                            idTaxi=token.nextToken();



                                            if(!ValorViaje.equals("0#vacio")){
                                                ////
                                                SiEstaEnViaje=true;
                                            }
                                            if(SiEstaEnViaje)
                                            {
                                                if (validatorUtil.isOnline()) {
                                                    Intent intent = new Intent(getApplicationContext(),MapsActivityTaxi.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("idTaxi",idTaxi);
                                                    intent.putExtra("idUsuario",idUsuario);
                                                    System.out.println("Cargando datos de viaje\n"+idTaxi+" \n"+idUsuario);
                                                    startActivity(intent);
                                                    Toast.makeText(getApplicationContext(),"Cargando datos de viaje", Toast.LENGTH_SHORT).show();
                                                }else Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                                            }

                                        }
                                        //(5
                                    } catch (NullPointerException e) { e.printStackTrace();
                                        System.out.println("|||||||||||||||||||||TRono aqui "); }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //mAuth.signOut();
                        }
                    }
                };
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(listener != null)
        {
            mDatabase.removeEventListener(listener);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Autenticación fallida.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if(task.isSuccessful())
                        {
                            if(id!=null)
                            {obtenerDatosUsuario();
                                Toast.makeText(MainActivity.this, "Autenticación exitosa",Toast.LENGTH_SHORT).show();
                            }


                        }

                    }
                });
    }
    public void obtenerDatosUsuario()
    {

        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(id);

        listener = mDatabase.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {String Nombre="";
                try {
                    Nombre= dataSnapshot.child("name").getValue().toString();
                    System.out.println("12134543211123435 "+Nombre);
                } catch (NullPointerException e) {e.printStackTrace();}


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        System.out.println(id);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            DatabaseReference currentUserBD = mDatabase.child(id);
        System.out.println("id "+id+" QQQ "+currentUserBD);
            currentUserBD.child("name").setValue(nameF);
            currentUserBD.child("email").setValue(emailF);

            DatabaseReference currentUserBD2 = FirebaseDatabase.getInstance().getReference().child("tipo");
            currentUserBD2.child(id).setValue("usuario");

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ValidatorUtil validatorUtil = new ValidatorUtil(context);
            if (!validatorUtil.isOnline())
            {
                Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_LONG);
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
        if(listener != null)
        {
            mDatabase.removeEventListener(listener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listener != null)
        {
            mDatabase.removeEventListener(listener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listener != null)
        {
            mDatabase.removeEventListener(listener);
        }
    }
    ///////////////////////////////////////////////OnClickEscuchador/////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {


        contador++;

    }
///////////////////////////////////////////////OnClickEscuchador/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("MainActivity", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("MainActivity", true);
    }
    public boolean getValuePreference2(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("SiCalifico",false);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
