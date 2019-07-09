package mx.aqtiva.cantinazo.global;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import mx.aqtiva.cantinazo.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseActivity extends Activity {

    private final static String baseUrl = "http://192.168.100.14:8000/";//ip trabajo
    public final static String urlapp = baseUrl + "sistema/";
    protected Retrofit retrofit;
    public static TextView tvTitulo;
    public static ImageButton btn_menu, btn_accion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrofit = new Retrofit.Builder()
                .baseUrl(BaseActivity.urlapp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
