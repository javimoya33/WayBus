package com.example.acer.waybus.Rutas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.waybus.Adaptadores.eliminarFavorito;
import com.example.acer.waybus.Adaptadores.insertarFavorito;
import com.example.acer.waybus.Modelo.Estacion;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Linea;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.R;

import java.util.Calendar;
import java.util.List;

/**
 * Adaptador del recycler view de Próximas Rutas.
 *
 * Se asocia a cada elemento de la vista los valores recibidos en la consulta a la base de datos.
 */
public class Adaptador_ProxRutas extends RecyclerView.Adapter<Adaptador_ProxRutas.HorarioViewHolder> {

    /* Etiqueta de depuración */
    private static final String TAG = Adaptador_Rutas.class.getSimpleName();

    /* Lista de objetos {@link} que representa la fuente de datos de inflado */
    private List<Horario> itemsHorario;
    private List<Ruta> itemsRuta;
    private List<Linea> itemsLinea;
    private List<Estacion> itemsEstacion;
    private List<Usuario> itemsUsuario;

    /* Contexto donde actúa el recycler view */
    private Context context;

    private Activity activity;

    Typeface fontPrincipal;
    Typeface fontSecondary;

    private int[] tvFavoritos = new int[1000];

    private insertarFavorito insercion = new insertarFavorito();
    private eliminarFavorito eliminacion = new eliminarFavorito();

    /**
     *
     * Constructor de la clase
     *
     * @param itemRut -> Lista de objetos con la fuente de datos de la tabla 'Rutas'
     * @param itemHor -> Lista de objetos con la fuente de datos de la tabla 'Horarios'
     * @param itemLinea -> Lista de objetos con la fuente de datos de la tabla 'Lineas'
     * @param itemEstacion -> Lista de objetos con la fuente de datos de la tabla 'Estaciones'
     * @param cntxt -> Contexto de la aplicación
     */
    public Adaptador_ProxRutas(List<Horario> itemHor, List<Ruta> itemRut, List<Linea> itemLinea,
                               List<Estacion> itemEstacion, Context cntxt)
    {
        this.context = cntxt;
        this.itemsHorario = itemHor;
        this.itemsRuta = itemRut;
        this.itemsLinea = itemLinea;
        this.itemsEstacion = itemEstacion;
    }

