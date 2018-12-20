package com.example.acer.waybus.MisParadas;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.acer.waybus.R;

/**
 * Actividad de la vista de Mis Paradas
 *
 */
public class viewMisParadas extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_view_paradas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarPrincipal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_main_favourite2);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment_MisParadas fragment_misParadas = new Fragment_MisParadas();
        fragmentTransaction.add(R.id.coordFavs, fragment_misParadas);

        fragmentTransaction.commit();
    }
}
