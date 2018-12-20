package com.example.acer.waybus.Rutas;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 *
 * Adaptador donde se referencian los diferentes fragmentos de la vista dispuestos por pestañas.
 */
public class Adaptador_ViewRutas extends FragmentPagerAdapter {

    int numSecciones;

    public Adaptador_ViewRutas(FragmentManager fm, int numeroSecciones){
        super(fm);
        this.numSecciones = numeroSecciones;
    }

    /**
     *
     * @param position -> Posición de la pestaña donde está posicionado el usuario actualmente.
     * @return -> Devuelve el fragmento asociado a cada pestaña.
     */
    public Fragment getItem(int position)
    {
        switch (position){

            case 0: //Siempre empieza desde 0
                return new Fragment_seccion2();
            case 1:
                return new Fragment_seccion3();
            case 2:
                return new Fragment_seccion1();

            default:
                return null;
        }
    }

    /**
     *
     * @param position -> Posición de la pestaña donde está posicionado el usuario actualmente.
     * @return -> Devuelve el título asociado a cada pestaña
     */
    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "Mi Zona";
            case 1:
                return "Próximas";
            case 2:
                return "Todas";

            default:
                return null;
        }
    }

    /**
     *
     * @return -> Devuelve el numero de secciones de la vista.
     */
    @Override
    public int getCount() {

        return numSecciones;
    }
}
