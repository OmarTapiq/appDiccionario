package mx.itlalaguna.c20130022.u3diccionarioapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Hacer la transicion hacia el activity principal despues de 3 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Invocamos al main activity
                Intent intent = new Intent( SplashActivity.this, MainActivity.class );
                startActivity(intent);
                finish(); //Finalizamos el SplashActivity
            }
        }, 3000 );
    }
}