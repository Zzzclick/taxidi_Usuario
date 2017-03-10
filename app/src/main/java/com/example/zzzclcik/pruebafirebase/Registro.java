package com.example.zzzclcik.pruebafirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText mPasswordField;
    private Button mRegistrerButton;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Boolean aux=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_registro);

        mAuth=FirebaseAuth.getInstance();
        mNameField=(EditText)findViewById(R.id.editTextName);
        mEmailField=(EditText)findViewById(R.id.editTextEmail);
        mPasswordField=(EditText)findViewById(R.id.editTextClave);
        mRegistrerButton=(Button)findViewById(R.id.buttonRegistrar);

        mProgress=new ProgressDialog(this);

mRegistrerButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (ConexionInternet())
        {
            startRegister();
        }
    }
});//OnClick BotonRegistrar

    }//On create

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in,R.anim.left_out);
        finish();
    }

    private void startRegister()
    {
        final String name=mNameField.getText().toString().trim();
        final String email=mEmailField.getText().toString().trim();
        String password=mPasswordField.getText().toString().trim();
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)) {
            char[] arrayChar = password.toCharArray();
            char[] arrayChar2 = email.toCharArray();

            if (arrayChar.length > 5) {

                for (int i = 0; i < arrayChar2.length; i++) {

                    if (arrayChar2[i] == '@'){Toast.makeText(Registro.this, "Correo valido", Toast.LENGTH_LONG).show();aux = true;}
                }
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
                                    DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
                                    DatabaseReference currentUserBD=mDatabase.child(user_id);
                                    currentUserBD.child("name").setValue(name);
                                    currentUserBD.child("email").setValue(email);
                                    currentUserBD.child("estado").setValue("0");
                                    currentUserBD.child("image").setValue("default");
                                    currentUserBD.child("peticion").setValue("nada");

                                    Intent i = new Intent(Registro.this, MainActivity.class);
                                    startActivity(i);finish();
                                } else {
                                    Toast.makeText(Registro.this, "Datos inválidos\nrevisa tus datos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {Toast.makeText(Registro.this,"Correo inválido",Toast.LENGTH_LONG).show();}
        }else {Toast.makeText(Registro.this,"La contraseña debe tener mínimo 6 dígitos",Toast.LENGTH_LONG).show();}
        }else{Toast.makeText(Registro.this,"Por favor introduce datos",Toast.LENGTH_SHORT).show();}
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
    }//

}
