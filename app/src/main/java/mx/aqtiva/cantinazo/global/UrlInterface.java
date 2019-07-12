package mx.aqtiva.cantinazo.global;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UrlInterface {

    @FormUrlEncoded
    @POST("loginApi/")
    Call<JsonObject> login(@Field("username") String username, @Field("password") String password);

    @GET("getCheckins/")
    Call<JsonObject> getCheckins(@Header("Authorization") String authorization,
                                 @Query("sucursal") String sucursal,
                                 @Query("supervisor") String supervisor,
                                 @Query("fecha") String fecha);

    @GET("getGastosApi/")
    Call<JsonObject> getGastosApi(@Header("Authorization") String authorization,
                                 @Query("fecha_inicio") String fecha_inicio,
                                 @Query("fecha_fin") String fecha_fin,
                                 @Query("tipo") String tipo,
                                 @Query("sucursal") String sucursal);

    @GET("getSucursalesApi/")
    Call<JsonObject> getSucursalesApi(@Header("Authorization") String authorization);

}
