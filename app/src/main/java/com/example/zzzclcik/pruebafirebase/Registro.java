package com.example.zzzclcik.pruebafirebase;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegistrerButton;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;


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



    }
}
