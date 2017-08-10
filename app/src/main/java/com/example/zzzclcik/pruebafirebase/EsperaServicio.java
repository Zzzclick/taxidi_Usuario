package com.example.zzzclcik.pruebafirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.StringTokenizer;

public class EsperaServicio extends AppCompatActivity implements  View.OnClickListener{
    public TextView txtTiempo,txtMensaje;
    public ImageView btnCancelar;
    public boolean aux=false,aux2=false;
    public String numP,nomP,latP,lonP,idP,dirP;
    public String idTaxi,union,peticion,escuchador;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    AlertDialog alert = null;
    View viewLayout,viewLayout2;
    CountDownTimer timer = null;
    ValueEventListener listener;
    ValidatorUtil validatorUtil;
    private ShowcaseView showcaseView;
    private int contador=0;
    private Target t1,t2,t3,t4,t5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_espera_servicio);
        getSupportActionBar().setTitle("Pantalla de espera");
        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        txtTiempo = (TextView) findViewById(R.id.Tiempo);
        btnCancelar = (ImageView) findViewById(R.id.btnCancelarPeticion);
        txtMensaje = (TextView) findViewById(R.id.textView14Peticion);

        txtMensaje.setTypeface(fuente);
        txtMensaje.setTypeface(fuente);

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast,(ViewGroup)findViewById(R.id.custom_layout));

        LayoutInflater layoutInflater2 = getLayoutInflater();
        viewLayout2 = layoutInflater2.inflate(R.layout.custom_toast_sininternet,(ViewGroup)findViewById(R.id.custom_layout2));


        t1= new ViewTarget(R.id.Tiempo, this);
        t2= new ViewTarget(R.id.btnCancelarPeticion, this);

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
            contador=2;
            showcaseView.hide();

        }
        /////////////////////////Fin_______/////////////////////////////////////////////
        try {
            idTaxi=getIntent().getStringExtra("idTaxi");
            union=getIntent().getStringExtra("union");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        System.out.println("UNION"+union);
        Esperar();
        try {
            StringTokenizer st1 = new StringTokenizer(union, "¥");
            numP = st1.nextToken();//num peticion
            nomP = st1.nextToken();//nombre
            latP = st1.nextToken();//lat
            lonP = st1.nextToken();//lon
            idP = st1.nextToken();//id
            dirP = st1.nextToken();//dir
            peticion="peticion"+numP;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
                mAuth=FirebaseAuth.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        if (validatorUtil.isOnline()) {
                            AlertCancelarPetición();
                        }
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        aux2=true;
        System.out.println("Inicio de ESPERAR SEVICION");

        if (validatorUtil.isOnline()) {
            try {
                    mAuth= FirebaseAuth.getInstance();

                    mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(idP);
                    listener = mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            escuchador=dataSnapshot.child("estado").getValue().toString();

                            if (escuchador.equals("0")&&aux2)
                            {
                                if(timer != null)
                                    timer.cancel();

                                Toast.makeText(getApplicationContext(),"Petición rechazada", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            if(escuchador.equals("2")&&aux2)
                            {
                                if(timer != null)
                                    timer.cancel();

                                Toast.makeText(getApplicationContext(),"Petición Aceptada", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MapsActivityTaxi.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("idTaxi",idTaxi);
                                intent.putExtra("idUsuario",idP);
                                finish();
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_LONG);
            customToast.setGravity(Gravity.BOTTOM,0,0);
            customToast.setView(viewLayout2);
            customToast.show();
        }
    }

    public void Esperar()
    {
        timer = new CountDownTimer(60000, 1000)
        {

            @Override
            public void onTick(long millisUntilFinished)
            {
                txtTiempo.setText("Esperando respuesta\n " + (millisUntilFinished/1000));

            }

            @Override
            public void onFinish()
            {
                txtTiempo.setText("La solicitud no fue respondida");
                aux=true;
                try {
                        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                        DatabaseReference currentUserBD=mDatabase.child(idTaxi);
                        currentUserBD.child(peticion).setValue("123");
                        btnCancelar.setBackgroundResource(R.drawable.cast_skip_ad_label_border);
                        btnCancelar.setVisibility(View.INVISIBLE);
                        btnCancelar.setEnabled(false);
                        txtMensaje.setEnabled(false);
                        txtMensaje.setVisibility(View.INVISIBLE);

                        Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_LONG);
                        customToast.setGravity(Gravity.BOTTOM,0,0);
                        customToast.setView(viewLayout);
                        customToast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (validatorUtil.isOnline()) {
            timer.start();
        }
        else
        {
        Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_LONG);
        customToast.setGravity(Gravity.BOTTOM,0,0);
        customToast.setView(viewLayout2);
        customToast.show();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(aux)
        {
            if (listener != null) {
                mDatabase.removeEventListener(listener);
            }
            Intent i = new Intent(getApplicationContext(),MapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.putExtra("latitud",latP);
            i.putExtra("longitud",lonP);
            i.putExtra("idUsuario",idP);
            finish();
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Espere unos segundos",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            mDatabase.removeEventListener(listener);
        }
        aux2=false;
    }

    private void AlertCancelarPetición() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar Peticion");
        builder.setMessage("¿Está seguro de cancelar su petición?")
                .setCancelable(false)
                .setPositiveButton("Sí, estoy seguro", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if(timer != null)
                            timer.cancel();

                        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                        DatabaseReference currentUserBD=mDatabase.child(idTaxi);
                        currentUserBD.child(peticion).setValue("123");

                        if (listener != null) {
                            mDatabase.removeEventListener(listener);
                        }

                        Intent i = new Intent(getApplicationContext(),MapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra("latitud",latP);
                        i.putExtra("longitud",lonP);
                        i.putExtra("idUsuario",idP);
                        finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
            ValidatorUtil validatorUtil = new ValidatorUtil(context);
            if (!validatorUtil.isOnline())
            {
                Toast customToast = Toast.makeText(getApplicationContext(),"Toast:Gravity.Top",Toast.LENGTH_LONG);
                customToast.setGravity(Gravity.BOTTOM,0,0);
                customToast.setView(viewLayout2);
                customToast.show();
            }
            else {
                if(timer != null)
                {
                    timer.start();
                }
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
        if (listener != null) {
            mDatabase.removeEventListener(listener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            mDatabase.removeEventListener(listener);
        }
    }



    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("EsperaServicio", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("EsperaServicio", true);
    }

    @Override
    public void onClick(View v) {
        switch (contador) {
            case 0:
                showcaseView.setShowcase(t1, true);
                showcaseView.setContentTitle("Tiempo de espera");
                showcaseView.setContentText("Aqui se muestra el tiempo para que la peticion se cancele");
                break;

            case 1:
                showcaseView.setShowcase(t2, true);
                showcaseView.setContentTitle("Cancelación");
                showcaseView.setContentText("presione para cancelar la peticion");
                showcaseView.setButtonText("Finalizar");
                break;

            case 2:
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

}
