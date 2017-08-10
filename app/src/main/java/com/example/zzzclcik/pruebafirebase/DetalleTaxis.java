package com.example.zzzclcik.pruebafirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class DetalleTaxis extends AppCompatActivity implements View.OnClickListener{
    private TextView Nombre2,placasText,comentariosTxt,calificacion;
    private RatingBar bar;
    private ImageView foto;
    private Button mandar;
    String imageUrl;
    String placas ;
    String idTaxi;
    private FirebaseAuth mAuth;
    String latU,lonU;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    public String idUsusario,union,union2;
    String Rating="0",CometarioUlt;
    ArrayList<String> ArraynumeroComentarios=new ArrayList<>();
    ValueEventListener listener1, listener2;
    ValidatorUtil validatorUtil = null;

    private ShowcaseView showcaseView;
    private int contador=0;
    private Target t1,t2,t3,t4,t5,t6,t7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        getSupportActionBar().setTitle("Detalle de taxis");
        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        try {
            idUsusario=getIntent().getStringExtra("idUsuario");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_detalle_taxis);
        String Nombre = null;
        String snipe = null;
        try {
            Nombre = getIntent().getStringExtra("nombre");
            snipe = getIntent().getStringExtra("foto");
            latU = getIntent().getStringExtra("latitud");
            lonU = getIntent().getStringExtra("longitud");
            union = Nombre;
            union2 = snipe;
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringTokenizer st = new StringTokenizer(snipe, "|");
         imageUrl = st.nextToken();
         placas = st.nextToken();
         idTaxi=st.nextToken();

        Nombre2=(TextView)findViewById(R.id.NombreTextView);

        bar=(RatingBar)findViewById(R.id.ratingBarTaxis) ;
        foto=(ImageView) findViewById(R.id.PerfilimageView);
        placasText=(TextView)findViewById(R.id.PlacastextView);
        mandar=(Button)findViewById(R.id.MandarSolicitudBtn);
        comentariosTxt=(TextView)findViewById(R.id.comentariostextView);
        calificacion = (TextView)findViewById(R.id.textView13);

        Nombre2.setTypeface(fuente);
        placasText.setTypeface(fuente);
        mandar.setTypeface(fuente);
        comentariosTxt.setTypeface(fuente);
        calificacion.setTypeface(fuente);


        t1= new ViewTarget(R.id.NombreTextView, this);
        t2= new ViewTarget(R.id.PerfilimageView, this);
        t3= new ViewTarget(R.id.PlacastextView, this);
        t4= new ViewTarget(R.id.scrollView2, this);
        t5= new ViewTarget(R.id.textView13, this);
        t6= new ViewTarget(R.id.ratingBarTaxis, this);
        t7= new ViewTarget(R.id.MandarSolicitudBtn, this);
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
            contador=7;
            showcaseView.hide();

        }
        /////////////////////////Fin_______/////////////////////////////////////////////

        bar.setEnabled(false);

        Nombre2.setText(Nombre);
        placasText.setText("No. de placas\n"+placas);

        if(!imageUrl.equals("default")|| imageUrl!=null)
        {
            Picasso.with(DetalleTaxis.this).load(Uri.parse(imageUrl)).into(foto);
        }else{System.out.println("imageUrl="+imageUrl);}

    mandar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (validatorUtil.isOnline()) {
                Intent intent = new Intent(DetalleTaxis.this, Destino.class);
                intent.putExtra("idTaxi",idTaxi);
                intent.putExtra("idUsuario", idUsusario);
                intent.putExtra("nombre",union);
                intent.putExtra("foto",union2);
                finish();
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
            else
                Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
        }
    });


    }

    @Override
    protected void onStart() {
        super.onStart();
        obtenerPeticiones();
        ObtenerComentarios();
        bar.setEnabled(false);

        try {
                mAuth=FirebaseAuth.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("id del Taxi es: "+idTaxi);
    }

    public void obtenerPeticiones()
    {
        if (validatorUtil.isOnline()) {
            try {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("taxis").child(idTaxi);

                    listener1 = mDatabase.addValueEventListener(new ValueEventListener()
                    {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            Rating= dataSnapshot.child("rating").getValue().toString();
                            getRating();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
    }

    public void getRating()
    {
        try {
            double rating = Double.parseDouble(Rating);
            if(rating == 0)
            {
                calificacion.setText("Aún no tiene calificación");
                calificacion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                bar.setEnabled(false);
            }
            else
            {
                calificacion.setText(String.valueOf(rating));
                calificacion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
                bar.setRating((float)rating);
                bar.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public void ObtenerComentarios()
    {
        if (validatorUtil.isOnline()) {
            try {
                    mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                    mDatabase = mDatabase.child(idTaxi);

                    listener2 = mDatabase.child("comentarios").limitToLast(10).orderByKey().addValueEventListener(new ValueEventListener()
                    {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            CometarioUlt= dataSnapshot.getValue().toString();
                            System.out.println("All    "+CometarioUlt);

                            CometarioUlt=CometarioUlt.replace("{1=Primer comentario}","[No hay comentarios");
                            CometarioUlt=CometarioUlt.replace("{2=Primer comentario}","[ ");
                            CometarioUlt=CometarioUlt.replace("Primer comentario"," ");

                            System.out.println(CometarioUlt.length()+"Tamaño");
                            System.out.println(CometarioUlt+" Despues");
                            StringTokenizer token4 = new StringTokenizer(CometarioUlt, "[");
                            CometarioUlt=token4.nextToken();

                            CometarioUlt=CometarioUlt.replaceAll("","");
                            CometarioUlt=CometarioUlt.replaceAll("]","");
                            CometarioUlt= CometarioUlt.replaceAll("null","");
                            CometarioUlt= CometarioUlt.replaceAll("null,","");

                            StringTokenizer token = new StringTokenizer(CometarioUlt, ",");
                            int numTokens=token.countTokens();
                            System.out.println(numTokens+" Numero de tokens");
                            if(numTokens>10)
                            {
                                System.out.println("Entro al if");
                                int numComentarios=numTokens-10;
                                System.out.println(numComentarios+" comentarios de mas");
                                int cont=0;


                                System.out.println(ArraynumeroComentarios.size()+" Tamaño del arrayList");
                                for (int i=numComentarios;i<ArraynumeroComentarios.size();i++)
                                {
                                    CometarioUlt+=ArraynumeroComentarios.get(i);
                                    System.out.println(i+" 2222");
                                }
                            }
                            CometarioUlt=CometarioUlt.replaceAll("#","\n");
                            CometarioUlt=CometarioUlt.replaceAll(",","\n\n");
                            CometarioUlt=CometarioUlt.replaceAll(" \n\n","");

                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!\n"+CometarioUlt+" \nAquiiiiiiiiiiiiiiiii");
                            comentariosTxt.setText(CometarioUlt);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    System.out.println("Si entra}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (listener1 != null && listener2 != null) {
            mDatabase.removeEventListener(listener1);
            mDatabase.removeEventListener(listener2);
        }
        Intent i = new Intent(getApplicationContext(),MapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("latitud",latU);
        i.putExtra("longitud",lonU);
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
        if (listener1 != null && listener2 != null) {
            mDatabase.removeEventListener(listener1);
            mDatabase.removeEventListener(listener2);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener1 != null && listener2 != null) {
            mDatabase.removeEventListener(listener1);
            mDatabase.removeEventListener(listener2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener1 != null && listener2 != null) {
            mDatabase.removeEventListener(listener1);
            mDatabase.removeEventListener(listener2);
        }
    }



    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("DetalleTaxi", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("DetalleTaxi", true);
    }

    @Override
    public void onClick(View v) {
        switch (contador) {
            case 0:
                showcaseView.setShowcase(t1, true);
                showcaseView.setContentTitle("Nombre del conductor");
                showcaseView.setContentText("Aqui puedes vizualizar el nombre del taxista ");
                break;
            case 1:
                showcaseView.setShowcase(t2, true);
                showcaseView.setContentTitle("Foto de perfil");
                showcaseView.setContentText("Aqui puedes vizualizar la foto del taxista");
                break;
            case 2:
                showcaseView.setShowcase(t3, true);
                showcaseView.setContentTitle("Placas");
                showcaseView.setContentText("Aqui puedes vizualizar las placas del auto ");
                break;
            case 3:
                showcaseView.setShowcase(t4, true);
                showcaseView.setContentTitle("Comentarios");
                showcaseView.setContentText("Aqui puedes vizualizar los comentarios que tiene ");
                break;
            case 4:
                showcaseView.setShowcase(t5, true);
                showcaseView.setContentTitle("Calificacion general");
                showcaseView.setContentText("Aqui puedes vizualizar la calificacion exacta");
                break;
            case 5:
                showcaseView.setShowcase(t6, true);
                showcaseView.setContentTitle("Rating");
                showcaseView.setContentText("Aqui puedes vizualizar rating ");
                break;

            case 6:
                showcaseView.setShowcase(t7, true);
                showcaseView.setContentTitle("Solicitudes");
                showcaseView.setContentText("presione para enviar un peticion");
                showcaseView.setButtonText("Finalizar");
                break;

            case 7:
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
