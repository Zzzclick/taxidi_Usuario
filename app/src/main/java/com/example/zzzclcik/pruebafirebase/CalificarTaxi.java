package com.example.zzzclcik.pruebafirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

public class CalificarTaxi extends AppCompatActivity {
    public RatingBar barA,barM,barU,barS,barAt;

    Button btnEnviar;
    TextView txtTitulo,txtAmabilidad,txtManejo,txtUnidad,txtServicio,txtAtencion;
    EditText comen;
    double [] auxPuntosDeCalificacion = new double[6];
    String [] puntosDeCalificacion = new String[6];
    int [] contadores = new int[5];
    int numComentario;
    String idTaxi,idUsuario,nomUsuario;
    private DatabaseReference mDatabase;
    private ValueEventListener listener;
    ValidatorUtil validatorUtil = null;
    View viewLayout,viewLayout2;
    private ShowcaseView showcaseView;
    private int contador=0;
    private Target t1,t2,t3,t4,t5;
    String CometarioUlt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calificar_taxi);
        getSupportActionBar().setTitle("Califica a tu operador");
        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast_sininternet,(ViewGroup)findViewById(R.id.custom_layout2));
        LayoutInflater layoutInflater2 = getLayoutInflater();
        viewLayout2 = layoutInflater2.inflate(R.layout.ok_calificar,(ViewGroup)findViewById(R.id.custom_layout3));

        barA=(RatingBar)findViewById(R.id.ratingBarTaxis);
        barM=(RatingBar)findViewById(R.id.ratingBarTaxis1);
        barU=(RatingBar)findViewById(R.id.ratingBarTaxis2);
        barS=(RatingBar)findViewById(R.id.ratingBarTaxis3);
        barAt=(RatingBar)findViewById(R.id.ratingBarTaxis4);
        btnEnviar=(Button)findViewById(R.id.Enviarbutton);
        comen=(EditText)findViewById(R.id.comentarioEditText);
        txtTitulo = (TextView)findViewById(R.id.textViewTitulo);
        txtAmabilidad = (TextView)findViewById(R.id.textViewAmabilidad);
        txtAtencion = (TextView)findViewById(R.id.textViewAtencion);
        txtManejo = (TextView)findViewById(R.id.textViewManejo);
        txtUnidad = (TextView)findViewById(R.id.textViewUnidad);
        txtServicio = (TextView)findViewById(R.id.textViewServicio);

        idTaxi=getIntent().getStringExtra("idTaxi");
        idUsuario=getIntent().getStringExtra("idUsuario");
        nomUsuario=getIntent().getStringExtra("nomUsuario");
        System.out.println(idTaxi+"\n"+idUsuario+"\n"+nomUsuario);
        Toast.makeText(getApplicationContext(),idTaxi+"\n"+idUsuario+"\n"+nomUsuario, Toast.LENGTH_LONG).show();

        barA.setRating(5);
        barM.setRating(5);
        barU.setRating(5);
        barS.setRating(5);
        barAt.setRating(5);

        comen.setTypeface(fuente);
        btnEnviar.setTypeface(fuente);
        txtTitulo.setTypeface(fuente);
        txtAmabilidad.setTypeface(fuente);
        txtAtencion.setTypeface(fuente);
        txtManejo.setTypeface(fuente);
        txtUnidad.setTypeface(fuente);
        txtServicio.setTypeface(fuente);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatorUtil.isOnline()) {
                    MandarDatos();
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setRatingTaxi(float amabilidadf, float manejof, float unidadf, float serviciof, float atencionf) {

        try {
            StringTokenizer token = new StringTokenizer(puntosDeCalificacion[0], "#");
            StringTokenizer token1 = new StringTokenizer(puntosDeCalificacion[1], "#");
            StringTokenizer token2 = new StringTokenizer(puntosDeCalificacion[2], "#");
            StringTokenizer token3 = new StringTokenizer(puntosDeCalificacion[3], "#");
            StringTokenizer token4 = new StringTokenizer(puntosDeCalificacion[4], "#");

            auxPuntosDeCalificacion[0] = Double.parseDouble(token.nextToken());
            contadores[0] = Integer.parseInt(token.nextToken());
            auxPuntosDeCalificacion[1] = Double.parseDouble(token1.nextToken());
            contadores[1] = Integer.parseInt(token1.nextToken());
            auxPuntosDeCalificacion[2] = Double.parseDouble(token2.nextToken());
            contadores[2] = Integer.parseInt(token2.nextToken());
            auxPuntosDeCalificacion[3] = Double.parseDouble(token3.nextToken());
            contadores[3] = Integer.parseInt(token3.nextToken());
            auxPuntosDeCalificacion[4] = Double.parseDouble(token4.nextToken());
            contadores[4] = Integer.parseInt(token4.nextToken());

            auxPuntosDeCalificacion[0] = auxPuntosDeCalificacion[0] + amabilidadf;
            contadores[0]++;
            auxPuntosDeCalificacion[1] = auxPuntosDeCalificacion[1] + manejof;
            contadores[1]++;
            auxPuntosDeCalificacion[2] = auxPuntosDeCalificacion[2] + unidadf;
            contadores[2]++;
            auxPuntosDeCalificacion[3] = auxPuntosDeCalificacion[3] + serviciof;
            contadores[3]++;
            auxPuntosDeCalificacion[4] = auxPuntosDeCalificacion[4] + atencionf;
            contadores[4]++;
            auxPuntosDeCalificacion[5] = (((auxPuntosDeCalificacion[0]/contadores[0]) + (auxPuntosDeCalificacion[1]/contadores[1]) + (auxPuntosDeCalificacion[2]/contadores[2]) + (auxPuntosDeCalificacion[3]/contadores[3]) + (auxPuntosDeCalificacion[4]/contadores[4])) / 5);

            puntosDeCalificacion[0] = auxPuntosDeCalificacion[0] + "#" + contadores[0];
            puntosDeCalificacion[1] = auxPuntosDeCalificacion[1] + "#" + contadores[1];
            puntosDeCalificacion[2] = auxPuntosDeCalificacion[2] + "#" + contadores[2];
            puntosDeCalificacion[3] = auxPuntosDeCalificacion[3] + "#" + contadores[3];
            puntosDeCalificacion[4] = auxPuntosDeCalificacion[4] + "#" + contadores[4];
            puntosDeCalificacion[5] = String.valueOf((Math.rint(auxPuntosDeCalificacion[5]*10)/10));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }catch (Exception ex)
        {
            ex.toString();
        }
    }
    public void MandarDatos()
    {
            comen.getText();
            setRatingTaxi(barA.getRating(),barM.getRating(),barU.getRating(),barS.getRating(),barAt.getRating());

            DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
            DatabaseReference currentUserBD=mDatabase.child(idTaxi);
        System.out.println("\n\n"+barA.getRating()+"\n"+barM.getRating()+"\n"+barU.getRating()+"\n"+barS.getRating()+"\n"+barAt.getRating());
        System.out.println("\n\n"+currentUserBD+"\n\n");
            currentUserBD.child("amabilidad").setValue(puntosDeCalificacion[0]);
            currentUserBD.child("manejo").setValue(puntosDeCalificacion[1]);
            currentUserBD.child("unidad").setValue(puntosDeCalificacion[2]);
            currentUserBD.child("servicio").setValue(puntosDeCalificacion[3]);
            currentUserBD.child("atencion").setValue(puntosDeCalificacion[4]);
            currentUserBD.child("rating").setValue(puntosDeCalificacion[5]);
            if(comen.getText().toString().equals("")||comen.getText().toString().equals("")||comen.getText().toString().equals(" "))
            {
                Toast.makeText(CalificarTaxi.this,"Sin comentario",Toast.LENGTH_SHORT).show();

            }else
            {
                String aux=Integer.toString(numComentario);
                DatabaseReference currentUserBD2=currentUserBD.child("comentarios");
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                Date fecha = new Date();
                String fecha2=formato.format(fecha);
                currentUserBD2.child(aux).setValue(nomUsuario+"       "+fecha2+"#"+comen.getText().toString());
            }
        saveValuePreference(getApplicationContext(),true);




            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();

        startActivity(i);
      //  System.exit(0);

    }
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("SiCalifico", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("SiCalifico",false);
    }
    public void saveValuePreference2(Context context, String mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString("idTaxi", mostrar);
        editor.commit();
    }



    public String getValuePreference2(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString("idTaxi","vacio");
    }
    public void obtenerRating()
    {
        try {
                mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis").child(idTaxi);

                listener = mDatabase.addValueEventListener(new ValueEventListener()
                {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        puntosDeCalificacion[0] = dataSnapshot.child("amabilidad").getValue().toString();
                        puntosDeCalificacion[1] = dataSnapshot.child("manejo").getValue().toString();
                        puntosDeCalificacion[2] = dataSnapshot.child("unidad").getValue().toString();
                        puntosDeCalificacion[3] = dataSnapshot.child("servicio").getValue().toString();
                        puntosDeCalificacion[4] = dataSnapshot.child("atencion").getValue().toString();
                        puntosDeCalificacion[5] = dataSnapshot.child("rating").getValue().toString();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void ObtenerComentarios()
    {
        try {
            if (validatorUtil.isOnline()) {
                mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                mDatabase=mDatabase.child(idTaxi);

                mDatabase.child("comentarios").limitToLast(1).addValueEventListener(new ValueEventListener()
                {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        CometarioUlt= dataSnapshot.getValue().toString();
                        System.out.println(CometarioUlt);
                        CometarioUlt=CometarioUlt.replace("{","");
                        CometarioUlt=CometarioUlt.replace("}","");
                        StringTokenizer token = new StringTokenizer(CometarioUlt, "=");
                        String aux;
                        if(CometarioUlt!=null)
                        {
                            aux = token.nextToken();
                            aux=aux.replace("{","");
                            System.out.println(aux+"Aquiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

                            numComentario=Integer.parseInt(aux);

                            System.out.println(numComentario+"===================");
                            numComentario=numComentario+1;
                            System.out.println(numComentario+"+1 ===================");
                        }else{
                            Toast.makeText(CalificarTaxi.this,"Verifique su conexion",Toast.LENGTH_SHORT).show();}

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        obtenerRating();
        ObtenerComentarios();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"La calificación es obligatoria",Toast.LENGTH_SHORT).show();
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
        if (listener != null) {
            mDatabase.removeEventListener(listener);
        }
        System.exit(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            mDatabase.removeEventListener(listener);
        }
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            mDatabase.removeEventListener(listener);
        }
        System.exit(0);
    }


}


