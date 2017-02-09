package com.example.zzzclcik.pruebafirebase;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView mensajeTextView;
    private EditText mensajeEditText;
    private EditText textEmail;
    private EditText textPass;
    private TextView infoTextView;
    private Button btnRegister;

    private ProgressDialog progressDialog;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mensajeRef = ref.child("mensaje");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String TAG = "NOTICIAS";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTextView = (TextView) findViewById(R.id.infoTextView);
        mensajeTextView = (TextView) findViewById(R.id.textViewBase);
        mensajeEditText = (EditText) findViewById(R.id.editTextBase);
        progressDialog=new ProgressDialog(this);
        textEmail=(EditText)findViewById(R.id.editTextEmail);
        textPass=(EditText)findViewById(R.id.editTextClave);
        btnRegister=(Button)findViewById(R.id.buttonEntrar);
        mAuth=FirebaseAuth.getInstance();

        Button boton1 = (Button)findViewById(R.id.buttonBase);
        Button botonEnviar=(Button)findViewById(R.id.buttonEnviar);
        Button botonGps=(Button)findViewById(R.id.buttonGPS);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                infoTextView.append("\n" + key + ": " + value);
            }
        }

        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Token: " + token);

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
modificar();
            }
        });

        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Registro.class );
                startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             doLogin();
            }
        });
        botonGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class );
                startActivity(i);
            }
        });
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    Toast.makeText(MainActivity.this,"Ya estas logueado "+firebaseAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT);
                 //   mAuth.signOut();
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        mensajeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                mensajeTextView.setText("En la base esta"+value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void modificar() {
        String mensaje = mensajeEditText.getText().toString();
        mensajeRef.setValue(mensaje);
        mensajeEditText.setText("");
    }
    public void doLogin()
    {
        String email=textEmail.getText().toString().trim();
        String password=textPass.getText().toString().trim();
        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
        progressDialog.setMessage("Entrando,espere por favor");
        progressDialog.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this,"Logueo correcto",Toast.LENGTH_SHORT);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Logueo fallido",Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

}
