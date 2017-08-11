package com.example.zzzclcik.pruebafirebase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class EsperaTaxis extends AppCompatActivity {

    public Button aceptarUno,aceptarDos,aceptarTres,aceptarCuatro,aceptarCinco;
    public Button rechazarUno,rechazarDos,rechazarTres,rechazarCuatro,rechazarCinco;
    public TextView mens1,mens2,mens3,mens4,mens5,sinPet;
    String id1,id2,id3,id4,id5;
    String auxB1,auxB2,auxB3,auxB4,auxB5,auxB6,ignorados="";
    String MiId,contenidoPeticiones,taxis;
    MediaPlayer sonido;
    private FirebaseAuth mAuth;
    ValidatorUtil validatorUtil = null;
    private DatabaseReference mDatabase,mDatabaseT,mDatabase2,mDatabaseT2;
    ValueEventListener listenerTaxi, listener2,listenertaxi2,listenerCoordenadas;
    LocationManager locationManager;
    int contadorBotones = 0;
    String comentariosT,nombreT,raiting,placas,imagen,CometarioUlt,idTaxiA;
    TextView nombreTa,placasTa,comentarioTa,raitingTa;
    ImageView fotoTa;
    RatingBar raitingTa2;
    Button enviarTa;
     AlertDialog dialog2;
    private static long back_pressed;
    boolean taxiDisponible=true;

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    ////////////////ARRAYS_inicio//////////////////////////////////
    ArrayList<String> idArray=new ArrayList<>();
    ArrayList<String> nombreArray=new ArrayList<>();
    ArrayList<String> taxisArray=new ArrayList<>();
    ArrayList<String> idBorrarArray=new ArrayList<>();


    ////////////////ARRAYS_fin//////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in, R.anim.right_in);
        setContentView(R.layout.activity_espera_taxis);
        validatorUtil = new ValidatorUtil(getApplicationContext());
        sonido = MediaPlayer.create(this, R.raw.tono);
        mAuth= FirebaseAuth.getInstance();
        MiId = mAuth.getCurrentUser().getUid();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EsperaTaxis.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_detalles_taxi, null);
        getSupportActionBar().setTitle("Bandeja de peticiones");
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        nombreTa = (TextView) mView.findViewById(R.id.nombreDet);
        placasTa = (TextView) mView.findViewById(R.id.placasDet);
        comentarioTa = (TextView) mView.findViewById(R.id.comentariosDet);
        raitingTa = (TextView) mView.findViewById(R.id.ratingDet);
        fotoTa = (ImageView) mView.findViewById(R.id.fotoDet);
        enviarTa = (Button) mView.findViewById(R.id.enviarDet);
        raitingTa2 = (RatingBar) mView.findViewById(R.id.ratingDet2);

        raitingTa2.setEnabled(false);
        mBuilder.setView(mView);
        dialog2 = mBuilder.create();


        aceptarUno=(Button)findViewById(R.id.buttonUnoAceptar);
        aceptarDos=(Button)findViewById(R.id.buttonDosAceptar);
        aceptarTres=(Button)findViewById(R.id.buttonTresAceptar);
        aceptarCuatro=(Button)findViewById(R.id.buttonCuatroAceptar);
        aceptarCinco=(Button)findViewById(R.id.buttonCincoAceptar);
        ////////////////////////////////////////////////////////
        rechazarUno=(Button)findViewById(R.id.buttonUnoRechazar);
        rechazarDos=(Button)findViewById(R.id.buttonDosRechazar);
        rechazarTres=(Button)findViewById(R.id.buttonTresRechazar);
        rechazarCuatro=(Button)findViewById(R.id.buttonCuatroRechazar);
        rechazarCinco=(Button)findViewById(R.id.buttonCincoRechazar);

