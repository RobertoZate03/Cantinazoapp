package mx.aqtiva.cantinazo.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.global.Sesion;

public class Splash extends AppCompatActivity {
    Sesion sesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sesion = new Sesion(this);
        if (sesion.getLoggedIn()) {
            new Handler().postDelayed(() -> {
                Intent i = new Intent(Splash.this,
                        Home.class);
                startActivity(i);
                finish();
            }, 2000);
        }else{
            new Handler().postDelayed(() -> {
                Intent i = new Intent(Splash.this,
                        Login.class);
                startActivity(i);
                finish();
            }, 2000);
        }

    }
}
