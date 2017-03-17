package com.example.zzzclcik.pruebafirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

public class CalificarTaxi extends AppCompatActivity {
    public RatingBar bar;

    Button btnEnviar;
    TextView txvRating,prueba;
    EditText comen;
    float ratingTaxi = 0;
    int cont = 0,numComentario;
    String Rating = "0#0",Rating2;
    String idTaxi,idUsuario,nomUsuario;
    String CometarioUlt;
    private DatabaseReference mDatabase;
    ArrayList<String> idArray=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calificar_taxi);
        bar=(RatingBar)findViewById(R.id.ratingBarTaxis);
        btnEnviar=(Button)findViewById(R.id.Enviarbutton);
        comen=(EditText)findViewById(R.id.comentarioEditText);
        prueba=(TextView)findViewById(R.id.PruebaTextView7);

        //idTaxi=getIntent().getStringExtra("idTaxi");
        idTaxi=getIntent().getStringExtra("idTaxi");
        idUsuario=getIntent().getStringExtra("idUsuario");
        nomUsuario=getIntent().getStringExtra("nomUsuario");

        bar.setRating(5);
        bar.setNumStars(5);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MandarDatos();
                finish();
            }
        });
    }

    public void setRatingTaxi(float rating) {
        StringTokenizer token = new StringTokenizer(Rating, "#");
        System.out.println(Rating+"$$$$$$$$$");
        ratingTaxi = Float.parseFloat(token.nextToken());
        cont = Integer.parseInt(token.nextToken());
        ratingTaxi = ratingTaxi + rating;
        System.out.println(cont+"%%%%%%%%%%%%%%%%%%%%%%");
        cont++;
        System.out.println(cont+"&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        Rating2 = ratingTaxi + "#" + cont;
    }
    public void MandarDatos()
    {   comen.getText();
        setRatingTaxi(bar.getRating());
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
        DatabaseReference currentUserBD=mDatabase.child(idTaxi);
        currentUserBD.child("rating").setValue(Rating2);
        System.out.println("||||||||||||||||||| "+numComentario+"   aqui "+comen.getText().toString());
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

    }
    public void obtenerRating()
    {
        mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");

        mDatabase.child(idTaxi).addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Rating= dataSnapshot.child("rating").getValue().toString();
                System.out.println(Rating+"###################");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        final SpannableStringBuilder texto= new SpannableStringBuilder("Osiel Rios Escorcia\ntaxi muy sucio");
        StringTokenizer token = new StringTokenizer(texto.toString(), "\n");
        String nombreUsuario=token.nextToken();//extraigo el nombre de usuario para convertirlo a String


        final StyleSpan letraEnNegrita= new StyleSpan(android.graphics.Typeface.BOLD); // Para hacer negrita
        //lo convierto para indicar el numero de negritas
        texto.setSpan(letraEnNegrita, 0, nombreUsuario.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);// Convierte los primeros caracteres en negrita pero tomando el tamaño del arreglo de cahrs, tu puedes decirle cuantos caracteres :)

        prueba.setText(texto);//mandamos el texto al tEXTvIEW
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        obtenerRating();
        ObtenerComentarios();
    }

    public void ObtenerComentarios()
    {
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
}


