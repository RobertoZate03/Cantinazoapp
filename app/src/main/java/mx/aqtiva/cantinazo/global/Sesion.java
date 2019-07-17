package mx.aqtiva.cantinazo.global;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

/**
 * Created by Roberto Zate on 03/07/2017.
 */

public class Sesion {
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    public Sesion(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);

    }


    public boolean setLogin(boolean status) {
        spEditor = sp.edit();
        spEditor.putBoolean("is_logged_in", status);
        spEditor.commit();
        return true;
    }

    public boolean getLoggedIn() {
        return sp.getBoolean("is_logged_in", false);
    }

    public String setToken(String token) {
        spEditor = sp.edit();
        spEditor.putString("token", token);
        spEditor.commit();
        return token;
    }

    public String getToken() {
        return sp.getString("token","");
    }

    public String setRol(String rol) {
        spEditor = sp.edit();
        spEditor.putString("rol", rol);
        spEditor.commit();
        return rol;
    }

    public String getRol() {
        return sp.getString("rol","");
    }

    public String setIdSucursal(String idSucursal) {
        spEditor = sp.edit();
        spEditor.putString("idSucursal", idSucursal);
        spEditor.commit();
        return idSucursal;
    }

    public String getIdSucursal() {
        return sp.getString("idSucursal","");
    }

    public String setUsuario(String user) {
        spEditor = sp.edit();
        spEditor.putString("user", user);
        spEditor.commit();
        return user;
    }

    public String getUsuario() {
        return sp.getString("user","");
    }

    public String setEmail(String email) {
        spEditor = sp.edit();
        spEditor.putString("email", email);
        spEditor.commit();
        return email;
    }

    public String getEmail() {
        return sp.getString("email","");
    }


    public String setPass(String pass) {
        spEditor = sp.edit();
        spEditor.putString("pass", pass);
        spEditor.commit();
        return pass;
    }

    public String getPass() {
        return sp.getString("pass","");
    }

}
