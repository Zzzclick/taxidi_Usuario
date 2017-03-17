package com.example.zzzclcik.pruebafirebase;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.StringTokenizer;

public class DetalleTaxis extends AppCompatActivity {
    private TextView Nombre2,Coordenadas2,Lat,Lon,placasText,comentariosTxt;
    private RatingBar bar;
    private ImageView foto;
    private Button mandar;
    String imageUrl;
    String placas ;
    String idTaxi;
    private FirebaseAuth mAuth;
    String peticion1,peticion2,peticion3,peticion4,peticion5;
    String nomU,latU,lonU;
    String nomP1,nomP2,nomP3,nomP4,nomP5;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    public String idUsusario,union,union2;
    public boolean auxBtn,auxEnvio=false;
    String Rating="0#0",CometarioUlt;
    int numComentario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        idUsusario=getIntent().getStringExtra("idUsuario");

        setContentView(R.layout.activity_detalle_taxis);
        String Nombre = getIntent().getStringExtra("nombre");
        String Coordenadas = getIntent().getStringExtra("posicion");
        String snipe = getIntent().getStringExtra("foto");

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

        Nombre2.setText(Nombre);
        placasText.setText("No. de placas\n"+placas);

        //bar.setNumStars(5);
        //bar.setRating(5);
        System.out.println(Nombre+"0000000000000000000000"+imageUrl+"\n000000000000"+placas);

