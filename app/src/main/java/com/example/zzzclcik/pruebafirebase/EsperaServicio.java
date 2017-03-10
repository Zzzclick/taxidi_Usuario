package com.example.zzzclcik.pruebafirebase;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.StringTokenizer;

public class EsperaServicio extends AppCompatActivity {
    public TextView txtTiempo;
    public boolean aux=false;
    public String nom1,nom2,nom3,nom4,nom5;
    public String idTaxi,union,peticion,escuchador;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_espera_servicio);
        txtTiempo = (TextView) findViewById(R.id.Tiempo);
idTaxi=getIntent().getStringExtra("idTaxi");
union=getIntent().getStringExtra("union");
        System.out.println("UNION"+union);
        Esperar();
        StringTokenizer st1 = new StringTokenizer(union, "#");
        nom1 = st1.nextToken();//nombre
        nom2 = st1.nextToken();//lat
        nom3 = st1.nextToken();//lon
        nom4 = st1.nextToken();//id de usuario
        nom5 = st1.nextToken();//num de peticion
        peticion="peticion"+nom5;
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth= FirebaseAuth.getInstance();


        mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.child(nom4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                escuchador=dataSnapshot.child("estado").getValue().toString();
                if(escuchador.equals("2"))
                {
                    Intent intent = new Intent(EsperaServicio.this,MapsActivityTaxi.class);
                    intent.putExtra("idTaxi",idTaxi);
                    //intent.putExtra("union",union2);
                    startActivity(intent);
                }
                if (escuchador.equals("0"))
                {
                    Toast.makeText(getApplicationContext(),"Petición rechazada", Toast.LENGTH_SHORT).show();
                    aux=true;
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Esperar()
    {
        new CountDownTimer(10000, 1000)
        {

            @Override
            public void onTick(long millisUntilFinished)
            {
                txtTiempo.setText("Esperando respuesta\n " + (millisUntilFinished/1000));

            }

            @Override
            public void onFinish()
            {
                txtTiempo.setText("La solicitud no fue respondida");aux=true;
                DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                DatabaseReference currentUserBD=mDatabase.child(idTaxi);
                currentUserBD.child(peticion).setValue("123");
            }
        }.start();
    }

    @Override
    public void onBackPressed()
    {
        if(aux)
        {
        super.onBackPressed();
        }
    }
}
