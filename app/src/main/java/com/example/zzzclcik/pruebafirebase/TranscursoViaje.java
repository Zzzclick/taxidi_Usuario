package com.example.zzzclcik.pruebafirebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TranscursoViaje extends AppCompatActivity {
    String idTaxi,idUsuario,nomUsuario;
    String escuchadorUsu,escuchadorUsuP;
    String escuchadorTax,escuchadorTaxP1,escuchadorTaxP2,escuchadorTaxP3,escuchadorTaxP4,escuchadorTaxP5;
    int numP=0;
    ImageView cancelar,terminar;
    AlertDialog alert = null;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcurso_viaje);
        idTaxi=getIntent().getStringExtra("idTaxi");
        idUsuario=getIntent().getStringExtra("idUsuario");
        cancelar=(ImageView)findViewById(R.id.CanImageView);
        terminar=(ImageView)findViewById(R.id.TerImageView);


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertTerminarServicio();

            }
        });

        terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertTerminarServicio();
                Intent intent = new Intent(TranscursoViaje.this, CalificarTaxi.class );
                intent.putExtra("idTaxi",idTaxi);
                intent.putExtra("idUsuario",idUsuario);
                intent.putExtra("nomUsuario",nomUsuario);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        CargarPeticionesU();
        CargarPeticionesT();

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
    public void onBackPressed() {
        super.onBackPressed();
        if(alert != null)
        {
            alert.dismiss ();
        }
    }
    private void AlertTerminarServicio() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Esta seguro de Terminar el servicio de taxi")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        TerminarViaje();

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
    private  void TerminarViaje()
    {

        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
        DatabaseReference currentUserBD=mDatabase.child(idTaxi);
        currentUserBD.child("estado").setValue("0");
        currentUserBD.child("peticion1").setValue("123");
        currentUserBD.child("peticion2").setValue("123");
        currentUserBD.child("peticion3").setValue("123");
        currentUserBD.child("peticion4").setValue("123");
        currentUserBD.child("peticion5").setValue("123");
        DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference currentUserBD2=mDatabase2.child(idUsuario);
        currentUserBD2.child("estado").setValue("0");
        currentUserBD2.child("peticion").setValue("");
    }
    private void CargarPeticionesT() {

        mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
        mDatabase.child(idTaxi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                escuchadorTax=dataSnapshot.child("estado").getValue().toString();
                escuchadorTaxP1=dataSnapshot.child("peticion1").getValue().toString();
                escuchadorTaxP2=dataSnapshot.child("peticion2").getValue().toString();
                escuchadorTaxP3=dataSnapshot.child("peticion3").getValue().toString();
                escuchadorTaxP4=dataSnapshot.child("peticion4").getValue().toString();
                escuchadorTaxP5=dataSnapshot.child("peticion5").getValue().toString();
                //////////////////////////////////////////////
                if(!escuchadorTaxP1.equals("123"))
                {    numP+=1;                }
                if(!escuchadorTaxP2.equals("123"))
                {    numP+=2;                }
                if(!escuchadorTaxP3.equals("123"))
                {    numP+=3;                }
                if(!escuchadorTaxP4.equals("123"))
                {    numP+=4;                }
                if(!escuchadorTaxP5.equals("123"))
                {    numP+=5;                }
                /////////////////////////////////////////////

                //if(escuchadorTax.equals("0")) {  }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CargarPeticionesU() {
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                escuchadorUsu=dataSnapshot.child("estado").getValue().toString();
                escuchadorUsuP=dataSnapshot.child("peticion").getValue().toString();
                nomUsuario=dataSnapshot.child("name").getValue().toString();
                if(escuchadorUsu.equals("0"))
                {
                   // HacerAlert();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

private void HacerAlert()
{
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Importante");
    builder.setMessage("El conductor cancelo el servicio");
    builder.setPositiveButton("Aceptar",null);
    builder.show();
    Toast.makeText(TranscursoViaje.this, "El servicio se cancelo\nel conductor lo cancelo", Toast.LENGTH_LONG).show();
}

}
