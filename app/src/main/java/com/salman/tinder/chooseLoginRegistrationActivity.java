package com.salman.tinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class chooseLoginRegistrationActivity extends AppCompatActivity {
    Button Login,Registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);
        Login=findViewById(R.id.Login);
        Registration=findViewById(R.id.Registration);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(chooseLoginRegistrationActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(chooseLoginRegistrationActivity.this,RegistrationActivity.class);
                startActivity(i);

            }
        });
    }
}
