package com.example.zzzclcik.pruebafirebase;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.math.BigInteger;
import java.security.SecureRandom;



public class Usuario extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private Button btnLogOut;
    private ImageView imagePerfil,imagenTaxi;
    private TextView txtName,lblLongitud,lblLatitud;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int CAMERA_REGUEST_CODE=0;
    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private GoogleApiClient apiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_usuario);
        txtName= (TextView) findViewById(R.id.txtNombre);
        btnLogOut= (Button) findViewById(R.id.singOut);
        imagePerfil= (ImageView) findViewById(R.id.imageView);
        imagenTaxi=(ImageView)findViewById(R.id.imageGps);
       lblLongitud=(TextView)findViewById(R.id.idLongitud);
       lblLatitud=(TextView)findViewById(R.id.idLatitud);
        mAuth=FirebaseAuth.getInstance();


        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        imagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if(intent.resolveActivity(getPackageManager())!=null)
                {
                    startActivityForResult(Intent.createChooser(intent,"Seleciona una foto de perfil"),CAMERA_REGUEST_CODE);
                }
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(mAuth.getCurrentUser().getUid()!=null) { mAuth.signOut(); }
            }
        });
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    mStorage= FirebaseStorage.getInstance().getReference();
                    mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            txtName.setText(dataSnapshot.child("name").getValue().toString());
                            String imageUrl=dataSnapshot.child("image").getValue().toString();
                            if(!imageUrl.equals("default")|| TextUtils.isEmpty(imageUrl))
                            {
                                Picasso.with(Usuario.this).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).into(imagePerfil);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{ Intent i = new Intent(Usuario.this, MainActivity.class); startActivity(i); }
            }
        };


        imagenTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Usuario.this, MapsActivity.class );
                startActivity(i);
            }
        });
    }
    public String getRamdomString()
    {
        SecureRandom random =new SecureRandom();
        return new BigInteger(130,random).toString(32);
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        mDatabase=FirebaseDatabase.getInstance().getReference().child("users");
        final DatabaseReference currentUserBD=mDatabase.child(mAuth.getCurrentUser().getUid());
        currentUserBD.child("latitud").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                lblLatitud.setText("Latitud:"+value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        currentUserBD.child("longitud").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                lblLongitud.setText("Longitud:"+value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
        this.finish(); // Sale de la aplicación
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_REGUEST_CODE&&resultCode==RESULT_OK)
        {
            if(mAuth.getCurrentUser()==null)
            return;
            progressDialog.setMessage("Subiendo imagen");
            progressDialog.show();
            final Uri uri=data.getData();
            if(uri==null)
            {
                progressDialog.dismiss();
                return;
            }
            if(mAuth.getCurrentUser()==null)
                return;
            if(mStorage==null)
                mStorage=FirebaseStorage.getInstance().getReference();
            if(mDatabase==null)
                mDatabase=FirebaseDatabase.getInstance().getReference().child("users");
            final StorageReference filepath=mStorage.child("photo").child(getRamdomString());
            final DatabaseReference currentUserBD=mDatabase.child(mAuth.getCurrentUser().getUid());

            currentUserBD.child("latitud").setValue("");

            currentUserBD.child("longitud").addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {
                //aqui se modifica
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onCancelled(DatabaseError databaseError) { }

            });



            currentUserBD.child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                String image=dataSnapshot.getValue().toString();
                    if(!image.equals("default")&&!image.isEmpty())
                    {

                        com.google.android.gms.tasks.Task<Void> task=FirebaseStorage.getInstance().getReferenceFromUrl(image).delete();
                        task.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(Usuario.this, "Foto antigua eliminada satisfactoriamente", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(Usuario.this, "Hubo un error al eliminar la foto antigua", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                    currentUserBD.child("image").removeEventListener(this);
                    filepath.putFile(uri).addOnSuccessListener(Usuario.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Uri downloadUri =taskSnapshot.getDownloadUrl();
                            Toast.makeText(Usuario.this, "Finalizado", Toast.LENGTH_SHORT).show();
                            Picasso.with(Usuario.this).load(uri).fit().centerCrop().into(imagePerfil);
                            DatabaseReference currentUserDB=mDatabase.child(mAuth.getCurrentUser().getUid());
                            currentUserDB.child("image").setValue(downloadUri.toString());
                        }
                    }).addOnFailureListener(Usuario.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Usuario.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Toast.makeText(Usuario.this, "Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Toast.makeText(Usuario.this, "Error grave al conectar con Google Play Services", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(Location loc) {
        if (loc != null) {
           // lblLatitud.setText("Latitud: " + String.valueOf(loc.getLatitude()));
           // lblLongitud.setText("Longitud: " + String.valueOf(loc.getLongitude()));
            mAuth=FirebaseAuth.getInstance();
            String user_id = mAuth.getCurrentUser().getUid();
            DatabaseReference currentUserBD=mDatabase.child(user_id);
            currentUserBD.child("latitud").setValue(String.valueOf(loc.getLatitude()));
            currentUserBD.child("longitud").setValue(String.valueOf(loc.getLongitude()));

        } else {
            lblLatitud.setText("Latitud: (desconocida)");
            lblLongitud.setText("Longitud: (desconocida)");
        }
    }
}
