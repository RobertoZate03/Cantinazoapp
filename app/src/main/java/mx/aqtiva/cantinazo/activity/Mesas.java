package mx.aqtiva.cantinazo.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.adapters.AdapterMesasActivas;
import mx.aqtiva.cantinazo.adapters.AdapterMesasPagadas;
import mx.aqtiva.cantinazo.adapters.AdapterSucursales;
import mx.aqtiva.cantinazo.global.BaseActivity;
import mx.aqtiva.cantinazo.global.DividerItemDecoration;
import mx.aqtiva.cantinazo.global.MarginDecoration;
import mx.aqtiva.cantinazo.global.Sesion;
import mx.aqtiva.cantinazo.global.UrlInterface;
import mx.aqtiva.cantinazo.listas.ListaMesasActiva;
import mx.aqtiva.cantinazo.listas.ListaMesasPagadas;
import mx.aqtiva.cantinazo.listas.ListaSucursales;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Mesas extends BaseActivity {

    ProgressDialog progress;
    Sesion sesion;
    ArrayList<ListaMesasActiva> listaMesasActivas = new ArrayList<>();
    ArrayList<ListaMesasPagadas> listaMesasPagadas = new ArrayList<>();
    ArrayList<ListaSucursales> listaSucursales = new ArrayList<>();
    TextView tvFechaI, tvFechaF, tv_total_efectivoA, tv_total_creditoA, tv_total_debitoA, tv_total_efectivoP, tv_total_creditoP, tv_total_debitoP;
    ////date and time piker
    private static final String CERO = "0";
    private static final String BARRA = "-";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    Button btnFiltro, btnActivos, btnPagados;
    AdapterSucursales adapterSucursales1;
    Spinner spSucursal1;
    String sucursalSelected = "0";
    RecyclerView rv_activos, rv_pagados;
    AdapterMesasActivas adapterMesasActivas;
    AdapterMesasPagadas adapterMesasPagadas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);
        sesion = new Sesion(this);
        progress = new ProgressDialog(Mesas.this);
        progress.setMessage("Cargando...");
        progress.setTitle("Cantinazo");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        rv_activos = findViewById(R.id.rv_activos);
        rv_pagados = findViewById(R.id.rv_pagados);

        tvTitulo = findViewById(R.id.tv_titulo);
        tvTitulo.setText("Mesas activas");
        btn_accion = findViewById(R.id.btn_accion);
        btn_menu = findViewById(R.id.btn_menu);
        btn_accion.setVisibility(View.GONE);
        btn_menu.setVisibility(View.VISIBLE);
        btn_menu.setOnClickListener(v -> finish());
        btn_mesas = findViewById(R.id.btn_mesas);
        btn_mesas.setVisibility(View.GONE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String hoy = sdf.format(new Date());
        tvFechaI = findViewById(R.id.tvFechaI);
        tvFechaI.setText(hoy);
        tvFechaI.setOnClickListener(v -> {
            obtenerFecha(tvFechaI);
        });
        tvFechaF = findViewById(R.id.tvFechaF);
        tvFechaF.setText(hoy);
        tvFechaF.setOnClickListener(v -> {
            obtenerFecha(tvFechaF);
        });
        tv_total_efectivoA = findViewById(R.id.tv_total_efectivoA);
        tv_total_creditoA = findViewById(R.id.tv_total_creditoA);
        tv_total_debitoA = findViewById(R.id.tv_total_debitoA);
        tv_total_efectivoP = findViewById(R.id.tv_total_efectivoP);
        tv_total_creditoP = findViewById(R.id.tv_total_creditoP);
        tv_total_debitoP = findViewById(R.id.tv_total_debitoP);
        adapterSucursales1 = new AdapterSucursales(Mesas.this, android.R.layout.simple_spinner_item, listaSucursales);
        spSucursal1 = findViewById(R.id.spSucursal);
        spSucursal1.setAdapter(adapterSucursales1);
        getSucursales(adapterSucursales1);
        spSucursal1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sucursalSelected = listaSucursales.get(spSucursal1.getSelectedItemPosition()).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnFiltro = findViewById(R.id.btnFiltro);
        btnFiltro.setOnClickListener(v -> getMesas(tvFechaI.getText().toString(), tvFechaF.getText().toString(), sucursalSelected));
        btnActivos = findViewById(R.id.btnActivos);
        btnActivos.setOnClickListener(v -> cargarMesas("1"));
        btnPagados = findViewById(R.id.btnPagados);
        btnPagados.setOnClickListener(v -> cargarMesas("0"));

        rv_activos = findViewById(R.id.rv_activos);
        rv_activos.setLayoutManager(new GridLayoutManager(Mesas.this, 1));
        rv_activos.setItemAnimator(new DefaultItemAnimator());
        rv_activos.addItemDecoration(new DividerItemDecoration(Mesas.this, R.drawable.divider));
        rv_activos.addItemDecoration(new MarginDecoration(Mesas.this));
        rv_activos.setHasFixedSize(true);
        rv_activos.setFocusable(false);
        adapterMesasActivas = new AdapterMesasActivas(Mesas.this, listaMesasActivas);
        GridLayoutManager manager = (GridLayoutManager) rv_activos.getLayoutManager();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapterMesasActivas.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });
        rv_activos.setAdapter(adapterMesasActivas);

        rv_pagados = findViewById(R.id.rv_pagados);
        rv_pagados.setLayoutManager(new GridLayoutManager(Mesas.this, 1));
        rv_pagados.setItemAnimator(new DefaultItemAnimator());
        rv_pagados.addItemDecoration(new DividerItemDecoration(Mesas.this, R.drawable.divider));
        rv_pagados.addItemDecoration(new MarginDecoration(Mesas.this));
        rv_pagados.setHasFixedSize(true);
        rv_pagados.setFocusable(false);
        adapterMesasPagadas = new AdapterMesasPagadas(Mesas.this, listaMesasPagadas);
        GridLayoutManager manager1 = (GridLayoutManager) rv_pagados.getLayoutManager();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapterMesasPagadas.isHeader(position) ? manager1.getSpanCount() : 1;
            }
        });
        rv_pagados.setAdapter(adapterMesasPagadas);
    }

    private void cargarMesas(String activo) {
        switch (activo) {
            case "1":
                btnActivos.setBackgroundResource(R.drawable.btn_rojo_solid);
                btnPagados.setBackgroundResource(R.drawable.btn_rojo_border);
                btnActivos.setTextColor(Color.parseColor("#ffffff"));
                btnPagados.setTextColor(Color.parseColor("#cccccc"));
                rv_activos.setVisibility(View.VISIBLE);
                rv_pagados.setVisibility(View.GONE);
                tv_total_efectivoA.setVisibility(View.GONE);
                tv_total_creditoA.setVisibility(View.GONE);
                tv_total_debitoA.setVisibility(View.GONE);
                tv_total_efectivoP.setVisibility(View.GONE);
                tv_total_creditoP.setVisibility(View.GONE);
                tv_total_debitoP.setVisibility(View.GONE);
                break;
            case "0":
                btnActivos.setBackgroundResource(R.drawable.btn_rojo_border);
                btnPagados.setBackgroundResource(R.drawable.btn_rojo_solid);
                btnActivos.setTextColor(Color.parseColor("#cccccc"));
                btnPagados.setTextColor(Color.parseColor("#ffffff"));
                rv_activos.setVisibility(View.GONE);
                rv_pagados.setVisibility(View.VISIBLE);
                tv_total_efectivoA.setVisibility(View.GONE);
                tv_total_creditoA.setVisibility(View.GONE);
                tv_total_debitoA.setVisibility(View.GONE);
                tv_total_efectivoP.setVisibility(View.VISIBLE);
                tv_total_creditoP.setVisibility(View.VISIBLE);
                tv_total_debitoP.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void getSucursales(AdapterSucursales adapterSucursales) {
        progress.show();
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getSucursalesApi(sesion.getToken());
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() >= 200 && response.code() < 300) {
                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                Log.e("Respuesta", response.body() + "");
                                listaSucursales.clear();
                                listaSucursales.add(new ListaSucursales("0", "Seleccione una sucursal"));
                                //parseamos el json obtenido del backend
                                JSONObject jsonResponse = new JSONObject(response.body() + "");
                                JSONArray data = jsonResponse.getJSONArray("sucursales");
                                for (int i = 0; i < data.length(); i++) {
                                    String id = data.getJSONObject(i).getString("idSucursal");
                                    String nombre = data.getJSONObject(i).getString("nombre");
                                    listaSucursales.add(new ListaSucursales(id, nombre));
                                }
                                adapterSucursales.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progress.dismiss();
                            }
                        } else {
                            Toast.makeText(Mesas.this, "Home.- Ocurrio un error", Toast.LENGTH_LONG).show();
                        }
                        progress.dismiss();
                        return;
                    }
                }
                progress.dismiss();
                Toast.makeText(Mesas.this, "2.- Home.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Mesas.this, "Home.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getMesas(String fechaI, String fechaF, String sucursal) {
        Log.e("Ress", "entro a la verga");
        listaMesasActivas.clear();
        listaMesasPagadas.clear();
        progress.show();
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getMesasApi(sesion.getToken(), fechaI, fechaF, sucursal);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.e("Ress", response + "");
                if (response.code() >= 200 && response.code() < 300) {

                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                JSONObject jsonResponse = null;
                                try {
                                    listaMesasActivas.add(new ListaMesasActiva("", "", "", "", "", "", "", "", "", "", "", ""));
                                    listaMesasPagadas.add(new ListaMesasPagadas("", "", "", "", "", "", "", "", "", "", "", ""));
                                    jsonResponse = new JSONObject(response.body() + "");
                                    Log.e("Respuesta", jsonResponse + "");
                                    float creditoA = 0;
                                    float efectivoA = 0;
                                    float debitoA = 0;
                                    float creditoP = 0;
                                    float efectivoP = 0;
                                    float debitoP = 0;
                                    JSONArray data = jsonResponse.getJSONArray("activo");
                                    for (int i = 0; i < data.length(); i++) {
                                        String id = data.getJSONObject(i).getString("id");
                                        String monto_total = data.getJSONObject(i).getString("monto_total");
                                        String mesa = data.getJSONObject(i).getString("mesa");
                                        String fecha_venta = data.getJSONObject(i).getString("fecha_venta");
                                        String pagado = data.getJSONObject(i).getString("pagado");
                                        String usuario_id = data.getJSONObject(i).getString("usuario_id");
                                        String usuario_name = data.getJSONObject(i).getString("usuario_name");
                                        String sucursal_id = data.getJSONObject(i).getString("sucursal_id");
                                        String sucursal_name = data.getJSONObject(i).getString("sucursal_name");
                                        String tipo_pago_id = data.getJSONObject(i).getString("tipo_pago_id");
                                        String tipo_pago_name = data.getJSONObject(i).getString("tipo_pago_name");
                                        String terminado = data.getJSONObject(i).getString("terminado");
                                        listaMesasActivas.add(new ListaMesasActiva(id, monto_total, mesa, fecha_venta, pagado, usuario_id, usuario_name, sucursal_id, sucursal_name, tipo_pago_id, tipo_pago_name, terminado));
                                        switch (tipo_pago_id) {
                                            case "1"://efectivo
                                                efectivoA = efectivoA + Float.parseFloat(monto_total);
                                                break;
                                            case "2"://credito
                                                creditoA = creditoA + Float.parseFloat(monto_total);
                                                break;
                                            case "3"://debito
                                                debitoA = debitoA + Float.parseFloat(monto_total);
                                                break;
                                        }
                                    }
                                    JSONArray data2 = jsonResponse.getJSONArray("pagado");
                                    for (int i = 0; i < data2.length(); i++) {
                                        String id = data2.getJSONObject(i).getString("id");
                                        String monto_total = data2.getJSONObject(i).getString("monto_total");
                                        String mesa = data2.getJSONObject(i).getString("mesa");
                                        String fecha_venta = data2.getJSONObject(i).getString("fecha_venta");
                                        String pagado = data2.getJSONObject(i).getString("pagado");
                                        String usuario_id = data2.getJSONObject(i).getString("usuario_id");
                                        String usuario_name = data2.getJSONObject(i).getString("usuario_name");
                                        String sucursal_id = data2.getJSONObject(i).getString("sucursal_id");
                                        String sucursal_name = data2.getJSONObject(i).getString("sucursal_name");
                                        String tipo_pago_id = data2.getJSONObject(i).getString("tipo_pago_id");
                                        String tipo_pago_name = data2.getJSONObject(i).getString("tipo_pago_name");
                                        String terminado = data2.getJSONObject(i).getString("terminado");
                                        //listaMesasPagadas.add(new ListaMesasPagadas(id, monto_total, mesa, fecha_venta, pagado, usuario_id, usuario_name, sucursal_id, sucursal_name, tipo_pago_id, tipo_pago_name, terminado));
                                        switch (tipo_pago_id) {
                                            case "1"://efectivo
                                                efectivoP = efectivoP + Float.parseFloat(monto_total);
                                                break;
                                            case "2"://credito
                                                creditoP = creditoP + Float.parseFloat(monto_total);
                                                break;
                                            case "3"://debito
                                                debitoP = debitoP + Float.parseFloat(monto_total);
                                                break;
                                        }

                                    }
                                    JSONArray data3 = jsonResponse.getJSONArray("totales");
                                    for (int i = 0; i < data3.length(); i++) {
                                        String id = data3.getJSONObject(i).getString("id");
                                        String monto_total = data3.getJSONObject(i).getString("monto_total");
                                        String mesa = data3.getJSONObject(i).getString("mesa");
                                        String fecha_venta = data3.getJSONObject(i).getString("fecha_venta");
                                        String pagado = data3.getJSONObject(i).getString("pagado");
                                        String usuario_id = data3.getJSONObject(i).getString("usuario_id");
                                        String usuario_name = data3.getJSONObject(i).getString("usuario_name");
                                        String sucursal_id = data3.getJSONObject(i).getString("sucursal_id");
                                        String sucursal_name = data3.getJSONObject(i).getString("sucursal_name");
                                        String tipo_pago_id = data3.getJSONObject(i).getString("tipo_pago_id");
                                        String tipo_pago_name = data3.getJSONObject(i).getString("tipo_pago_name");
                                        String terminado = data3.getJSONObject(i).getString("terminado");
                                        listaMesasPagadas.add(new ListaMesasPagadas(id, monto_total, mesa, fecha_venta, pagado, usuario_id, usuario_name, sucursal_id, sucursal_name, tipo_pago_id, tipo_pago_name, terminado));
                                        /*switch (tipo_pago_id) {
                                            case "1"://efectivo
                                                efectivoP = efectivoP + Float.parseFloat(monto_total);
                                                break;
                                            case "2"://credito
                                                creditoP = creditoP + Float.parseFloat(monto_total);
                                                break;
                                            case "3"://debito
                                                debitoP = debitoP + Float.parseFloat(monto_total);
                                                break;
                                        }*/

                                    }
                                    cargarMesas("1");
                                    adapterMesasActivas.notifyDataSetChanged();
                                    adapterMesasPagadas.notifyDataSetChanged();
                                    tv_total_efectivoA.setText("Total en efectivo $" + efectivoA);
                                    tv_total_creditoA.setText("Total en tarjeta credito $" + creditoA);
                                    tv_total_debitoA.setText("Total en tarjeta debito $" + debitoA);
                                    tv_total_efectivoP.setText("Total en efectivo $" + efectivoP);
                                    tv_total_creditoP.setText("Total en tarjeta credito $" + creditoP);
                                    tv_total_debitoP.setText("Total en tarjeta debito $" + debitoP);
                                    Log.e("Lista de mesas activa", listaMesasActivas.size() + "");
                                    Log.e("Lista de mesas pagada", listaMesasPagadas.size() + "");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progress.dismiss();
                                }
                                progress.dismiss();
                                return;
                            } catch (Exception e) {
                                progress.dismiss();
                                Log.e("err", e.toString());
                            }
                        } else {
                            progress.dismiss();
                            Toast.makeText(Mesas.this, "Mesas.- Sin datos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progress.dismiss();
                        Toast.makeText(Mesas.this, "Mesas.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    progress.dismiss();
                    Toast.makeText(Mesas.this, "Mesas.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    progress.dismiss();
                    Toast.makeText(Mesas.this, "Mesas.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Mesas.this, "Mesas.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void obtenerFecha(TextView tvFecha) {
        DatePickerDialog recogerFecha = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            final int mesActual = month + 1;
            String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
            String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
            String fecha = year + BARRA + mesFormateado + BARRA + diaFormateado;
            tvFecha.setText(fecha);
        }, anio, mes, dia);
        recogerFecha.show();

    }
}
