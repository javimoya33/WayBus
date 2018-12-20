package com.example.acer.waybus;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.acer.waybus.Avisos.viewAvisos;
import com.example.acer.waybus.Horarios.viewHorarios;
import com.example.acer.waybus.Rutas.viewRutas;
import com.example.acer.waybus.MisParadas.viewMisParadas;
import com.example.acer.waybus.Plano.Activity_Plano;


/**
 * Actividad principal de la clase.
 *
 * Se referencia los diferentes botones que componen el la vista principal de la aplicación.
 *
 * Se invoca las diferentes actividades de las que se compone la aplicación.
 *
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button btnLineas;
    private Button btnHorarios;
    private Button btnMisParadas;
    private Button btnAvisos;
    private Button btnPlano;

    Typeface fontPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarPrincipal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_waybus);

        fontPrincipal = Typeface.createFromAsset(getApplicationContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");

        btnLineas = (Button) findViewById(R.id.btnRutas);
        btnLineas.setTypeface(fontPrincipal);
        btnLineas.setOnClickListener(this);

        btnHorarios = (Button) findViewById(R.id.btnHorarios);
        btnHorarios.setTypeface(fontPrincipal);
        btnHorarios.setOnClickListener(this);

        btnMisParadas = (Button) findViewById(R.id.btnMisParadas);
        btnMisParadas.setTypeface(fontPrincipal);
        btnMisParadas.setOnClickListener(this);

        btnAvisos = (Button) findViewById(R.id.btnAlertas);
        btnAvisos.setTypeface(fontPrincipal);
        btnAvisos.setOnClickListener(this);

        btnPlano = (Button) findViewById(R.id.btnPlano);
        btnPlano.setTypeface(fontPrincipal);
        btnPlano.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnRutas:
                Intent intentViewRutas = new Intent(this, viewRutas.class);
                startActivity(intentViewRutas);
                break;
            case R.id.btnHorarios:
                Intent intentViewHorarios = new Intent(this, viewHorarios.class);
                startActivity(intentViewHorarios);
                break;
            case R.id.btnMisParadas:
                Intent intentViewMisParadas = new Intent(this, viewMisParadas.class);
                startActivity(intentViewMisParadas);
                break;
            case R.id.btnAlertas:
                Intent intentViewAvisos = new Intent(this, viewAvisos.class);
                startActivity(intentViewAvisos);
                break;
            case R.id.btnPlano:
                Intent intentViewPlano = new Intent(this, Activity_Plano.class);
                startActivity(intentViewPlano);
                break;
        }
    }
}
