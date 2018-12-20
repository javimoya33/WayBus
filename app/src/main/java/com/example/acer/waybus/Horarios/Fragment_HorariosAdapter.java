package com.example.acer.waybus.Horarios;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.R;

import java.util.List;

/**
 * Adaptador del recycler view de Rutas.
 *
 * Se asocia a cada elemento de la vista los valores recibidos en la consulta a la base de datos.
 */
public class Fragment_HorariosAdapter extends RecyclerView.Adapter<Fragment_HorariosAdapter.HorarioViewHolder> {

    /**
     * Lista de objetos {@link Horario} que representa la fuente de datos de inflado
     */
    private List<Horario> itemsHorario;

    // Contexto donde actua el recycler view
    private Context context;

    private Fragment_Horarios fragment_horarios = new Fragment_Horarios();

    /**
     *
     * Constructor de la clase
     *
     * @param items -> Lista de objetos con la fuente de datos de la tabla 'Horarios'
     * @param cntxt -> Contexto de la aplicación
     */
    public Fragment_HorariosAdapter(List<Horario> items, Context cntxt)
    {
        this.context = cntxt;
        this.itemsHorario = items;
    }

    /**
     *
     * @param parent -> Controla la lista de views de la aplicación
     * @param viewType
     * @return -> Devuelve el layout asociado al adaptador
     */
    @Override
    public Fragment_HorariosAdapter.HorarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hor_table_horarios, parent, false);
        return new HorarioViewHolder(v);
    }

    /**
     *
     * @param holder -> Soporte de la vista
     * @param position -> posicion concreta del holder
     */
    @Override
    public void onBindViewHolder(Fragment_HorariosAdapter.HorarioViewHolder holder, int position)
    {
        String subcadenaHoraSalida = itemsHorario.get(position).getHoraSalida().substring(0,5);
        String subcadenaHoraLlegada = itemsHorario.get(position).getHoraLlegada().substring(0,5);
        String subcadenaDuracion = itemsHorario.get(position).getDuracion().substring(0,5);

        holder.recHoraSalidaLlegada.setText(subcadenaHoraSalida + " - " + subcadenaHoraLlegada);
        holder.recDuracion.setText(subcadenaDuracion);
        holder.recPrecio.setText(itemsHorario.get(position).getPrecio() + "€");
        holder.recDiasSemana.setText(itemsHorario.get(position).getDiasSemana());
    }

    /**
     *
     * @return Devuelve el tamaño de la lista de horarios
     */
    @Override
    public int getItemCount() {

        return itemsHorario.size();
    }

    public class HorarioViewHolder extends RecyclerView.ViewHolder
    {
        // Campos respectivos de un item
        public TextView recHoraSalidaLlegada;
        public TextView recDuracion;
        public TextView recPrecio;
        public TextView recDiasSemana;

        public HorarioViewHolder(View itemView) {
            super(itemView);

            recHoraSalidaLlegada = (TextView) itemView.findViewById(R.id.recyclerHoraSalidaLlegada);
            recDuracion = (TextView) itemView.findViewById(R.id.recyclerDuracion);
            recPrecio = (TextView) itemView.findViewById(R.id.recyclerPrecio);
            recDiasSemana = (TextView) itemView.findViewById(R.id.recyclerDiasSemana);
        }
    }
}
