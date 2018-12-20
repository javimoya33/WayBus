package com.example.acer.waybus.Avisos;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.acer.waybus.R;

/**
 * Actividad de la vista de Alertas
 *
 */
public class viewAvisos extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avi_view_avisos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarPrincipal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_main_alert2);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment_Avisos fragment_avisos = new Fragment_Avisos();
        fragmentTransaction.add(R.id.coordAvisos, fragment_avisos);

        fragmentTransaction.commit();
    }
}
