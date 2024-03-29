package mx.aqtiva.cantinazo.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.global.BaseActivity;
import mx.aqtiva.cantinazo.global.Sesion;
import mx.aqtiva.cantinazo.global.UrlInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends BaseActivity {

    Button btnIniciar;
    EditText etEmail, etPassword;
    Sesion sesion;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sesion = new Sesion(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // show password
                checkBox.setText("Ocultar password");
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // hide password
                checkBox.setText("Mostrar password");
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        btnIniciar = findViewById(R.id.btnIniciar);
        btnIniciar.setOnClickListener(v -> loginClick(v));
        if (sesion.getEmail().length() == 0) {
            etEmail.setText("");
            etPassword.setText("");
        } else {
            etEmail.setText(sesion.getEmail());
            etPassword.setText(sesion.getPass());
        }
    }

    public void loginClick(View v) {
        String username = etEmail.getText().toString();
        sesion.setEmail(username);
        String password = etPassword.getText().toString();
        sesion.setPass(password);

        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).loginApi(username, password);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.e("Respuesta", response + "");
                if (response.code() >= 200 && response.code() < 300) {
                    if (response.body() != null && response.body().has("token")) {
                        try {
                            Log.e("Respuesta", response.body() + "");
                            String key = response.body().get("token").getAsString();
                            sesion.setToken("Token " + key);
                            String rol = response.body().get("rol").getAsString();
                            sesion.setRol(rol);
                            //parseamos el json obtenido del backend
                            JSONObject jsonResponse = null;
                            try {
                                jsonResponse = new JSONObject(response.body() + "");
                                Log.e("Respuesta", jsonResponse + "");
                                JSONArray array = jsonResponse.getJSONArray("user");
                                sesion.setUsuario(response.body() + "");
                                if(rol.equals("1")) {
                                    sesion.setLogin(true);
                                    Intent i = new Intent(Login.this,
                                            Home.class);
                                    startActivity(i);
                                    finish();
                                }else{
                                    sesion.setLogin(false);
                                    Toast.makeText(Login.this, "No tienes acceso a la app", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return;
                        }catch (Exception e){
                            Log.e("err", e.toString());
                        }
                    }else{
                        Log.e("Token", "No trae el token, algo paso");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Login.this, "Inicio de sesión.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

    }
}
