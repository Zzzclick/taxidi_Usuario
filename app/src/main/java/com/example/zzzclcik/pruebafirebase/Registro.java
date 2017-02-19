package com.example.zzzclcik.pruebafirebase;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
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
startRegister();
    }
});//OnClick BotonRegistrar

    }//On create
    private void startRegister()
    {
        final String name=mNameField.getText().toString().trim();
        String email=mEmailField.getText().toString().trim();
        String password=mPasswordField.getText().toString().trim();
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)) {
            char[] arrayChar = password.toCharArray();
            if (arrayChar.length > 6) {

                for (int i = 0; i < arrayChar.length; i++) {

                    if (arrayChar[i] == '@'){Toast.makeText(Registro.this, "Correo valido", Toast.LENGTH_LONG).show();aux = true;}
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
                                } else {
                                    Toast.makeText(Registro.this, "Datos invalidos\nrevisa tus datos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {Toast.makeText(Registro.this,"Correo invalido",Toast.LENGTH_LONG).show();}
        }else {Toast.makeText(Registro.this,"La contraseña debe tener minimo 6 digitos",Toast.LENGTH_LONG).show();}
        }else{Toast.makeText(Registro.this,"Por favor introduce datos",Toast.LENGTH_SHORT).show();}
    }
}
