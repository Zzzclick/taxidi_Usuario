package com.example.zzzclcik.pruebafirebase;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.StringTokenizer;

public class DetalleTaxis extends AppCompatActivity {
    private TextView Nombre2,Coordenadas2,Lat,Lon;
    private RatingBar bar;
    private ImageView foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle_taxis);
        String Nombre = getIntent().getStringExtra("nombre");
        String Coordenadas = getIntent().getStringExtra("posicion");
        String imageUrl = getIntent().getStringExtra("foto");
        Nombre2=(TextView)findViewById(R.id.NombreTextView);
        Coordenadas2=(TextView)findViewById(R.id.PosiciontextView);
        Lat=(TextView)findViewById(R.id.latTextView);
        Lon=(TextView)findViewById(R.id.lonTextView);
        bar=(RatingBar)findViewById(R.id.ratingBarTaxis) ;
        foto=(ImageView) findViewById(R.id.PerfilimageView);


        Nombre2.setText(Nombre);

        bar.setNumStars(5);
        bar.setRating(5);
        System.out.println(Nombre+"0000000000000000000000"+imageUrl);

        if(!imageUrl.equals("default")|| imageUrl!=null)
        {
            Picasso.with(DetalleTaxis.this).load(Uri.parse(imageUrl)).into(foto);
        }else{System.out.println("imageUrl="+imageUrl);}


        String s = "having Community Portal|Help Desk|Local Embassy|Reference Desk|Site News";
        StringTokenizer st = new StringTokenizer(s, "|");
        String community = st.nextToken();
        String helpDesk = st.nextToken();
        String localEmbassy = st.nextToken();
        String referenceDesk = st.nextToken();
        String siteNews = st.nextToken();
        System.out.println("aquiiiiiiiiiiiiiiiiiii"+community+" "+helpDesk+" "+localEmbassy+" "+referenceDesk+" "+siteNews);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
