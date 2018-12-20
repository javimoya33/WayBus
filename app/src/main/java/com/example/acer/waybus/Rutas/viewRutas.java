package com.example.acer.waybus.Rutas;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.acer.waybus.R;

/**
 * Actividad de la vista de Rutas
 *
 * Generación de un viewpager donde se definen tres pestañas
 */
public class viewRutas extends AppCompatActivity{

    private Adaptador_ViewRutas adaptador_ViewLineas;
    private ViewPager viewPager;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lineas_view_lineas);

        //Iniciamos la barra de herramientas
        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarPrincipal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_main_route2);

        //Iniciamos la barra de tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.TabLayoutPrincipal);

        //Añadimos los 3 tabs de las secciones
        //Aplicamos propiedades para que todas las pestañas tengan el mismo tamaño y esten centradas
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        //Iniciamos el viewPager
        viewPager = (ViewPager) findViewById(R.id.ViewPagerPrincipal);

        //Creamos el adaptador, al cual le pasamos por parámetro el gestor de Fragmentos y el número de tabs que hemos creado
        adaptador_ViewLineas = new Adaptador_ViewRutas(getSupportFragmentManager(), tabLayout.getTabCount());

        //Y los vinculamos
        viewPager.setAdapter(adaptador_ViewLineas);

        //Vinculamos el ViewPager con el control de tabs para sincronizar ambos
        tabLayout.setupWithViewPager(viewPager);
    }
}