//////////////////////////////////////////////////////////////////////////////////7
        mens1=(TextView)findViewById(R.id.textViewUno);
        mens2=(TextView)findViewById(R.id.textViewDos);
        mens3=(TextView)findViewById(R.id.textViewTres);
        mens4=(TextView)findViewById(R.id.textViewCuatro);
        mens5=(TextView)findViewById(R.id.textViewCinco);
        sinPet=(TextView)findViewById(R.id.SinPeticiones_textView);



        mens1.setMovementMethod(new ScrollingMovementMethod());
        mens2.setMovementMethod(new ScrollingMovementMethod());
        mens3.setMovementMethod(new ScrollingMovementMethod());
        mens4.setMovementMethod(new ScrollingMovementMethod());
        mens5.setMovementMethod(new ScrollingMovementMethod());
////////////////////////////Para hacer invisible los componentes_____INICIO/////////////////////////////////////////////////////////////////////        mens1.setVisibility(View.INVISIBLE);
////////////////////////Para configurar el tipo de letra____INICIO///////////////////////////////////////////////////////////////////
        mens1.setTypeface(fuente);
        mens2.setTypeface(fuente);
        mens3.setTypeface(fuente);
        mens4.setTypeface(fuente);
        mens5.setTypeface(fuente);

        aceptarUno.setTypeface(fuente);
        aceptarDos.setTypeface(fuente);
        aceptarTres.setTypeface(fuente);
        aceptarCuatro.setTypeface(fuente);
        aceptarCinco.setTypeface(fuente);

        rechazarUno.setTypeface(fuente);
        rechazarDos.setTypeface(fuente);
        rechazarTres.setTypeface(fuente);
        rechazarCuatro.setTypeface(fuente);
        rechazarCinco.setTypeface(fuente);
////////////////////////Para configurar el tipo de letra___FIN///////////////////////////////////////////////////////////////////

        HacerInvisible();
        sinPet.setVisibility(View.VISIBLE);

