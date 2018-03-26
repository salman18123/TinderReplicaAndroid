package com.salman.tinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    EditText email,password,name;
    Button Register;
    RadioGroup radioGroup;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        Register=findViewById(R.id.Register);
        radioGroup=findViewById(R.id.radioGroup);
        name=findViewById(R.id.name);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent i=new Intent(RegistrationActivity.this,MainActivity.class);
                    startActivity(i);
                }
            }
        };

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectId=radioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton=findViewById(selectId);
                if(radioButton.getText()==null){
                    return;
                }
                String e=email.getText().toString();
                String p=password.getText().toString();
                final String n=name.getText().toString();
                firebaseAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "Sign Up Error", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId).child("name");
                           Map userInfo =new HashMap();
                           userInfo.put("name",n);
                           userInfo.put("profileUrl","default");

                            ref.updateChildren(userInfo);
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseListener);
    }
}
