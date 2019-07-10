package mx.aqtiva.cantinazo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.global.BaseActivity;
import mx.aqtiva.cantinazo.global.NonSwipeableViewPager;
import mx.aqtiva.cantinazo.global.Sesion;

public class Home extends BaseActivity {
    Sesion sesion;
    public static NonSwipeableViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    FrameLayout page0, page1, page2, page3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sesion = new Sesion(this);
        tvTitulo = findViewById(R.id.tv_titulo);
        btn_accion = findViewById(R.id.btn_accion);
        btn_menu = findViewById(R.id.btn_menu);
        tvTitulo.setText("Venta");
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
                    break;
                case R.id.navigation_ventas:
                    break;
                case R.id.navigation_inventario:
                    break;
                case R.id.navigation_gastos:
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
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
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

                    }
                    page = page0;
                    break;
                case 1:
                    if (page1 == null) {
                        page1 = (FrameLayout) LayoutInflater.from(Home.this).inflate(R.layout.vp_ventas, null);

                    }
                    page = page1;
                    break;
                case 2:
                    if (page2 == null) {
                        page2 = (FrameLayout) LayoutInflater.from(Home.this).inflate(R.layout.vp_inventario, null);

                    }
                    page = page2;
                    break;

                case 3:
                    if (page3 == null) {
                        page3 = (FrameLayout) LayoutInflater.from(Home.this).inflate(R.layout.vp_gastos, null);

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
}