////////////////////////////////////////////////Paa los onclicks de Rechazar____INICIO/////////////////////////////////////////////////////////
        rechazarUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB1=BorrarPeticion(id1);
                    Toast.makeText(getApplicationContext(),auxB1, Toast.LENGTH_SHORT).show();
                }
                else
                    {
                        Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                    }

            }
        });
        rechazarDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB2=BorrarPeticion(id2);
                    Toast.makeText(getApplicationContext(),auxB2, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        rechazarTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB3=BorrarPeticion(id3);
                    Toast.makeText(getApplicationContext(),auxB3, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        rechazarCuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB4=BorrarPeticion(id4);
                    Toast.makeText(getApplicationContext(),auxB4, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        rechazarCinco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB5=BorrarPeticion(id5);
                    Toast.makeText(getApplicationContext(),auxB5, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
////////////////////////////////////////////////Paa los onclicks de Rechazar_____FIN/////////////////////////////////////////////////////////



///////////////////////////////////////////////Para los Onclicks para Aceptar__INICIO/////////////////////////////////////////////////////////
        aceptarUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    if (id1!=null|| !id1.isEmpty())
                    {
                        AceptarPeticion(id1);
                    }else{  Toast.makeText(getApplicationContext(),"error en id 1 : "+id1,Toast.LENGTH_LONG).show();}
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        aceptarDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    if (id2!=null|| !id2.isEmpty())
                    {
                        AceptarPeticion(id2);
                    }else{  Toast.makeText(getApplicationContext(),"error en id 2 : "+id2,Toast.LENGTH_LONG).show();}
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        aceptarTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    if (id3!=null|| !id3.isEmpty())
                    {
                        AceptarPeticion(id3);
                    }else{  Toast.makeText(getApplicationContext(),"error en id 3 : "+id3,Toast.LENGTH_LONG).show();}
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        aceptarCuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    if (id4!=null|| !id4.isEmpty())
                    {
                        AceptarPeticion(id4);
                    }else{  Toast.makeText(getApplicationContext(),"error en id 2 : "+id4,Toast.LENGTH_LONG).show();}
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        aceptarCinco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    if (id5!=null|| !id5.isEmpty())
                    {
                        AceptarPeticion(id5);
                    }else{  Toast.makeText(getApplicationContext(),"error en id 5 : "+id5,Toast.LENGTH_LONG).show();}
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });

///////////////////////////////////////////////Para los Onclicks para Aceptar____FIN/////////////////////////////////////////////////////////
       enviarTa.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (validatorUtil.isOnline())
               {
                   if (MiId!=null||idTaxiA!=null) {
                       Toast.makeText(getApplicationContext(),"Iniciando viaje", Toast.LENGTH_SHORT).show();
                       IniciarViaje();
                   }else{
                       System.out.println("QQQ Errror en ids  Mio "+MiId+"   Taxi"+idTaxiA);
                   }
               }
               else
               {
                   Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
               }

           }
       });
        ObtenerPeticiones();
        Validartaxi();
    }//ON CREATE

    @Override
    protected void onStart() {

        super.onStart();
        Validartaxi();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Aviso");
        dialogo1.setMessage("Si sale de ya no le van a llegar las peticiones y el viaje ya no lo va poder realizar");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Estoy de acuerdo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id)
            {
                Salir();
            }
        });
        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();




    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {

        try {
            mDatabase.removeEventListener(listener2);
            mDatabaseT.removeEventListener(listenerTaxi);
            mDatabaseT2.removeEventListener(listenertaxi2);
            mDatabase2.removeEventListener(listenerCoordenadas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        if(dialog2!=null)
        {
            dialog2.dismiss();
        }
            ///ELIMINAR LA PETICION DEL TODAS LAS PETICIONES Y DE TU APARTADO
        BorrarPeticion();
        BorrarPeticionesALL();

        super.onDestroy();
    }




    //////////////////////////////////////////////////////////______ObtenerPeticiones____________INICIO/////////////////////
    public void ObtenerPeticiones()
    {
            try {
                mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(MiId).child("peticiones");
                listener2 = new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HacerInvisible();
                        try {
                            contenidoPeticiones = dataSnapshot.getValue().toString();
                            contenidoPeticiones = contenidoPeticiones.replace("}", "");
                            contenidoPeticiones = contenidoPeticiones.replace("{", "");

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            sinPet.setVisibility(View.VISIBLE);
                        }
if(contenidoPeticiones==null){sinPet.setVisibility(View.VISIBLE);}

                        try {
                            if(contenidoPeticiones!=null||!contenidoPeticiones.equals("")){
                                                    System.out.println("ÑÑÑ" + contenidoPeticiones);
                                                    StringTokenizer token1 = new StringTokenizer(contenidoPeticiones, "=,");
                                                    HacerInvisible();
                                                    sinPet.setVisibility(View.INVISIBLE);
                                                    idArray.clear();
                                                    nombreArray.clear();
                                                    contenidoPeticiones = "";
                                                    contadorBotones = 0;
                                                    while (token1.hasMoreTokens()) {
                                                        idArray.add(token1.nextToken());
                                                        nombreArray.add(token1.nextToken());

                                                    }
                                                }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        System.out.println("TT1 "+idArray.size());
                        System.out.println("TT2 "+nombreArray.size());
                        for (int i = 0; i < idArray.size(); i++)
                        {
                            System.out.println("QQQ"+i+" __"+idArray.get(i));
                            System.out.println("QQQQQ"+i+" __"+nombreArray.get(i));
                        if (contadorBotones <= 5) {

                            switch (contadorBotones) {
                                case 0:
                                    PlayTono();
                                    sinPet.setVisibility(View.INVISIBLE);
                                    rechazarUno.setVisibility(View.VISIBLE);
                                    aceptarUno.setVisibility(View.VISIBLE);

                                    try {
                                        mens1.setVisibility(View.VISIBLE);
                                        mens1.setText("Nombre\n" + nombreArray.get(i));
                                        id1 = idArray.get(i);
                                        id1= id1.replaceAll(" ","");
                                        if(mens1==null||id1==null)
                                        {}
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }

                                    break;
                                case 1:
                                    PlayTono();
                                    sinPet.setVisibility(View.INVISIBLE);
                                    rechazarDos.setVisibility(View.VISIBLE);
                                    aceptarDos.setVisibility(View.VISIBLE);

                                    try {
                                        mens2.setVisibility(View.VISIBLE);
                                        mens2.setText("Nombre\n" + nombreArray.get(i));
                                        id2 = idArray.get(i);
                                        id2= id2.replaceAll(" ","");
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 2:
                                    PlayTono();
                                    sinPet.setVisibility(View.INVISIBLE);
                                    rechazarTres.setVisibility(View.VISIBLE);
                                    aceptarTres.setVisibility(View.VISIBLE);

                                    try {
                                        mens3.setVisibility(View.VISIBLE);
                                        mens3.setText("Nombre\n" + nombreArray.get(i));
                                        id3 = idArray.get(i);
                                        id3= id3.replaceAll(" ","");
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 3:
                                    PlayTono();
                                    sinPet.setVisibility(View.INVISIBLE);
                                    rechazarCuatro.setVisibility(View.VISIBLE);
                                    aceptarCuatro.setVisibility(View.VISIBLE);

                                    try {
                                        mens4.setVisibility(View.VISIBLE);
                                        mens4.setText("Nombre\n" + nombreArray.get(i));
                                        id4 = idArray.get(i);
                                        id4= id4.replaceAll(" ","");
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 4:
                                    PlayTono();
                                    sinPet.setVisibility(View.INVISIBLE);
                                    rechazarCinco.setVisibility(View.VISIBLE);
                                    aceptarCinco.setVisibility(View.VISIBLE);

                                    try {
                                        mens5.setVisibility(View.VISIBLE);
                                        mens5.setText("Nombre\n" + nombreArray.get(i));
                                        id5 = idArray.get(i);
                                        id5= id5.replaceAll(" ","");
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), "Hay más de cinco peticiones", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            contadorBotones++;
                        }
                    }




                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };mDatabase.addValueEventListener(listener2);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }

    }
/////////////////////////////////////////////////////////_ObtenerPeticiones____________FIN////////////////////////////




    ////////////////////////////////////////////////////TocarTono___INICIO///////////////////////////////////////////////
    public  void PlayTono()
    {


        sonido.start();
        //Toast.makeText(getApplicationContext(),"Duracion del audio="+sonido.getDuration(), Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long [] patron = {100,50,50,50};
        v.vibrate(patron,3);



        sinPet.setVisibility(View.INVISIBLE);
    }
////////////////////////////////////////////////////7TocarTono___FIN///////////////////////////////////////////////

    public void HacerInvisible()
    {
        mens1.setVisibility(View.INVISIBLE);
        mens2.setVisibility(View.INVISIBLE);
        mens3.setVisibility(View.INVISIBLE);
        mens4.setVisibility(View.INVISIBLE);
        mens5.setVisibility(View.INVISIBLE);

        aceptarUno.setVisibility(View.INVISIBLE);
        aceptarDos.setVisibility(View.INVISIBLE);
        aceptarTres.setVisibility(View.INVISIBLE);
        aceptarCuatro.setVisibility(View.INVISIBLE);
        aceptarCinco.setVisibility(View.INVISIBLE);

        rechazarUno.setVisibility(View.INVISIBLE);
        rechazarDos.setVisibility(View.INVISIBLE);
        rechazarTres.setVisibility(View.INVISIBLE);
        rechazarCuatro.setVisibility(View.INVISIBLE);
        rechazarCinco.setVisibility(View.INVISIBLE);


    }

    public String BorrarPeticion(String id)
    {
        String aux="false";
        String aux2="true";

        if(id!=null&&!id.equalsIgnoreCase("null")&&!id.equalsIgnoreCase(""))
        {
            id=id.replaceAll(" ","");
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(MiId).child("peticiones");
            System.out.println(id+" _______________________________________");
            DatabaseReference currentUserBD = mDatabase.child(id);
            currentUserBD.removeValue();
HacerInvisible();
            ObtenerPeticiones();
            return aux2;
        }else
        {
            Toast.makeText(getApplicationContext(),"Id Incorrecto Hay que revisarlo", Toast.LENGTH_SHORT).show();
            return aux;
        }
    }
    public void AceptarPeticion(String id)
    {
        if(id!=null&&!id.equals(""))
        {
            Toast.makeText(getApplicationContext(),id, Toast.LENGTH_SHORT).show();
            ObtenerTaxi(id);
        }else{Toast.makeText(getApplicationContext(),"id nulo", Toast.LENGTH_SHORT).show();}
    }

    public void ObtenerTaxi(String id)
    {    idTaxiA=id;
        id=id.replaceAll(" ","");
        final String auxId=id;

        System.out.println("vvv "+id);
        try {
            mDatabaseT2= FirebaseDatabase.getInstance().getReference().child("taxis").child(id);
            System.out.println("vvv "+mDatabaseT2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        listenertaxi2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try {
                    nombreT=dataSnapshot.child("name").getValue().toString();
                    System.out.println("SSS "+nombreT);
                    imagen=dataSnapshot.child("image").getValue().toString();
                    placas=dataSnapshot.child("placas").getValue().toString();
                    raiting=dataSnapshot.child("rating").getValue().toString();
                    ObtenerComentarios(auxId);
                    nombreTa.setText(nombreT);
                    if(!imagen.equals("default")|| TextUtils.isEmpty(imagen))
                    {
                    Picasso.with(EsperaTaxis.this).load(Uri.parse(imagen)).into(fotoTa);
                    }
                    placasTa.setText("Placas:"+placas);
                    if(raiting.equals("0")){raitingTa.setText("Aun no a sido calificado");raitingTa2.setRating(Float.parseFloat(raiting));}
                    else{raitingTa.setText(raiting);raitingTa2.setRating(Float.parseFloat(raiting));raitingTa.setTextSize(20);}


                    dialog2.show();
                } catch (NullPointerException e) { e.printStackTrace();  }
                catch (NumberFormatException e) { e.printStackTrace();  }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        try {
            mDatabaseT2.addValueEventListener(listenertaxi2);
        } catch (NullPointerException e) {e.printStackTrace();}
    }

    public void ObtenerComentarios(String id)
    {
        try {
            mDatabase2= FirebaseDatabase.getInstance().getReference().child("taxis").child(id).child("comentarios");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        listenerCoordenadas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try {
                    CometarioUlt= dataSnapshot.getValue().toString();
                    System.out.println("All    "+CometarioUlt);

                    CometarioUlt=CometarioUlt.replace("{1=Primer comentario}","[No hay comentarios");
                    CometarioUlt=CometarioUlt.replace("{2=Primer comentario}","[ ");
                    CometarioUlt=CometarioUlt.replace("Primer comentario"," ");


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

                    }
                    CometarioUlt=CometarioUlt.replaceAll("#","\n");
                    CometarioUlt=CometarioUlt.replaceAll(",","\n\n");
                    CometarioUlt=CometarioUlt.replaceAll(" \n\n","");

                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!\n"+CometarioUlt+" \nAquiiiiiiiiiiiiiiiii");
                    if(CometarioUlt==null||CometarioUlt.equals("")||CometarioUlt.isEmpty())
                    {
                        comentarioTa.setText("SIN COMENTARIOS");
                    }else
                        {
                            comentarioTa.setText(CometarioUlt);
                        }


                } catch (NullPointerException e) { e.printStackTrace();  }
                catch (NumberFormatException e) { e.printStackTrace();  }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        try {
            mDatabase2.limitToFirst(10).orderByKey().addValueEventListener(listenerCoordenadas);
        } catch (NullPointerException e) {e.printStackTrace();}
    }

    public void BorrarPeticion()
    {
        String id=MiId;
        if(id!=null&&!id.equalsIgnoreCase("null")&&!id.equalsIgnoreCase(""))
        {
            id=id.replaceAll(" ","");
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("peticiones");
            DatabaseReference currentUserBD = mDatabase.child(id);
            currentUserBD.removeValue();
        }else
        {
            Toast.makeText(getApplicationContext(),"Id Incorrecto Hay que revisarlo", Toast.LENGTH_SHORT).show();

        }
    }
    public void IniciarViaje()
    {
            if(dialog2!=null)
            {
                dialog2.dismiss();
            }
            DatabaseReference mDatabaseViaje = FirebaseDatabase.getInstance().getReference().child("users");
            DatabaseReference mDatabaseViaje2 = mDatabaseViaje.child(MiId);
            mDatabaseViaje2.child("ViajeA").setValue("1" + "#" + idTaxiA);
            DatabaseReference mDatabaseViaje3 = FirebaseDatabase.getInstance().getReference().child("taxis");
            DatabaseReference mDatabaseViaje4 = mDatabaseViaje3.child(idTaxiA);
            mDatabaseViaje4.child("ViajeA").setValue("1#$" + MiId);

            Intent intent = new Intent(EsperaTaxis.this, MapsActivityTaxi.class);
            intent.putExtra("idUsuario", MiId);
            intent.putExtra("idTaxi", idTaxiA);
            saveValuePreference2(getApplicationContext(),idTaxiA);
            System.out.println("Valor de id Config \n"+getValuePreference2(getApplicationContext()));

            if(dialog2!=null)
            {
                dialog2.dismiss();
            }
            BorrarPeticion();
            BorrarPeticionesALL();
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);

    }




    //////////////////////////////////////////////////////////______ObtenerPeticiones____________INICIO/////////////////////
    public void Validartaxi()
    {

            try {
                mDatabaseT= FirebaseDatabase.getInstance().getReference().child("taxis");

                listenerTaxi = new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        try
                        {
                            Iterator<DataSnapshot> items=dataSnapshot.getChildren().iterator();
                            idArray.clear();
                            taxisArray.clear();
                            idBorrarArray.clear();
                            int cont=0;
                            while (items.hasNext())
                            {
                                DataSnapshot item = items.next();
                                taxisArray.add(item.child("ViajeA").getValue().toString());
                                idBorrarArray.add(item.child("id").getValue().toString());
                                System.out.println(" en "+cont+" viaje "+taxisArray.get(cont)+" id "+idBorrarArray.get(cont));
                                cont++;
                                //  String taxis3 = dataSnapshot.child(idArray.get(i)).getValue().toString();
                            }

                            for (int i = 0; i < taxisArray.size(); i++)
                            {
                                try {
                                    if(!taxisArray.get(i).contains("0#vacio"))
                                    {
                                        System.out.println("en "+i+" taxi ocupado ");
                                        auxB6=BorrarPeticion(idBorrarArray.get(i));

                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }



                        } catch (NullPointerException e)
                        {
                            e.printStackTrace();

                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };mDatabaseT.addValueEventListener(listenerTaxi);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }

    }
/////////////////////////////////////////////////////////_ObtenerPeticiones____________FIN////////////////////////////
public String BorrarPeticionesALL()
{

    String aux2="true";

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(MiId).child("peticiones");
        DatabaseReference currentUserBD = mDatabase;
        currentUserBD.removeValue();
        return aux2;

}
public void Salir()
{
    try
    {

        if(dialog2!=null)
        {
            dialog2.dismiss();
        }


        BorrarPeticion();
        BorrarPeticionesALL();

        Intent intent = new Intent(getApplicationContext(),EnviarATodos.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        Toast.makeText(getBaseContext(), "Saliendo", Toast.LENGTH_SHORT).show();
        if(dialog2!=null)
        {
            dialog2.dismiss();
        }
        finish();
        startActivity(intent);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
System.exit(0);

    } catch (NullPointerException e) {
        e.printStackTrace();
    }
}
    private String PREFS_KEY = "mispreferencias";
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


}
