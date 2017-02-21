package com.example.zzzclcik.pruebafirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

public class DetalleTaxis extends AppCompatActivity {
private TextView Nombre2,Coordenadas2,Lat,Lon;
    private RatingBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_taxis);
        String Nombre = getIntent().getStringExtra("nombre");
        String Coordenadas = getIntent().getStringExtra("posicion");
        Nombre2=(TextView)findViewById(R.id.NombreTextView);
        Coordenadas2=(TextView)findViewById(R.id.PosiciontextView);
        Lat=(TextView)findViewById(R.id.latTextView);
        Lon=(TextView)findViewById(R.id.lonTextView);
        bar=(RatingBar)findViewById(R.id.ratingBarTaxis) ;

        Nombre2.setText(Nombre);
        Coordenadas2.setText(Coordenadas);
        bar.setNumStars(6);
    }
}
