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

    public String setUsuario(String user) {
        spEditor = sp.edit();
        spEditor.putString("user", user);
        spEditor.commit();
        return user;
    }

    public String getUsuario() {
        return sp.getString("user","");
    }

}
