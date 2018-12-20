package com.example.acer.waybus.Horarios;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.acer.waybus.R;

/**
 * Actividad de la vista de Horarios
 *
 */
public class viewHorarios extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hor_view_horarios);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarPrincipal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_main_timetable2);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment_Horarios fragment = new Fragment_Horarios();
        fragmentTransaction.add(R.id.CoordinadorLayout, fragment);

        fragmentTransaction.commit();
    }
}
