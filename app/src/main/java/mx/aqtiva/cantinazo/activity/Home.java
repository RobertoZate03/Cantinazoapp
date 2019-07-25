package mx.aqtiva.cantinazo.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import mx.aqtiva.cantinazo.listas.ListaVBarraCocina;
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
    ArrayList<ListaVBarraCocina> listaVBarraCocinas = new ArrayList<>();
    RecyclerView rvGastos, rvInventario;
    AdapterGastos adapterGastos;
    AdapterInventario adapterInventario;
    TextView tvFechaVentaI, tvFechaVentaF, tvFechaIndicadoresI, tvFechaIndicadoresF, tvFechaGastoI, tvFechaGastoF;
    Spinner spSucursal1, spSucursal2, spSucursal3, spSucursal4, spProductos;
    AdapterSucursales adapterSucursales1, adapterSucursales2, adapterSucursales3, adapterSucursales4;
    AdapterProductos adapterProductos;
    ArrayList<ListaSucursales> listaSucursales = new ArrayList<>();
    ArrayList<ListaProductos> listaProductos = new ArrayList<>();
    ArrayList<ListaInventario> listaInventarios = new ArrayList<>();
    ArrayList<ListaVentas> listaVentas = new ArrayList<>();
    ArrayList<ListaVentasDias> listaVentasDias = new ArrayList<>();
    String sucursalSelected = "0";
    String insumoSelected = "0";
    float totalVenta = 0;
    private float[] precios;
    ArrayList<String> nombreMarca;
    ArrayList<String> fechaVenta;
    ProgressDialog progress;
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
    Button btnGFijos, btnGPersonal, btnGCocina, btnGBarra, brnICocina, btnIBarra, brnVPagado, btnVNoPagado, btnFiltro;

    BarChart grafica, barChart;

    TextView tv_total_gasto_fijo, tv_total_gasto_personal, tv_total_gasto_cocina, tv_total_gasto_barra, tv_total_gastos, tv_total_venta_barra, tv_total_venta_cocina, tv_total_venta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sesion = new Sesion(this);
        progress = new ProgressDialog(Home.this);
        progress.setMessage("Cargando...");
        progress.setTitle("Cantinazo");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
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
        btn_mesas = findViewById(R.id.btn_mesas);
        btn_mesas.setVisibility(View.VISIBLE);
        btn_mesas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this,
                        Mesas.class);
                startActivity(i);
            }
        });
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
                        break;
                    case 1:
                        tvTitulo.setText("Venta");
                        break;
                    case 2:
                        tvTitulo.setText("Inventario");
                        getInsumos();
                        break;
                    case 3:
                        tvTitulo.setText("Gastos");
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String hoy = sdf.format(new Date());
                        tvFechaIndicadoresI = page0.findViewById(R.id.tvFechaI);
                        tvFechaIndicadoresI.setText(hoy);
                        tvFechaIndicadoresI.setOnClickListener(v -> {
                            obtenerFecha(tvFechaIndicadoresI);
                        });
                        tvFechaIndicadoresF = page0.findViewById(R.id.tvFechaF);
                        tvFechaIndicadoresF.setText(hoy);
                        tvFechaIndicadoresF.setOnClickListener(v -> {
                            obtenerFecha(tvFechaIndicadoresF);
                        });
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
                        tv_total_gasto_fijo = page0.findViewById(R.id.tv_total_gasto_fijo);
                        tv_total_gasto_personal = page0.findViewById(R.id.tv_total_gasto_personal);
                        tv_total_gasto_cocina = page0.findViewById(R.id.tv_total_gasto_cocina);
                        tv_total_gasto_barra = page0.findViewById(R.id.tv_total_gasto_barra);
                        tv_total_gastos = page0.findViewById(R.id.tv_total_gastos);
                        tv_total_venta_barra = page0.findViewById(R.id.tv_total_venta_barra);
                        tv_total_venta_cocina = page0.findViewById(R.id.tv_total_venta_cocina);
                        tv_total_venta = page0.findViewById(R.id.tv_total_venta);
                        getSucursales(adapterSucursales1);
                        getIndicadores(tvFechaIndicadoresI.getText().toString(), tvFechaIndicadoresF.getText().toString(), sucursalSelected);
                        btnFiltro = page0.findViewById(R.id.btnFiltro);
                        btnFiltro.setOnClickListener(v -> getIndicadores(tvFechaIndicadoresI.getText().toString(), tvFechaIndicadoresF.getText().toString(), sucursalSelected));
                    }
                    page = page0;
                    break;
                case 1:
                    if (page1 == null) {
                        page1 = (FrameLayout) LayoutInflater.from(Home.this).inflate(R.layout.vp_ventas, null);
                        tvTitulo.setText("Venta");
                        adapterSucursales2 = new AdapterSucursales(Home.this, android.R.layout.simple_spinner_item, listaSucursales);
                        spSucursal2 = page1.findViewById(R.id.spSucursal);
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String hoy = sdf.format(new Date());
                        tvFechaVentaI = page1.findViewById(R.id.tvFechaI);
                        tvFechaVentaI.setText(hoy);
                        tvFechaVentaI.setOnClickListener(v -> obtenerFecha(tvFechaVentaI));
                        tvFechaVentaF = page1.findViewById(R.id.tvFechaF);
                        tvFechaVentaF.setText(hoy);
                        tvFechaVentaF.setOnClickListener(v -> obtenerFecha(tvFechaVentaF));
                        brnVPagado = page1.findViewById(R.id.brnVPagado);
                        brnVPagado.setOnClickListener(v -> cargarVenta("1"));
                        btnVNoPagado = page1.findViewById(R.id.btnVNoPagado);
                        btnVNoPagado.setOnClickListener(v -> cargarVenta("0"));
                        grafica = page1.findViewById(R.id.grafica1);
                        nombreMarca = new ArrayList<>();
                        fechaVenta = new ArrayList<>();
                        barChart = page1.findViewById(R.id.grafica2);
                        getSucursales(adapterSucursales2);
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
                        getSucursales(adapterSucursales3);
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String hoy = sdf.format(new Date());
                        tvFechaGastoI = page3.findViewById(R.id.tvFechaI);
                        tvFechaGastoI.setText(hoy);
                        tvFechaGastoI.setOnClickListener(v -> obtenerFecha(tvFechaGastoI));
                        tvFechaGastoF = page3.findViewById(R.id.tvFechaF);
                        tvFechaGastoF.setText(hoy);
                        tvFechaGastoF.setOnClickListener(v -> obtenerFecha(tvFechaGastoF));
                        getGastos(tvFechaGastoI.getText().toString(), tvFechaGastoF.getText().toString(), "1", "2");
                        btnGFijos = page3.findViewById(R.id.btnGFijos);
                        btnGFijos.setOnClickListener(v -> cargarGastos("1"));
                        btnGPersonal = page3.findViewById(R.id.btnGPersonal);
                        btnGPersonal.setOnClickListener(v -> cargarGastos("2"));
                        btnGCocina = page3.findViewById(R.id.btnGCocina);
                        btnGCocina.setOnClickListener(v -> cargarGastos("3"));
                        btnGBarra = page3.findViewById(R.id.btnGBarra);
                        btnGBarra.setOnClickListener(v -> cargarGastos("4"));
                        adapterSucursales4 = new AdapterSucursales(Home.this, android.R.layout.simple_spinner_item, listaSucursales);
                        spSucursal4 = page3.findViewById(R.id.spSucursal);
                        spSucursal4.setAdapter(adapterSucursales4);
                        spSucursal4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                sucursalSelected = listaSucursales.get(spSucursal4.getSelectedItemPosition()).id;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        getSucursales(adapterSucursales4);
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
        getGastos(tvFechaGastoI.getText().toString(), tvFechaGastoF.getText().toString(), tipo, sucursalSelected);
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
        getVentas(tvFechaVentaI.getText().toString(), tvFechaVentaF.getText().toString(), pagado, sucursalSelected);
        getVentasArea(tvFechaVentaI.getText().toString(), tvFechaVentaF.getText().toString(), pagado, sucursalSelected);
    }

    public void getGastos(String fechaInicio, String fechaFin, String tipo, String sucursal) {
        progress.show();
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
                                progress.dismiss();
                                return;
                            } catch (Exception e) {
                                Log.e("err", e.toString());
                                progress.dismiss();
                            }
                        } else {
                            progress.dismiss();
                            Toast.makeText(Home.this, "Gastos.- Sin datos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progress.dismiss();
                        Toast.makeText(Home.this, "Gastos.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    progress.dismiss();
                    Toast.makeText(Home.this, "Gastos.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    progress.dismiss();
                    Toast.makeText(Home.this, "Gastos.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Home.this, "Gastos.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getInventario(String insumo, String area, String sucursal) {
        progress.show();
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
                            Toast.makeText(Home.this, "Inventario.- Sin datos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progress.dismiss();
                        Toast.makeText(Home.this, "Inventario.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    progress.dismiss();
                    Toast.makeText(Home.this, "Inventario.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    progress.dismiss();
                    Toast.makeText(Home.this, "Inventario.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Home.this, "Inventario.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

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
                            Toast.makeText(Home.this, "Home.- Ocurrio un error", Toast.LENGTH_LONG).show();
                        }
                        progress.dismiss();
                        return;
                    }
                }
                progress.dismiss();
                Toast.makeText(Home.this, "2.- Home.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Home.this, "Home.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getInsumos() {
        progress.show();
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
                                progress.dismiss();
                            }
                        } else {
                            progress.dismiss();
                            Toast.makeText(Home.this, "Home.- Ocurrio un error", Toast.LENGTH_LONG).show();
                        }
                        progress.dismiss();
                        return;
                    }
                }
                progress.dismiss();
                Toast.makeText(Home.this, "2.- Home.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Home.this, "Home.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getVentas(String fechaI, String fechaF, String pagado, String sucursal) {
        Log.e("Respuesta ->", "entra a get ventas");
        progress.show();
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getVentasApi(sesion.getToken(), fechaI, fechaF, pagado, sucursal);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.e("Respuesta ->", response + "");
                if (response.code() >= 200 && response.code() < 300) {

                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                listaVentas.clear();
                                nombreMarca.clear();
                                grafica.removeAllViews();
                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response.body() + "");
                                    Log.e("Respuesta ->", jsonResponse + "");
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
                                    progress.dismiss();
                                }
                                progress.dismiss();
                                return;
                            } catch (Exception e) {
                                Log.e("err", e.toString());
                                progress.dismiss();
                            }
                        } else {
                            Toast.makeText(Home.this, "Venta.- Sin datos", Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                    } else {
                        progress.dismiss();
                        Toast.makeText(Home.this, "Venta.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    progress.dismiss();
                    Toast.makeText(Home.this, "Venta.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    progress.dismiss();
                    Toast.makeText(Home.this, "Venta.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Home.this, "Venta.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getVentasArea(String fechaI, String fechaF, String pagado, String sucursal) {
        progress.show();
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getVentasDivididasApi(sesion.getToken(), fechaI, fechaF, pagado, sucursal);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() >= 200 && response.code() < 300) {

                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            try {
                                listaVBarraCocinas.clear();
                                fechaVenta.clear();
                                barChart.removeAllViews();
                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response.body() + "");
                                    Log.e("Respuesta", jsonResponse + "");
                                    JSONArray data = jsonResponse.getJSONArray("totales");
                                    for (int i = 0; i < data.length(); i++) {
                                        String fecha = data.getJSONObject(i).getString("fecha");
                                        String totalCocina = data.getJSONObject(i).getString("totalVentasCocina");
                                        String totalBarra = data.getJSONObject(i).getString("totalVentasBarra");
                                        listaVBarraCocinas.add(new ListaVBarraCocina(fecha, totalBarra, totalCocina));
                                        fechaVenta.add(fecha);
                                    }
                                    Log.e("Respuesta", listaVBarraCocinas.size() + " .- lista de ventas por area");
                                    addDataSet2();
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
                            Toast.makeText(Home.this, "Venta.- Sin datos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progress.dismiss();
                        Toast.makeText(Home.this, "Venta.- Ocurrio un error", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 400) {
                    progress.dismiss();
                    Toast.makeText(Home.this, "Venta.- Sin datos", Toast.LENGTH_LONG).show();
                } else {
                    progress.dismiss();
                    Toast.makeText(Home.this, "Venta.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Home.this, "Venta.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getIndicadores(String fechaI,String fechaF, String sucursal) {
        progress.show();
        Call<JsonObject> serviceDownload = retrofit.create(UrlInterface.class).getTotalesApi(sesion.getToken(), fechaI, fechaF, sucursal);
        serviceDownload.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() >= 200 && response.code() < 300) {
                    if (response.body() != null && response.body().has("success")) {
                        if (response.body().get("success").getAsInt() == 1) {
                            Log.e("Respuesta", response.body() + "");
                            float totalVentasBarra = response.body().get("totalVentasBarra").getAsFloat();
                            float totalVentasCocina = response.body().get("totalVentasCocina").getAsFloat();
                            float totalGastoFijo = response.body().get("totalGastoFijo").getAsFloat();
                            float totalGastoPersonal = response.body().get("totalGastoPersonal").getAsFloat();
                            float totalGastoCocina = response.body().get("totalGastoCocina").getAsFloat();
                            float totalGastoBarra = response.body().get("totalGastoBarra").getAsFloat();
                            tv_total_gasto_fijo.setText("$" + totalGastoFijo + "");
                            tv_total_gasto_personal.setText("$" + totalGastoPersonal + "");
                            tv_total_gasto_cocina.setText("$" + totalGastoCocina + "");
                            tv_total_gasto_barra.setText("$" + totalGastoBarra + "");
                            float totalGastos = (totalGastoFijo + totalGastoPersonal + totalGastoCocina + totalGastoBarra);
                            tv_total_gastos.setText("Gastos totales $" + totalGastos + "");
                            tv_total_venta_barra.setText("$" + totalVentasBarra + "");
                            tv_total_venta_cocina.setText("$" + totalVentasCocina + "");
                            float totalVentas = (totalVentasBarra + totalVentasCocina);
                            tv_total_venta.setText("Ventas totales $" + totalVentas + "");
                        } else {
                            tv_total_gasto_fijo.setText("$0.0");
                            tv_total_gasto_personal.setText("$0.0");
                            tv_total_gasto_cocina.setText("$0.0");
                            tv_total_gasto_barra.setText("$0.0");
                            tv_total_gastos.setText("Gastos totales $0.0");
                            tv_total_venta_barra.setText("$0.0");
                            tv_total_venta_cocina.setText("$0.0");
                            tv_total_venta.setText("Ventas totales $0.0");
                        }
                        progress.dismiss();
                        return;
                    }
                }
                progress.dismiss();
                Toast.makeText(Home.this, "2.- Home.- No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Toast.makeText(Home.this, "Home.- No se pudo conectar con el servidor, revise su conexion a internet", Toast.LENGTH_LONG).show();
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
        Log.e("Res", "entra " + listaVentasDias.size());
        grafica.setMaxVisibleValueCount(listaVentasDias.size());
        ArrayList<BarEntry> precio = new ArrayList<>();
        for (int i = 0; i < listaVentasDias.size(); i++) {
            precios = new float[listaVentasDias.size()];
            float entrada = Float.parseFloat("" + listaVentasDias.get(i).monto_total);
            precios[i] = entrada;
            precio.add(new BarEntry(i, entrada));
        }

        BarDataSet dataSet = new BarDataSet(precio, "Día de Venta");

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
        xaxis.setDrawGridLines(true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularity(1f);
        xaxis.setDrawLabels(true);
        xaxis.setDrawAxisLine(true);
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

    public void addDataSet2() {
        barChart.setDrawBarShadow(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription(null);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(true);

        XAxis xaxis = barChart.getXAxis();
        xaxis.setDrawGridLines(true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularity(0.68f);
        xaxis.setDrawLabels(true);
        xaxis.setCenterAxisLabels(false);
        xaxis.setDrawAxisLine(true);
        xaxis.setValueFormatter(new IndexAxisValueFormatter(fechaVenta));
        xaxis.setGridColor(R.color.gris);
        xaxis.setTextColor(R.color.gris);

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
        float barWidth = 0.30f; // x2 dataset

        List<BarEntry> yVals1 = new ArrayList<>();
        List<BarEntry> yVals2 = new ArrayList<>();


        for (int i = 0; i < listaVBarraCocinas.size(); i++) {
            yVals1.add(new BarEntry(i, Float.parseFloat(listaVBarraCocinas.get(i).totalCocina)));
            yVals2.add(new BarEntry(i, Float.parseFloat(listaVBarraCocinas.get(i).totalBarra)));
        }

        BarDataSet set1, set2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
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
        barChart.getXAxis().setAxisMinValue(0);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate();
    }
}