        if(!imageUrl.equals("default")|| imageUrl!=null)
        {
            Picasso.with(DetalleTaxis.this).load(Uri.parse(imageUrl)).into(foto);
        }else{System.out.println("imageUrl="+imageUrl);}


mandar.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        obtenerPeticiones();
        if (ConexionInternet())
        {
            ChekarPeticiones();


        System.out.println("Usuario Peticion/////////////////////////////////////////////////////////////\n");
        System.out.println(nomP1);
        System.out.println(nomP2);
        System.out.println(nomP3);
        System.out.println(nomP4);
        System.out.println(nomP5);
        System.out.println("AQUI ACABA /////////////////////////////////////////////////////////////\n");

            String cadenaUsuario = nomU;
            if (cadenaUsuario.equals(nomP1) || cadenaUsuario.equals(nomP2) || cadenaUsuario.equals(nomP3) || cadenaUsuario.equals(nomP4) || cadenaUsuario.equals(nomP5))
            {
                Toast.makeText(getApplicationContext(), "Tu petición ya fue enviada anteriormente", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mandarSolicitud();
            }


    }

    }
});


    }

    @Override
    protected void onStart() {
        obtenerPeticiones();
        obtenerDatosUsuario();
        ObtenerComentarios();
        super.onStart(); mAuth=FirebaseAuth.getInstance();




        System.out.println("id del Taxi es: "+idTaxi);

        System.out.println("PETICION/////////////////////////////////////////////////////////////\n"+peticion1);
        System.out.println(peticion2);
        System.out.println(peticion3);
        System.out.println(peticion4);
        System.out.println(peticion5);
        System.out.println("AQUI ACABA /////////////////////////////////////////////////////////////\n");

        System.out.println("Usuario/////////////////////////////////////////////////////////////\n");
        System.out.println("ID:"+idUsusario);
        System.out.println(nomU);
        System.out.println(latU);
        System.out.println(lonU);
        System.out.println("AQUI ACABA /////////////////////////////////////////////////////////////\n");


    }
    public void ChekarPeticiones()
    {
        StringTokenizer st1 = new StringTokenizer(peticion1, "#");
        nomP1 = st1.nextToken();
        StringTokenizer st2 = new StringTokenizer(peticion2, "#");
        nomP2 = st2.nextToken();
        StringTokenizer st3 = new StringTokenizer(peticion3, "#");
        nomP3 = st3.nextToken();
        StringTokenizer st4 = new StringTokenizer(peticion4, "#");
        nomP4 = st4.nextToken();
        StringTokenizer st5 = new StringTokenizer(peticion5, "#");
        nomP5 = st5.nextToken();
    }

    public int TiempoEspera()
    {
        Random random = new Random();
        //int op = (int)Math.floor(Math.random()*10);}
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
    public void obtenerPeticiones()
    {
        mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");

        mDatabase.child(idTaxi).addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                peticion1= dataSnapshot.child("peticion1").getValue().toString();
                peticion2= dataSnapshot.child("peticion2").getValue().toString();
                peticion3= dataSnapshot.child("peticion3").getValue().toString();
                peticion4= dataSnapshot.child("peticion4").getValue().toString();
                peticion5= dataSnapshot.child("peticion5").getValue().toString();
                Rating= dataSnapshot.child("rating").getValue().toString();
                System.out.println(Rating+"#############################");
                getRating();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

      /* try {
        Thread.sleep(TiempoEspera());
    } catch (InterruptedException e) {
        e.printStackTrace();
    }*////

    }
    public void obtenerPeticiones2()
    {
        mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");

        mDatabase.child(idTaxi).addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                peticion1= dataSnapshot.child("peticion1").getValue().toString();
                peticion2= dataSnapshot.child("peticion2").getValue().toString();
                peticion3= dataSnapshot.child("peticion3").getValue().toString();
                peticion4= dataSnapshot.child("peticion4").getValue().toString();
                peticion5= dataSnapshot.child("peticion5").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }



    public void obtenerDatosUsuario()
    {
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
    }




   public void  mandarSolicitud()
    {
union=nomU+"#"+latU+"#"+lonU+"#"+idUsusario;
        boolean aux=false;

        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
        DatabaseReference currentUserBD2=mDatabase.child(idTaxi);
        DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference currentUserBD22=mDatabase2.child(idUsusario);

        System.out.println("Aqui______________");
        System.out.println("-----------------------"+peticion1+" "+peticion2+" "+peticion3+" "+peticion4+" "+peticion5);

        obtenerPeticiones();
        if(peticion1.equals("123"))
        {

            union2=union+"#1";
            currentUserBD2.child("peticion1").setValue(union+"#1");auxEnvio=true;
            currentUserBD22.child("estado").setValue("1");auxEnvio=true;
            System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
            System.out.println("-----------------------"+peticion1+" "+peticion2+" "+peticion3+" "+peticion4+" "+peticion5);
            Toast.makeText(getApplicationContext(),"Petición  enviada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetalleTaxis.this,EsperaServicio.class);
            intent.putExtra("idTaxi",idTaxi);
            intent.putExtra("union",union2);
            System.out.println("222222222UNION   "+union2);
            startActivity(intent);


        }else
        {obtenerPeticiones2();

            if(peticion2.equals("123"))
            {
                obtenerPeticiones();
                union2=union+"#2";
               currentUserBD2.child("peticion2").setValue(union+"#2");auxEnvio=true;
                currentUserBD22.child("estado").setValue("1");auxEnvio=true;
                Toast.makeText(getApplicationContext(),"Petición  enviada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetalleTaxis.this,EsperaServicio.class);
                intent.putExtra("idTaxi",idTaxi);
                intent.putExtra("union",union2);
                System.out.println("222222222UNION   "+union2);
                startActivity(intent);
            }else
            {
                obtenerPeticiones();
                if(peticion3.equals("123"))
                {                    union2=union+"#3";
                    currentUserBD2.child("peticion3").setValue(union+"#3");auxEnvio=true;
                    currentUserBD22.child("estado").setValue("1");auxEnvio=true;
                    Toast.makeText(getApplicationContext(),"Petición  enviada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetalleTaxis.this,EsperaServicio.class);
                    intent.putExtra("idTaxi",idTaxi);
                    intent.putExtra("union",union2);
                    System.out.println("222222222UNION   "+union2);
                    startActivity(intent);
                }else
                {
                    obtenerPeticiones();
                    if(peticion4.equals("123"))
                    {

                        union2=union+"#4";
                        currentUserBD2.child("peticion4").setValue(union+"#4");auxEnvio=true;
                        currentUserBD22.child("estado").setValue("1");auxEnvio=true;
                        Toast.makeText(getApplicationContext(),"Petición  enviada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DetalleTaxis.this,EsperaServicio.class);
                        intent.putExtra("idTaxi",idTaxi);
                        intent.putExtra("union",union2);
                        System.out.println("222222222UNION   "+union2);
                        startActivity(intent);
                    }
                    else
                    {
                        obtenerPeticiones();
                        if(peticion5.equals("123"))
                        { union2=union+"#5";
                            currentUserBD2.child("peticion5").setValue(union+"#5");auxEnvio=true;
                            currentUserBD22.child("estado").setValue("1");auxEnvio=true;
                            Toast.makeText(getApplicationContext(),"Petición  enviada", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DetalleTaxis.this,EsperaServicio.class);
                            intent.putExtra("idTaxi",idTaxi);
                            intent.putExtra("union",union2);
                            System.out.println("222222222UNION   "+union2);
                            startActivity(intent);
                        }else
                        {
                            Toast.makeText(DetalleTaxis.this, "Este taxi tiene muchas peticiones", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }



    }

    public boolean ConexionInternet()
    {
        ConnectivityManager conect =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if ((conect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED) ||
                (conect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING) ||
                (conect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) ||
                (conect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING))
        {
            return true;
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Conexión a Internet");
            builder.setMessage("No estás conectado a Internet");
            builder.setPositiveButton("Aceptar",null);
            builder.show();
            return false;
        }
    }
    public void getRating()
    {
        StringTokenizer token = new StringTokenizer(Rating, "#");
        System.out.println(Rating+"$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        float rat = Float.parseFloat(token.nextToken());
        int num = Integer.parseInt(token.nextToken());
        float prom = rat/num;
        bar.setRating(prom);
        bar.setEnabled(false);
        System.out.println("Rating actual " + prom + " #" + num);
    }
    public void ObtenerComentarios()
    {
        mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
        mDatabase=mDatabase.child(idTaxi);

        mDatabase.child("comentarios").limitToLast(10).addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                CometarioUlt= dataSnapshot.getValue().toString();
                System.out.println("All    "+CometarioUlt);

                CometarioUlt=CometarioUlt.replace("{2=Primer comentario}","");

                System.out.println(CometarioUlt.length());
                StringTokenizer token4 = new StringTokenizer(CometarioUlt, "[");
                CometarioUlt=token4.nextToken();

//CometarioUlt=CometarioUlt.substring(1,comentariosTxt.length()+1);
                CometarioUlt=CometarioUlt.replaceAll("","");
                CometarioUlt=CometarioUlt.replaceAll("]","");
                CometarioUlt= CometarioUlt.replaceAll("null","");
                CometarioUlt=CometarioUlt.replaceAll("#","\n");
                CometarioUlt=CometarioUlt.replaceAll(",","\n\n");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!\n"+CometarioUlt+" \nAquiiiiiiiiiiiiiiiii");
                StringTokenizer token = new StringTokenizer(CometarioUlt, ",");
                int numTokens=token.countTokens();
                /*
                String comemtario1,comemtario2,comemtario3,comemtario4,comemtario5,comemtario6,comemtario7,comemtario8,comemtario9,comemtario10;
                switch (numTokens) {
                    case 1:
                        comemtario1=token.nextToken();
                        System.out.println(comemtario1);
                          break;
                    case 2:
                        comemtario1=token.nextToken();

                        comemtario2=token.nextToken();

                        System.out.println(comemtario1+"\n "+comemtario2);
                        break;
                    case 3:
                        comemtario1=token.nextToken();

                        comemtario2=token.nextToken();
                        comemtario3=token.nextToken();

                        System.out.println(comemtario1+"\n "+comemtario2+"\n "+comemtario3);
                        break;
                    case 4:
                        comemtario1=token.nextToken();

                        comemtario2=token.nextToken();
                        comemtario3=token.nextToken();
                        comemtario4=token.nextToken();

                        System.out.println(comemtario1+"\n "+comemtario2+"\n "+comemtario3+"\n "+comemtario4);
                        break;
                    case 5:
                        comemtario1=token.nextToken();

                        comemtario2=token.nextToken();
                        comemtario3=token.nextToken();
                        comemtario4=token.nextToken();
                        comemtario5=token.nextToken();

                        System.out.println(comemtario1+"\n "+comemtario2+"\n "+comemtario3+"\n "+comemtario4+"\n "+comemtario5+"  5");
                        break;
                    case 6:
                        comemtario1=token.nextToken();

                        comemtario2=token.nextToken();
                        comemtario3=token.nextToken();
                        comemtario4=token.nextToken();
                        comemtario5=token.nextToken();
                        comemtario6=token.nextToken();

                        System.out.println(comemtario1+"\n "+comemtario2+"\n "+comemtario3+"\n "+comemtario4+"\n "+comemtario5+"\n "
                                +comemtario6+"  6");
                        break;
                    case 7:
                        comemtario1=token.nextToken();

                        comemtario2=token.nextToken();
                        comemtario3=token.nextToken();
                        comemtario4=token.nextToken();
                        comemtario5=token.nextToken();
                        comemtario6=token.nextToken();
                        comemtario7=token.nextToken();

                        System.out.println(comemtario1+"\n "+comemtario2+"\n "+comemtario3+"\n "+comemtario4+"\n "+comemtario5+"\n "
                                +comemtario6+"\n "+comemtario7+"  7");
                        break;
                    case 8:
                        comemtario1=token.nextToken();

                        comemtario2=token.nextToken();
                        comemtario3=token.nextToken();
                        comemtario4=token.nextToken();
                        comemtario5=token.nextToken();
                        comemtario6=token.nextToken();
                        comemtario7=token.nextToken();
                        comemtario8=token.nextToken();

                        System.out.println(comemtario1+"\n "+comemtario2+"\n "+comemtario3+"\n "+comemtario4+"\n "+comemtario5+"\n "
                                +comemtario6+"\n "+comemtario7+"\n "+comemtario8);
                        break;
                    case 9:
                        comemtario1=token.nextToken();

                        comemtario2=token.nextToken();
                        comemtario3=token.nextToken();
                        comemtario4=token.nextToken();
                        comemtario5=token.nextToken();
                        comemtario6=token.nextToken();
                        comemtario7=token.nextToken();
                        comemtario8=token.nextToken();
                        comemtario9=token.nextToken();

                        System.out.println(comemtario1+"\n "+comemtario2+"\n "+comemtario3+"\n "+comemtario4+"\n "+comemtario5+"\n "
                                +comemtario6+"\n "+comemtario7+"\n "+comemtario8+"\n "+comemtario9+"\n "+"\n "
                        );
                        break;
                    case 10:
                        comemtario1=token.nextToken();


                        comemtario2=token.nextToken();
                        comemtario3=token.nextToken();
                        comemtario4=token.nextToken();
                        comemtario5=token.nextToken();
                        comemtario6=token.nextToken();
                        comemtario7=token.nextToken();
                        comemtario8=token.nextToken();
                        comemtario9=token.nextToken();
                        comemtario10=token.nextToken();


                        System.out.println(comemtario1+"\n "+comemtario2+"\n "+comemtario3+"\n "+comemtario4+"\n "+comemtario5+"\n "
                                +comemtario6+"\n "+comemtario7+"\n "+comemtario8+"\n "+comemtario9+"\n "+comemtario10+"\n "
                        );
                        break;
                    default:

                        break;

                }*/



                if(CometarioUlt!=null)
                {
                    final SpannableStringBuilder texto= new SpannableStringBuilder("Osiel Rios Escorcia\ntaxi muy sucio");
                    final SpannableStringBuilder texto2= new SpannableStringBuilder("Osiel Rios Escorcia\ntaxi muy sucio");
                    StringTokenizer token2 = new StringTokenizer(texto.toString(), "\n");//aqui hacemos el token hasta \n para extraer el nombre
                    StringTokenizer token3 = new StringTokenizer(texto2.toString(), "\n");//aqui hacemos el token hasta \n para extraer el nombre
                    String comentTam=token2.nextToken();//extraigo el nombre de usuario para convertirlo a String

                    final StyleSpan letraEnNegrita= new StyleSpan(android.graphics.Typeface.BOLD); // Para hacer negrita
                    texto.setSpan(letraEnNegrita, 0, comentTam.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);// Convierte los primeros caracteres en negrita pero tomando el tamaño del sTRING nombreUsuario, tu puedes decirle cuantos caracteres :)
                    texto2.setSpan(letraEnNegrita, 0, comentTam.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);// Convierte los primeros caracteres en negrita pero tomando el tamaño del sTRING nombreUsuario, tu puedes decirle cuantos caracteres :)
                    comentariosTxt.setText(CometarioUlt);//mandamos el texto al tEXTvIEW

                }else{Toast.makeText(DetalleTaxis.this,"Verifique su conexion",Toast.LENGTH_SHORT).show();}

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        System.out.println("Si entra}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");

    }
}
