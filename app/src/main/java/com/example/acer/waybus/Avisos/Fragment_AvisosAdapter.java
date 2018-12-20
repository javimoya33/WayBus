package com.example.acer.waybus.Avisos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.acer.waybus.Rutas.viewRutasDetalle;
import com.example.acer.waybus.Modelo.Aviso;
import com.example.acer.waybus.Modelo.Estacion;
import com.example.acer.waybus.Modelo.Horario;

import com.example.acer.waybus.Modelo.Linea;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.R;
import com.example.acer.waybus.Adaptadores.*;
import com.example.acer.waybus.tools.Constantes;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador del recycler view de Avisos.
 *
 * Se asocia a cada elemento de la vista los valores recibidos en la consulta a la base de datos.
 * Gestiona las posibles actualizaciones referentes al envio de un aviso
 */
public class Fragment_AvisosAdapter extends RecyclerView.Adapter<Fragment_AvisosAdapter.HorarioViewHolder> {

    /* Etiqueta de depuración */
    private static final String TAG = Fragment_AvisosAdapter.class.getSimpleName();

    /* Lista de objetos {@link} que representa la fuente de datos de inflado */
    private List<Estacion> itemsEstacion;
    private List<Horario> itemsHorario;
    private List<Linea> itemsLinea;
    private List<Ruta> itemsRuta;
    private List<Aviso> itemsAviso;
    private List<Usuario> itemsUsuario;

    Typeface fontPrincipal;

    /* Contexto donde actua el recycler view */
    private Context context;
    private Activity activity;

    private cargaAviso actualizacion = new cargaAviso();

    private String valorHoraAviso;
    private String valorRepAviso;

    /**
     *
     * @param iEstacion -> Lista de datos de la tabla 'Estaciones'
     * @param iLinea -> Lista de datos de la tabla 'Linea'
     * @param iRuta -> Lista de datos de la tabla 'Ruta'
     * @param iHorario -> Lista de datos de la tabla 'Horario'
     * @param iAviso -> Lista de datos de la tabla 'Aviso'
     * @param iUsuario -> Lista de datos de la tabla 'Usuarios'
     * @param cntxt -> Contexto de la aplicación
     * @param act -> Actividad de la aplicación
     */
    public  Fragment_AvisosAdapter(List<Estacion> iEstacion, List<Linea> iLinea, List<Ruta> iRuta,
                                   List<Horario> iHorario, List<Aviso> iAviso, List<Usuario> iUsuario,
                                   Context cntxt, Activity act)
    {
        this.itemsEstacion = iEstacion;
        this.itemsLinea = iLinea;
        this.itemsRuta = iRuta;
        this.itemsHorario = iHorario;
        this.itemsAviso = iAviso;
        this.itemsUsuario = iUsuario;
        this.context = cntxt;
        this.activity = act;
    }

