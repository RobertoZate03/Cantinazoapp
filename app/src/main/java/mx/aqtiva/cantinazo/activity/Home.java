package mx.aqtiva.cantinazo.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.adapters.AdapterGastos;
import mx.aqtiva.cantinazo.adapters.AdapterInventario;
import mx.aqtiva.cantinazo.adapters.AdapterProductos;
import mx.aqtiva.cantinazo.adapters.AdapterSucursales;
import mx.aqtiva.cantinazo.global.BaseActivity;
import mx.aqtiva.cantinazo.global.DividerItemDecoration;
import mx.aqtiva.cantinazo.global.MarginDecoration;
import mx.aqtiva.cantinazo.global.NonSwipeableViewPager;
import mx.aqtiva.cantinazo.global.Sesion;
import mx.aqtiva.cantinazo.global.UrlInterface;
import mx.aqtiva.cantinazo.listas.ListaGastos;
import mx.aqtiva.cantinazo.listas.ListaInventario;
import mx.aqtiva.cantinazo.listas.ListaProductos;
import mx.aqtiva.cantinazo.listas.ListaSucursales;
import mx.aqtiva.cantinazo.listas.ListaVentas;
import mx.aqtiva.cantinazo.listas.ListaVentasArea;
import mx.aqtiva.cantinazo.listas.ListaVentasDias;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends BaseActivity {
    Sesion sesion;
    public static NonSwipeableViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    FrameLayout page0, page1, page2, page3;
    ArrayList<ListaGastos> listaGastos = new ArrayList<>();
    RecyclerView rvGastos, rvInventario;
    AdapterGastos adapterGastos;
    AdapterInventario adapterInventario;
    TextView tvFecha;
    Spinner spSucursal1, spSucursal2, spSucursal3, spProductos;
    AdapterSucursales adapterSucursales1, adapterSucursales2, adapterSucursales3;
    AdapterProductos adapterProductos;
    ArrayList<ListaSucursales> listaSucursales = new ArrayList<>();
    ArrayList<ListaProductos> listaProductos = new ArrayList<>();
    ArrayList<ListaInventario> listaInventarios = new ArrayList<>();
    ArrayList<ListaVentas> listaVentas = new ArrayList<>();
    ArrayList<ListaVentasDias> listaVentasDias = new ArrayList<>();
    ArrayList<ListaVentasArea> listaVentasAreas = new ArrayList<>();
    String sucursalSelected = "0";
    String insumoSelected = "0";
    float totalVenta = 0;
    private float[] precios;
    ArrayList<String> nombreMarca;

    ////date and time piker
    private static final String CERO = "0";
    private static final String BARRA = "-";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    String id, email, first_name, last_name, username;
    Button btnGFijos, btnGPersonal, btnGCocina, btnGBarra, brnICocina, btnIBarra, brnVPagado, btnVNoPagado;

    BarChart grafica, barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sesion = new Sesion(this);
        tvTitulo = findViewById(R.id.tv_titulo);
        btn_accion = findViewById(R.id.btn_accion);
        btn_menu = findViewById(R.id.btn_menu);
        btn_accion.setOnClickListener(v -> {
            sesion.setLogin(false);
            Intent i = new Intent(Home.this,
                    Login.class);
            startActivity(i);
            finish();
        });
        btn_menu.setVisibility(View.GONE);
        bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_indicadores:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_ventas:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_inventario:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.navigation_gastos:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });
        //ViewPager
        viewPager = findViewById(R.id.vp_home);
        viewPager.beginFakeDrag();
        viewPager.setAdapter(new Home.MainPageAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tvTitulo.setText("Indicadores");
                        getSucursales();
                        break;
                    case 1:
                        tvTitulo.setText("Venta");
                        getVentas("2019-07-01", "2019-07-09", "1", "3");
                        getVentasArea("2019-07-01", "2019-07-09", "1", "3", "2");
                        break;
                    case 2:
                        tvTitulo.setText("Inventario");
                        getSucursales();
                        getInsumos();
                        break;
                    case 3:
                        tvTitulo.setText("Gastos");
                        getSucursales();
                        break;
                    default:
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //No hay evento
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //No hay evento
            }
        });
        //cargamos los datos de usuario, que se almacenan en las preferencias
        String response = sesion.getUsuario();
        Log.e("Respuesta", response);
        JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(response);
            Log.e("Respuesta", jsonResponse + "");
            JSONArray data = jsonResponse.getJSONArray("user");
            for (int i = 0; i < data.length(); i++) {
                id = data.getJSONObject(i).getString("id");
                email = data.getJSONObject(i).getString("email");
                first_name = data.getJSONObject(i).getString("first_name");
                last_name = data.getJSONObject(i).getString("last_name");
                username = data.getJSONObject(i).getString("username");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MainPageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View page = null;
            switch (position) {
                case 0:
                    if (page0 == null) {
                        page0 = (FrameLayout) LayoutInflater.from(Home.this).inflate(R.layout.vp_indicadores, null);
                        tvTitulo.setText("Indicadores");
                        adapterSucursales1 = new AdapterSucursales(Home.this, android.R.layout.simple_spinner_item, listaSucursales);
                        spSucursal1 = page0.findViewById(R.id.spSucursal);
                        spSucursal1.setAdapter(adapterSucursales1);
                        spSucursal1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                sucursalSelected = listaSucursales.get(spSucursal1.getSelectedItemPosition()).id;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    page = page0;
                    break;
                case 1:
                    if (page1 == null) {
                        page1 = (FrameLayout) LayoutInflater.from(Home.this).inflate(R.layout.vp_ventas, null);
                        tvTitulo.setText("Venta");
                        brnVPagado = page1.findViewById(R.id.brnVPagado);
                        brnVPagado.setOnClickListener(v -> cargarVenta("1"));
                        btnVNoPagado = page1.findViewById(R.id.btnVNoPagado);
                        btnVNoPagado.setOnClickListener(v -> cargarVenta("0"));
                        grafica = page1.findViewById(R.id.grafica1);
                        nombreMarca = new ArrayList<>();
                        barChart = page1.findViewById(R.id.grafica2);
                        addDataSet2();
                    }
                    page = page1;
                    break;
                case 2:
                    if (page2 == null) {
                        page2 = (FrameLayout) LayoutInflater.from(Home.this).inflate(R.layout.vp_inventario, null);
                        tvTitulo.setText("Inventario");
                        adapterSucursales3 = new AdapterSucursales(Home.this, android.R.layout.simple_spinner_item, listaSucursales);
                        spSucursal3 = page2.findViewById(R.id.spSucursal);
                        spSucursal3.setAdapter(adapterSucursales3);
                        spSucursal3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                sucursalSelected = listaSucursales.get(spSucursal3.getSelectedItemPosition()).id;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        adapterProductos = new AdapterProductos(Home.this, android.R.layout.simple_spinner_item, listaProductos);
                        spProductos = page2.findViewById(R.id.spProductos);
                        spProductos.setAdapter(adapterProductos);
                        spProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                insumoSelected = listaProductos.get(spProductos.getSelectedItemPosition()).id;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        brnICocina = page2.findViewById(R.id.brnICocina);
                        brnICocina.setOnClickListener(v -> cargarInventario("1"));
                        btnIBarra = page2.findViewById(R.id.btnIBarra);
                        btnIBarra.setOnClickListener(v -> cargarInventario("2"));
                        rvInventario = page2.findViewById(R.id.rv_gastos);
                        rvInventario.setLayoutManager(new GridLayoutManager(Home.this, 1));
                        rvInventario.setItemAnimator(new DefaultItemAnimator());
                        rvInventario.addItemDecoration(new DividerItemDecoration(Home.this, R.drawable.divider));
                        rvInventario.addItemDecoration(new MarginDecoration(Home.this));
                        rvInventario.setHasFixedSize(true);
                        rvInventario.setFocusable(false);
                        adapterInventario = new AdapterInventario(Home.this, listaInventarios);
                        GridLayoutManager manager = (GridLayoutManager) rvInventario.getLayoutManager();
                        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                return adapterInventario.isHeader(position) ? manager.getSpanCount() : 1;
                            }
                        });
                        rvInventario.setAdapter(adapterInventario);
                    }
                    page = page2;
                    break;

                case 3:
                    if (page3 == null) {
                        page3 = (FrameLayout) LayoutInflater.from(Home.this).inflate(R.layout.vp_gastos, null);
                        tvTitulo.setText("Gastos");
                        rvGastos = page3.findViewById(R.id.rv_gastos);
                        rvGastos.setLayoutManager(new GridLayoutManager(Home.this, 1));
                        rvGastos.setItemAnimator(new DefaultItemAnimator());
                        rvGastos.addItemDecoration(new DividerItemDecoration(Home.this, R.drawable.divider));
                        rvGastos.addItemDecoration(new MarginDecoration(Home.this));
                        rvGastos.setHasFixedSize(true);
                        rvGastos.setFocusable(false);
                        adapterGastos = new AdapterGastos(Home.this, listaGastos);
                        GridLayoutManager manager = (GridLayoutManager) rvGastos.getLayoutManager();
                        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                return adapterGastos.isHeader(position) ? manager.getSpanCount() : 1;
                            }
                        });
                        rvGastos.setAdapter(adapterGastos);
                        tvFecha = page3.findViewById(R.id.tvFecha);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String hoy = sdf.format(new Date());
                        tvFecha.setText(hoy);
                        tvFecha.setOnClickListener(v -> obtenerFecha(tvFecha));
                        getGastos(tvFecha.getText().toString(), tvFecha.getText().toString(), "1", "2");
                        btnGFijos = page3.findViewById(R.id.btnGFijos);
                        btnGFijos.setOnClickListener(v -> cargarGastos("1"));
                        btnGPersonal = page3.findViewById(R.id.btnGPersonal);
                        btnGPersonal.setOnClickListener(v -> cargarGastos("2"));
                        btnGCocina = page3.findViewById(R.id.btnGCocina);
                        btnGCocina.setOnClickListener(v -> cargarGastos("3"));
                        btnGBarra = page3.findViewById(R.id.btnGBarra);
                        btnGBarra.setOnClickListener(v -> cargarGastos("4"));
                        adapterSucursales2 = new AdapterSucursales(Home.this, android.R.layout.simple_spinner_item, listaSucursales);
                        spSucursal2 = page3.findViewById(R.id.spSucursal);
                        spSucursal2.setAdapter(adapterSucursales2);
                        spSucursal2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                sucursalSelected = listaSucursales.get(spSucursal2.getSelectedItemPosition()).id;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    page = page3;
                    break;
                default:
            }

            collection.addView(page, 0);
            return page;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }
    }

    private void cargarGastos(String tipo) {
        switch (tipo) {
            case "1":
                btnGFijos.setBackgroundResource(R.drawable.btn_rojo_solid);
                btnGPersonal.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGCocina.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGBarra.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGFijos.setTextColor(Color.parseColor("#ffffff"));
                btnGPersonal.setTextColor(Color.parseColor("#cccccc"));
                btnGCocina.setTextColor(Color.parseColor("#cccccc"));
                btnGBarra.setTextColor(Color.parseColor("#cccccc"));
                break;
            case "2":
                btnGFijos.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGPersonal.setBackgroundResource(R.drawable.btn_rojo_solid);
                btnGCocina.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGBarra.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGFijos.setTextColor(Color.parseColor("#cccccc"));
                btnGPersonal.setTextColor(Color.parseColor("#ffffff"));
                btnGCocina.setTextColor(Color.parseColor("#cccccc"));
                btnGBarra.setTextColor(Color.parseColor("#cccccc"));
                break;
            case "3":
                btnGFijos.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGPersonal.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGCocina.setBackgroundResource(R.drawable.btn_rojo_solid);
                btnGBarra.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGFijos.setTextColor(Color.parseColor("#cccccc"));
                btnGPersonal.setTextColor(Color.parseColor("#cccccc"));
                btnGCocina.setTextColor(Color.parseColor("#ffffff"));
                btnGBarra.setTextColor(Color.parseColor("#cccccc"));
                break;
            case "4":
                btnGFijos.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGPersonal.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGCocina.setBackgroundResource(R.drawable.btn_rojo_border);
                btnGBarra.setBackgroundResource(R.drawable.btn_rojo_solid);
                btnGFijos.setTextColor(Color.parseColor("#cccccc"));
                btnGPersonal.setTextColor(Color.parseColor("#cccccc"));
                btnGCocina.setTextColor(Color.parseColor("#cccccc"));
                btnGBarra.setTextColor(Color.parseColor("#ffffff"));
                break;
        }
        getGastos(tvFecha.getText().toString(), tvFecha.getText().toString(), tipo, sucursalSelected);
    }

    private void cargarInventario(String area) {
        switch (area) {
            case "1":
                brnICocina.setBackgroundResource(R.drawable.btn_rojo_solid);
                btnIBarra.setBackgroundResource(R.drawable.btn_rojo_border);
                brnICocina.setTextColor(Color.parseColor("#ffffff"));
                btnIBarra.setTextColor(Color.parseColor("#cccccc"));
                break;
            case "2":
                brnICocina.setBackgroundResource(R.drawable.btn_rojo_border);
                btnIBarra.setBackgroundResource(R.drawable.btn_rojo_solid);
                brnICocina.setTextColor(Color.parseColor("#cccccc"));
                btnIBarra.setTextColor(Color.parseColor("#ffffff"));
                break;
        }
        Log.e("Res", insumoSelected + "-" + area + "-" + sucursalSelected);
        getInventario(insumoSelected, area, sucursalSelected);
    }

    private void cargarVenta(String pagado) {
        switch (pagado) {
            case "1":
                brnVPagado.setBackgroundResource(R.drawable.btn_rojo_solid);
                btnVNoPagado.setBackgroundResource(R.drawable.btn_rojo_border);
                brnVPagado.setTextColor(Color.parseColor("#ffffff"));
                btnVNoPagado.setTextColor(Color.parseColor("#cccccc"));
                break;
            case "0":
                brnVPagado.setBackgroundResource(R.drawable.btn_rojo_border);
                btnVNoPagado.setBackgroundResource(R.drawable.btn_rojo_solid);
                brnVPagado.setTextColor(Color.parseColor("#cccccc"));
                btnVNoPagado.setTextColor(Color.parseColor("#ffffff"));
                break;
        }
    }

    public void getGastos(String fechaInicio, String fechaFin, String tipo, String sucursal) {

        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getGastosApi(sesion.getToken(), fechaInicio, fechaFin, tipo, sucursal);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() >= 200 && response.code() < 300) {

                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                listaGastos.clear();
                                //parseamos el json obtenido del backend
                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response.body() + "");
                                    Log.e("Respuesta", jsonResponse + "");
                                    JSONArray data = jsonResponse.getJSONArray("gastoReporte");
                                    for (int i = 0; i < data.length(); i++) {
                                        String motivo = data.getJSONObject(i).getString("motivo");
                                        String cantidad = data.getJSONObject(i).getString("cantidad");
                                        listaGastos.add(new ListaGastos(motivo, cantidad));
                                    }
                                    Double totalGasto = jsonResponse.getDouble("totalGasto");
                                    listaGastos.add(new ListaGastos("Total", totalGasto + ""));
                                    Log.e("itemgastos", listaGastos.size() + "");
                                    adapterGastos.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } catch (Exception e) {
                                Log.e("err", e.toString());
                            }
                        } else {
                            Toast.makeText(Home.this, "Gastos.- Sin datos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Home.this, "Gastos.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    Toast.makeText(Home.this, "Gastos.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Home.this, "Gastos.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, "Gastos.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getInventario(String insumo, String area, String sucursal) {
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getInventarioApi(sesion.getToken(), insumo, area, sucursal);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() >= 200 && response.code() < 300) {

                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                listaInventarios.clear();
                                listaInventarios.add(new ListaInventario("Nombre", "Costo", "Restante", "Sucursal"));
                                //parseamos el json obtenido del backend
                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response.body() + "");
                                    Log.e("Respuesta", jsonResponse + "");
                                    JSONArray data = jsonResponse.getJSONArray("inventario");
                                    for (int i = 0; i < data.length(); i++) {
                                        String nombre = data.getJSONObject(i).getString("nombre");
                                        String costo = data.getJSONObject(i).getString("costo");
                                        String restante = data.getJSONObject(i).getString("restante");
                                        String sucursal = data.getJSONObject(i).getString("sucursal");
                                        listaInventarios.add(new ListaInventario(nombre, costo, restante, sucursal));
                                    }
                                    Log.e("Respuesta", listaInventarios.size() + "");
                                    adapterInventario.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } catch (Exception e) {
                                Log.e("err", e.toString());
                            }
                        } else {
                            Toast.makeText(Home.this, "Inventario.- Sin datos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Home.this, "Inventario.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    Toast.makeText(Home.this, "Inventario.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Home.this, "Inventario.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, "Inventario.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getSucursales() {
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
                                adapterSucursales1.notifyDataSetChanged();
                                adapterSucursales2.notifyDataSetChanged();
                                adapterSucursales3.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(Home.this, "Home.- Ocurrio un error", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                }
                Toast.makeText(Home.this, "2.- Home.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, "Home.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getInsumos() {
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getInsumosApi(sesion.getToken());
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() >= 200 && response.code() < 300) {
                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                Log.e("Respuesta", response.body() + "");
                                listaProductos.clear();
                                listaProductos.add(new ListaProductos("0", "Seleccione un insumo"));
                                //parseamos el json obtenido del backend
                                JSONObject jsonResponse = new JSONObject(response.body() + "");
                                JSONArray data = jsonResponse.getJSONArray("insumos");
                                for (int i = 0; i < data.length(); i++) {
                                    String id = data.getJSONObject(i).getString("idInsumo");
                                    String nombre = data.getJSONObject(i).getString("nombre");
                                    listaProductos.add(new ListaProductos(id, nombre));
                                }
                                adapterProductos.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(Home.this, "Home.- Ocurrio un error", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                }
                Toast.makeText(Home.this, "2.- Home.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, "Home.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getVentas(String fechaI, String fechaF, String pagado, String sucursal) {
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getVentasApi(sesion.getToken(), fechaI, fechaF, pagado, sucursal);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() >= 200 && response.code() < 300) {

                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                listaVentas.clear();
                                nombreMarca.clear();
                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response.body() + "");
                                    Log.e("Respuesta", jsonResponse + "");
                                    JSONArray data = jsonResponse.getJSONArray("ventasReporte");
                                    for (int i = 0; i < data.length(); i++) {
                                        String id = data.getJSONObject(i).getString("id");
                                        String monto_total = data.getJSONObject(i).getString("monto_total");
                                        String mesa = data.getJSONObject(i).getString("mesa");
                                        String fecha_venta = data.getJSONObject(i).getString("fecha_venta");
                                        String pagado = data.getJSONObject(i).getString("pagado");
                                        String usuario_id = data.getJSONObject(i).getString("usuario_id");
                                        String usuario = data.getJSONObject(i).getString("usuario");
                                        String tipo_pago = data.getJSONObject(i).getString("tipo_pago");
                                        String hora_venta = data.getJSONObject(i).getString("hora_venta");
                                        String terminado = data.getJSONObject(i).getString("terminado");

                                        //nombreMarca.add(i, id + ".-" + fecha_venta);
                                        listaVentas.add(new ListaVentas(id, monto_total, mesa, fecha_venta, pagado, usuario_id, usuario, tipo_pago, hora_venta, terminado));
                                    }
                                    JSONArray data2 = jsonResponse.getJSONArray("ventasReportexDia");
                                    for (int y = 0; y < data2.length(); y++) {
                                        String total = data2.getJSONObject(y).getString("total");
                                        String fecha = data2.getJSONObject(y).getString("fecha");

                                        nombreMarca.add(fecha);
                                        listaVentasDias.add(new ListaVentasDias(total, fecha));
                                    }
                                    totalVenta = response.body().get("totalVenta").getAsFloat();
                                    addDataSet();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } catch (Exception e) {
                                Log.e("err", e.toString());
                            }
                        } else {
                            Toast.makeText(Home.this, "Venta.- Sin datos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Home.this, "Venta.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    Toast.makeText(Home.this, "Venta.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Home.this, "Venta.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, "Venta.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getVentasArea(String fechaI, String fechaF, String pagado, String sucursal, String area_id) {
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getVentasDivididasApi(sesion.getToken(), fechaI, fechaF, pagado, sucursal);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() >= 200 && response.code() < 300) {

                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                listaVentasAreas.clear();
                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response.body() + "");
                                    Log.e("Respuesta", jsonResponse + "");
                                    JSONArray data = jsonResponse.getJSONArray("content");
                                    for (int i = 0; i < data.length(); i++) {
                                        String fecha = data.getJSONObject(i).getString("fecha");
                                        String producto_id = data.getJSONObject(i).getString("producto_id");
                                        String producto = data.getJSONObject(i).getString("producto");
                                        String total = data.getJSONObject(i).getString("total");
                                        String area = data.getJSONObject(i).getString("area");
                                        if(area.equals(area_id)) {
                                            listaVentasAreas.add(new ListaVentasArea(fecha, producto_id, producto, total, area));
                                        }
                                    }
                                    Log.e("Respuesta", listaVentasAreas.size() + " .- lista de ventas por area");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } catch (Exception e) {
                                Log.e("err", e.toString());
                            }
                        } else {
                            Toast.makeText(Home.this, "Venta.- Sin datos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Home.this, "Venta.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    Toast.makeText(Home.this, "Venta.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Home.this, "Venta.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, "Venta.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
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

    private void addDataSet() {
        Log.e("Resultado", nombreMarca + " .- tamaño nombreMarca");
        Log.e("Resultado", listaVentasDias + " .- tamaño listaVentasDias");
        grafica.setMaxVisibleValueCount(listaVentasDias.size());
        ArrayList<BarEntry> precio = new ArrayList<>();
        for (int i = 0; i < listaVentasDias.size(); i++) {
            precios = new float[listaVentasDias.size()];

            float entrada = Float.parseFloat("" + listaVentasDias.get(i).monto_total);
            precios[i] = entrada;
            precio.add(new BarEntry(i, entrada));
        }

        BarDataSet dataSet = new BarDataSet(precio, "Venta");

        ArrayList<Integer> colors = new ArrayList<>();
        int color1 = getResources().getColor(R.color.graficaReporteAzulMar);
        int color2 = getResources().getColor(R.color.graficaReporteRosa);
        int color3 = getResources().getColor(R.color.graficaReporteAnaranjado);
        int color4 = getResources().getColor(R.color.graficaReporeCafe);
        int color5 = getResources().getColor(R.color.grafica_reporte_verde);
        int color6 = getResources().getColor(R.color.graficaReporteMorado);
        int color7 = getResources().getColor(R.color.graficaReporteAzul);
        colors.add(color1);
        colors.add(color2);
        colors.add(color3);
        colors.add(color4);
        colors.add(color5);
        colors.add(color6);
        colors.add(color7);
        dataSet.setColors(colors);

        dataSet.setValueTextColor(R.color.gris);
        YAxis left = grafica.getAxisLeft();
        left.setEnabled(true);

        XAxis xaxis = grafica.getXAxis();
        xaxis.setDrawGridLines(false);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularity(1f);
        xaxis.setDrawLabels(true);
        xaxis.setDrawAxisLine(false);
        xaxis.setValueFormatter(new IndexAxisValueFormatter(nombreMarca));
        xaxis.setGridColor(R.color.gris);
        xaxis.setTextColor(R.color.gris);

        left.setValueFormatter(new DefaultAxisValueFormatter(2));
        left.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        BarData data = new BarData(dataSets);
        data.setBarWidth(0.9f);
        data.setValueTextSize(9f);
        data.setValueTextColor(Color.BLACK);
        Legend legend = grafica.getLegend();
        legend.setEnabled(true);
        grafica.animateY(1000);
        grafica.setData(data);
        grafica.invalidate();
        grafica.setDescription(null);


    }

    public void addDataSet2(){
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription(null);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(true);
        xl.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true
        barChart.getAxisRight().setEnabled(false);

        //data
        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.46f; // x2 dataset
        // (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"

        int startYear = 1980;
        int endYear = 1985;


        List<BarEntry> yVals1 = new ArrayList<>();
        List<BarEntry> yVals2 = new ArrayList<>();


        for (int i = startYear; i < endYear; i++) {
            yVals1.add(new BarEntry(i, 0.4f));
        }

        for (int i = startYear; i < endYear; i++) {
            yVals2.add(new BarEntry(i, 0.7f));
        }


        BarDataSet set1, set2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet)barChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            int color1 = getResources().getColor(R.color.graficaReporteAzulMar);
            int color2 = getResources().getColor(R.color.graficaReporteMorado);
            set1 = new BarDataSet(yVals1, "Cocina");
            set1.setColor(color1);
            set2 = new BarDataSet(yVals2, "Barra");
            set2.setColor(color2);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }

        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(startYear);
        barChart.groupBars(startYear, groupSpace, barSpace);
        barChart.invalidate();
    }
}
