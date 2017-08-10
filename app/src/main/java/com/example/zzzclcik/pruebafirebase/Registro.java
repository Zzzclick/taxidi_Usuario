package com.example.zzzclcik.pruebafirebase;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Registro extends AppCompatActivity {
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField, mPasswordField2;
    private Button mRegistrerButton;
    private TextView txtTitulo;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Boolean aux=false;
    ValidatorUtil validatorUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setTitle("Regístrate");
        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        mAuth=FirebaseAuth.getInstance();
        mNameField=(EditText)findViewById(R.id.editTextName);
        mEmailField=(EditText)findViewById(R.id.editTextEmail);
        mPasswordField=(EditText)findViewById(R.id.editTextClave);
        mRegistrerButton=(Button)findViewById(R.id.buttonRegistrar);
        mPasswordField2 = (EditText) findViewById(R.id.editTextClave2);
        txtTitulo = (TextView) findViewById(R.id.textView);
        mProgress=new ProgressDialog(this);

        mNameField.setTypeface(fuente);
        mEmailField.setTypeface(fuente);
        mPasswordField.setTypeface(fuente);
        mRegistrerButton.setTypeface(fuente);
        mPasswordField2.setTypeface(fuente);
        txtTitulo.setTypeface(fuente);

    mRegistrerButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (validatorUtil.isOnline()) {
                startRegister();
            }
            else
                Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
        }});//OnClick BotonRegistrar

    }//On create

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
    }

    private void startRegister()
    {
        try {
                final String name=mNameField.getText().toString().trim();
                final String email=mEmailField.getText().toString().trim();
                String password=mPasswordField.getText().toString().trim();
                String password2=mPasswordField2.getText().toString().trim();
                if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(password2)) {
                    char[] arrayChar = password.toCharArray();

                    if (arrayChar.length > 5) {
                        aux = ValidatorUtil.validateEmail(email);
                        if (mPasswordField.getText().toString().trim().equals(mPasswordField2.getText().toString().trim())) {
                            if (aux) {
                                mProgress.setMessage("Registrando, por favor espere");
                                mProgress.show();
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                mProgress.dismiss();
                                                if (task.isSuccessful()) {
                                                    String user_id = mAuth.getCurrentUser().getUid();
                                                    Toast.makeText(Registro.this, user_id, Toast.LENGTH_SHORT).show();
                                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                                    DatabaseReference currentUserBD = mDatabase.child(user_id);
                                                    currentUserBD.child("name").setValue(name);
                                                    currentUserBD.child("email").setValue(email);
                                                    currentUserBD.child("estado").setValue("0");
                                                    currentUserBD.child("image").setValue("default");
                                                    currentUserBD.child("ViajeA").setValue("0#vacio");
                                                    //currentUserBD.child("telefono").setValue("");
                                                    DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("tipo");
                                                    mDatabase2.child("tipo").setValue("usuario");

                                                    Intent i = new Intent(Registro.this, MainActivity.class);
                                                    finish();
                                                    startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                } else {
                                                    Toast.makeText(Registro.this, "Datos inválidos\nrevisa tus datos", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {mEmailField.setError("Correo inválido");}
                        } else {mPasswordField2.setError("Contraseñas distintas");}
                    } else {mPasswordField.setError("La contraseña debe tener mínimo 6 dígitos");}
                }else{Toast toast1 = Toast.makeText(Registro.this, "Por favor introduce datos", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();}
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }
}