    /**
     *
     * @param parent -> Controla la lista de views de la aplicación
     * @param viewType
     * @return -> Devuelve el layout asociado al adaptador
     */
    @Override
    public HorarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.avi_table_avisos, parent, false);

        return new HorarioViewHolder(v);
    }

    /**
     *
     * @param holder -> Soporte de la vista
     * @param position -> posicion concreta del holder
     */
    @Override
    public void onBindViewHolder(HorarioViewHolder holder, final int position) {

        final int finalPosicion = position;

        fontPrincipal = Typeface.createFromAsset(context.getAssets(),  "fonts/Atlantic_Cruise.ttf");

        holder.tvNomLinea.setText(itemsLinea.get(position).getNumBus());
        holder.tvNomLinea.setTypeface(fontPrincipal);

        // Si el nombre de la ruta es demasiado largo se sustituirá por puntos suspensivos
        final int cadenaRuta = (itemsRuta.get(position).getOrigen()
                + " - " + itemsRuta.get(position).getDestino()).length();
        if (cadenaRuta < 23)
        {
            int resto = 23 - cadenaRuta;
            String espacio = "";

            for (int i = 1; i <= resto; i++)
            {
                espacio += " ";
            }

            holder.tvNomRuta.setText(itemsRuta.get(position).getOrigen()
                    + " - " + itemsRuta.get(position).getDestino() + espacio);
        }
        else if (cadenaRuta > 23)
        {
            String txtRuta = itemsRuta.get(position).getOrigen()
                    + " - " + itemsRuta.get(position).getDestino();

            String subcadenaRuta = txtRuta.substring(21, txtRuta.length());
            String nuevaTxtRuta = txtRuta.replace(subcadenaRuta, "...");

            holder.tvNomRuta.setText(nuevaTxtRuta);
        }
        else
        {
            holder.tvNomRuta.setText(itemsRuta.get(position).getOrigen()
                    + " - " + itemsRuta.get(position).getDestino());
        }

        holder.tvNomRuta.setTypeface(fontPrincipal);

        holder.tvNomEstacion.setText(itemsEstacion.get(position).getNombre());
        holder.tvNomEstacion.setTypeface(fontPrincipal);

        final String subcadenaHoraSalida = itemsHorario.get(position).getHoraSalida().substring(0,5);
        String subcadenaHoraLlegada = itemsHorario.get(position).getHoraLlegada().substring(0,5);
        holder.tvHoraRuta.setText(subcadenaHoraSalida + " - " + subcadenaHoraLlegada);
        holder.tvHoraRuta.setTypeface(fontPrincipal);

        // Creación del spinner con las opciones de frecuencia de los avisos
        ArrayList<String> frecAvisos = new ArrayList<>();
        frecAvisos.add("Sólo una vez");
        frecAvisos.add("Diariamente");
        frecAvisos.add("Semanalmente");

        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(context, R.layout.simple_spinner, frecAvisos)
                {
                    public View getView(int position, View convertView, android.view.ViewGroup parent)
                    {
                        fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
                        TextView textSpinnerFrecuencia = (TextView) super.getView(position, convertView, parent);
                        textSpinnerFrecuencia.setTypeface(fontPrincipal);
                        return textSpinnerFrecuencia;
                    }

                    public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {

                        fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
                        TextView textSpinnerFrecuencia = (TextView) super.getView(position, convertView, parent);
                        textSpinnerFrecuencia.setTypeface(fontPrincipal);
                        textSpinnerFrecuencia.setTextSize(18);
                        return textSpinnerFrecuencia;
                    }
                };
        adapter1.setDropDownViewResource(R.layout.simple_spinner);
        holder.spinRepeticion.setAdapter(adapter1);
        int defaultValueSpinnerRep = adapter1.getPosition(
                itemsAviso.get(position).getRepeticion()
        );

        holder.spinRepeticion.setSelection(defaultValueSpinnerRep);
        holder.spinRepeticion.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        actualizacion.actualizarAviso(String.valueOf(parent.getItemAtPosition(pos)),
                                itemsAviso.get(finalPosicion).getIdAviso(), Constantes.UPDATE_FREC_AVISO,
                                TAG, context, activity);

                        int horAvisoFinal = Integer.parseInt(subcadenaHoraSalida.toString().substring(0, 2));
                        int minAvisoFinal = Integer.parseInt(subcadenaHoraSalida.toString().substring(3, 5));

                        valorRepAviso = itemsAviso.get(finalPosicion).getRepeticion();
                        valorHoraAviso = itemsAviso.get(finalPosicion).getNotificacion();

                        // Si modificamos el valor del spinner se actualizara el aviso para que se lance en el momento
                        // que más conviene al usuario
                        if (!valorRepAviso.equals(String.valueOf(parent.getItemAtPosition(pos))))
                        {
                            if (valorHoraAviso.equals("5min."))
                            {
                                actualizacion.enviarAviso(horAvisoFinal, minAvisoFinal, 5,
                                        String.valueOf(parent.getItemAtPosition(pos)), context, itemsAviso.get(position).getIdAviso());
                                valorRepAviso = String.valueOf(parent.getItemAtPosition(pos));
                            }
                            else if (valorHoraAviso.equals("10min."))
                            {
                                actualizacion.enviarAviso(horAvisoFinal, minAvisoFinal, 10,
                                        String.valueOf(parent.getItemAtPosition(pos)), context, itemsAviso.get(position).getIdAviso());
                                valorRepAviso = String.valueOf(parent.getItemAtPosition(pos));
                            }
                            else if (valorHoraAviso.equals("30min."))
                            {
                                actualizacion.enviarAviso(horAvisoFinal, minAvisoFinal, 30,
                                        String.valueOf(parent.getItemAtPosition(pos)), context, itemsAviso.get(position).getIdAviso());
                                valorRepAviso = String.valueOf(parent.getItemAtPosition(pos));
                            }
                            else if (valorHoraAviso.equals("1hora"))
                            {
                                actualizacion.enviarAvisoEspecial(horAvisoFinal, minAvisoFinal,
                                        String.valueOf(parent.getItemAtPosition(pos)), context, itemsAviso.get(position).getIdAviso());
                                valorRepAviso = String.valueOf(parent.getItemAtPosition(pos));
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        // Creación del spinner con las opciones de hora a la que queremos que se lance el aviso
        ArrayList<String> frecHoraAviso = new ArrayList<>();
        frecHoraAviso.add("5min.");
        frecHoraAviso.add("10min.");
        frecHoraAviso.add("30min.");
        frecHoraAviso.add("1hora");

        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<String>(context, R.layout.simple_spinner, frecHoraAviso)
                {
                    public View getView(int position, View convertView, android.view.ViewGroup parent)
                    {
                        fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
                        TextView textSpinnerHoraAviso = (TextView) super.getView(position, convertView, parent);
                        textSpinnerHoraAviso.setTypeface(fontPrincipal);
                        return textSpinnerHoraAviso;
                    }

                    public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {

                        fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
                        TextView textSpinnerHoraAviso = (TextView) super.getView(position, convertView, parent);
                        textSpinnerHoraAviso.setTypeface(fontPrincipal);
                        textSpinnerHoraAviso.setTextSize(18);
                        return textSpinnerHoraAviso;
                    }
                };
        adapter2.setDropDownViewResource(R.layout.simple_spinner);

        holder.spinHoraAviso.setAdapter(adapter2);
        int defaultValueSpinnerHora = adapter2.getPosition(
                itemsAviso.get(position).getNotificacion());
        holder.spinHoraAviso.setSelection(defaultValueSpinnerHora);
        holder.spinHoraAviso.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        actualizacion.actualizarAviso(String.valueOf(parent.getItemAtPosition(pos)),
                                itemsAviso.get(finalPosicion).getIdAviso(), Constantes.UPDATE_HORA_AVISO,
                                TAG, context, activity);

                        int horAvisoFinal = Integer.parseInt(subcadenaHoraSalida.toString().substring(0, 2));
                        int minAvisoFinal = Integer.parseInt(subcadenaHoraSalida.toString().substring(3, 5));

                        valorRepAviso = itemsAviso.get(finalPosicion).getRepeticion();
                        valorHoraAviso = itemsAviso.get(finalPosicion).getNotificacion();


                        // El aviso será enviado siempre que el valor del spinner haya sido modificado con respecto
                        // al valor inicial
                        if (!valorHoraAviso.equals(String.valueOf(parent.getItemAtPosition(pos))))
                        {
                            if (pos == 0)
                            {
                                actualizacion.enviarAviso(horAvisoFinal, minAvisoFinal, 5, valorRepAviso, context,
                                        itemsAviso.get(position).getIdAviso());
                                valorHoraAviso = String.valueOf(parent.getItemAtPosition(pos));
                            }
                            else if (pos == 1)
                            {
                                actualizacion.enviarAviso(horAvisoFinal, minAvisoFinal, 10, valorRepAviso, context,
                                        itemsAviso.get(position).getIdAviso());
                                valorHoraAviso = String.valueOf(parent.getItemAtPosition(pos));
                            }
                            else if (pos == 2)
                            {
                                actualizacion.enviarAviso(horAvisoFinal, minAvisoFinal, 30, valorRepAviso, context,
                                        itemsAviso.get(position).getIdAviso());
                                valorHoraAviso = String.valueOf(parent.getItemAtPosition(pos));
                            }
                            else if (pos == 3)
                            {
                                actualizacion.enviarAvisoEspecial(horAvisoFinal, minAvisoFinal, valorRepAviso, context,
                                        itemsAviso.get(position).getIdAviso());
                                valorHoraAviso = String.valueOf(parent.getItemAtPosition(pos));
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        holder.butDetalle.setOnClickListener(new View.OnClickListener() {

            /**
             * Evento que se lanzará al clickar sobre el botón que nos llevará a la vista de detalle de la ruta
             *
             * @param v -> Vista actual
             */
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
                Intent intent = new Intent(context, viewRutasDetalle.class);
                intent.putExtra("TituloRuta", itemsRuta.get(position).getOrigen()
                        + " - " + itemsRuta.get(position).getDestino());
                intent.putExtra("NumLinea", itemsLinea.get(position).getNumBus());
                intent.putExtra("Modelo", itemsLinea.get(position).getModelo());
                intent.putExtra("Capacidad", itemsLinea.get(position).getCapacidad());
                intent.putExtra("NomEstacion", itemsEstacion.get(position).getNombre());
                intent.putExtra("CP", itemsEstacion.get(position).getCP());
                intent.putExtra("Telefono", itemsEstacion.get(position).getTelefono());
                intent.putExtra("IdRuta", itemsRuta.get(position).getIdRuta());
                context.startActivity(intent);
            }
        });
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
        public TextView tvNomEstacion;
        public TextView tvNomLinea;
        public TextView tvNomRuta;
        public TextView tvHoraRuta;
        public Spinner spinRepeticion;
        public Spinner spinHoraAviso;
        public ImageButton butDetalle;

        public ArrayAdapter<CharSequence> adapterSpinFrecuencia = ArrayAdapter.createFromResource(
                context, R.array.valores_frecuencia,
                R.layout.simple_spinner);

        public ArrayAdapter<CharSequence> adapterSpinHoraAviso = ArrayAdapter.createFromResource(
                context, R.array.valores_hora_aviso,
                R.layout.simple_spinner);

        public HorarioViewHolder(View itemView)
        {
            super(itemView);

            tvNomEstacion = (TextView) itemView.findViewById(R.id.nomEstacion);
            tvNomLinea = (TextView) itemView.findViewById(R.id.numLinea);
            tvNomRuta = (TextView) itemView.findViewById(R.id.nomRuta);
            tvHoraRuta = (TextView) itemView.findViewById(R.id.tabHorario);
            spinRepeticion = (Spinner) itemView.findViewById(R.id.spinAviFrecuencia);
            spinHoraAviso = (Spinner) itemView.findViewById(R.id.spinHoraAviso);
            butDetalle = (ImageButton) itemView.findViewById(R.id.butRutaDetalle);
        }
    }
}