    /**
     *
     * @param parent -> Controla la lista de views de la aplicación
     * @param viewType
     * @return -> Devuelve el layout asociado al adaptador
     */
    @Override
    public HorarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lineas_fragment_seccion3, parent, false);
        return new HorarioViewHolder(v);
    }

    /**
     *
     * @param holder -> Soporte de la vista
     * @param position -> posicion concreta del holder
     */
    @Override
    public void onBindViewHolder(final HorarioViewHolder holder, final int position) {

        fontPrincipal = Typeface.createFromAsset(context.getAssets(),  "fonts/Atlantic_Cruise.ttf");

        holder.tvNombreRuta.setTypeface(fontPrincipal);
        holder.tvNumLinea.setTypeface(fontPrincipal);
        holder.tvNomEstacion.setTypeface(fontPrincipal);

        // Si el nombre de la ruta es demasiado largo se sustituirá por puntos suspensivos
        int cadenaRuta = (itemsRuta.get(position).getOrigen()
                + " - " + itemsRuta.get(position).getDestino()).length();

        if (cadenaRuta < 23)
        {
            int resto = 23 - cadenaRuta;
            String espacio = "";

            for (int i = 1; i <= resto; i++)
            {
                espacio += " ";
            }

            holder.tvNombreRuta.setText(itemsRuta.get(position).getOrigen()
                    + " - " + itemsRuta.get(position).getDestino() + espacio);
        }
        else if (cadenaRuta > 23)
        {
            String txtRuta = itemsRuta.get(position).getOrigen()
                    + " - " + itemsRuta.get(position).getDestino();

            String subcadenaRuta = txtRuta.substring(21, txtRuta.length());
            String nuevaTxtRuta = txtRuta.replace(subcadenaRuta, "...");

            holder.tvNombreRuta.setText(nuevaTxtRuta);
        }
        else
        {
            holder.tvNombreRuta.setText(itemsRuta.get(position).getOrigen()
                    + " - " + itemsRuta.get(position).getDestino());
        }

        holder.tvNumLinea.setText(itemsLinea.get(position).getNumBus());
        holder.tvNomEstacion.setText(itemsEstacion.get(position).getNombre());

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
                intent.putExtra("NomEstacion", itemsEstacion.get(position).getNombre());
                intent.putExtra("IdRuta", itemsRuta.get(position).getIdRuta());
                context.startActivity(intent);
            }
        });

        // Si el id de la ruta aparece en el array de favoritos el checkbox asociado quedará seleccionado
        // de forma predeterminada
        if (tvFavoritos[position] == itemsRuta.get(position).getIdRuta())
        {
            holder.cbFavourite.setChecked(true);
        }
        else
        {
            holder.cbFavourite.setChecked(false);
        }


        holder.cbFavourite.setOnClickListener(new View.OnClickListener() {

            /**
             * Evento que se lanzará al pulsar sobre el checkbox que añadirá o eliminará la ruta de la tabla de favoritos
             *
             * @param v -> Vista actual
             */
            @Override
            public void onClick(View v) {

                if (holder.cbFavourite.isChecked())
                {
                    Toast.makeText(context, "La ruta queda marcada como favorita", Toast.LENGTH_SHORT).show();

                    insercion.insertarFavorito(
                            itemsRuta.get(position).getOrigen() + " - " + itemsRuta.get(position).getDestino(),
                            itemsRuta.get(position).getIdRuta(), itemsUsuario.get(0).getIdUsuario(),
                            TAG, context, activity);
                }
                else
                {
                    Toast.makeText(context, "La ruta ya no es favorita", Toast.LENGTH_SHORT).show();

                    eliminacion.eliminarFavorito(itemsRuta.get(position).getIdRuta(), itemsUsuario.get(0).getIdUsuario(),
                            TAG, context, activity);
                }
            }
        });

        String subcadenaHoraSalida = itemsHorario.get(position).getHoraSalida().substring(0,5);
        String subcadenaHoraLlegada = itemsHorario.get(position).getHoraLlegada().substring(0,5);

        holder.tvHorario.setText(subcadenaHoraSalida + " - " + subcadenaHoraLlegada);
        holder.tvPrecio.setText(itemsHorario.get(position).getPrecio() + "€");
        holder.tvCalendario.setText(itemsHorario.get(position).getDiasSemana());

        int horaRuta = Integer.parseInt(itemsHorario.get(position).getHoraSalida().substring(0,2));
        int minutosRuta = Integer.parseInt(itemsHorario.get(position).getHoraSalida().substring(3,5));

        Calendar calendario = Calendar.getInstance();
        int horaActual = calendario.get(Calendar.HOUR_OF_DAY);
        int minutosActual = calendario.get(Calendar.MINUTE);

        fontSecondary = Typeface.createFromAsset(context.getAssets(),  "fonts/Alex_Book.otf");

        holder.tvHoraRestante.setText(restarHorasFecha(horaRuta, horaActual, minutosRuta, minutosActual));
    }

    /**
     *
     * @return -> Devuelve el tamaño de la lista de horarios
     */
    @Override
    public int getItemCount() {

        return itemsHorario.size();
    }

    public class HorarioViewHolder extends RecyclerView.ViewHolder
    {
        // Campos respectivos de un item
        public TextView tvNombreRuta;
        public TextView tvNumLinea;
        public TextView tvNomEstacion;
        public CheckBox cbFavourite;
        public TextView tvHoraRestante;
        public ImageButton butDetalle;

        public TextView tvHorario;
        public TextView tvPrecio;
        public TextView tvCalendario;
        public CheckBox ibAviso;

        public HorarioViewHolder(View itemView) {
            super(itemView);

            tvNombreRuta = (TextView) itemView.findViewById(R.id.nomRuta);
            tvNumLinea = (TextView) itemView.findViewById(R.id.numLinea);
            tvNomEstacion = (TextView) itemView.findViewById(R.id.nomEstacion);
            cbFavourite = (CheckBox) itemView.findViewById(R.id.checkFavourite);
            tvHoraRestante = (TextView) itemView.findViewById(R.id.horaRestante);
            butDetalle = (ImageButton) itemView.findViewById(R.id.butRutaDetalle);

            tvHorario = (TextView) itemView.findViewById(R.id.tabHorario);
            tvPrecio = (TextView) itemView.findViewById(R.id.tabPrecio);
            tvCalendario = (TextView) itemView.findViewById(R.id.tabDiasSemana);
            ibAviso = (CheckBox) itemView.findViewById(R.id.checkAvisos);
        }
    }

    /**
     * Método utilizado para saber cuanto tiempo queda para iniciarse la ruta
     *
     * @param horaRuta -> Variable de la hora de comienzo de la ruta
     * @param horaActual -> Variable de la hora actual
     * @param minutoRuta -> Variable del minuto de comienzo de la ruta
     * @param minutoActual -> Variable de la hora actual
     * @return -> Devuelve la diferencia entre la hora de comienzo de la ruta y la hora actual
     */
    private String restarHorasFecha(int horaRuta, int horaActual, int minutoRuta, int minutoActual)
    {
        int resultHora;
        int resultMinuto;

        if (minutoRuta >= minutoActual)
        {
            if (horaRuta >= horaActual)
            {
                resultHora = horaRuta - horaActual;
                resultMinuto = minutoRuta - minutoActual;
            }
            else
            {
                resultHora = horaRuta + (23 - horaActual);
                resultMinuto = minutoRuta - minutoActual;
            }
        }
        else
        {
            if ((horaRuta > horaActual))
            {
                resultHora = horaRuta - horaActual - 1;
                resultMinuto = minutoRuta + (60 - minutoActual);
            }
            else
            {
                resultHora = horaRuta + (23 - horaActual);
                resultMinuto = minutoRuta + (60 - minutoActual);
            }
        }

        if (resultHora == 0)
        {
            return "Quedan " + resultMinuto + " minutos";
        }
        else if (resultHora == 1)
        {
            return "Quedan " + resultHora + " hora";
        }
        else {
            return "Quedan " + resultHora + " horas";
        }
    }
}
