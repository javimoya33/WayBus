package com.example.acer.waybus.Rutas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.waybus.Modelo.Estacion;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Linea;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.R;

import java.util.List;

import com.example.acer.waybus.Adaptadores.*;

/**
 * Adaptador del recycler view de Rutas.
 *
 * Se asocia a cada elemento de la vista los valores recibidos en la consulta a la base de datos.
 */
public class Adaptador_Rutas extends RecyclerView.Adapter<Adaptador_Rutas.RutaViewHolder> {

    /* Etiqueta de depuración */
    private static final String TAG = Adaptador_Rutas.class.getSimpleName();

    /*Lista de objetos {@link Ruta} que representa la fuente de datos de inflado */
    private List<Ruta> itemsRuta;
    private List<Horario> itemsHorario;
    private List<Linea> itemsLinea;
    private List<Estacion> itemsEstacion;
    private List<Usuario> itemsUsuario;

    /* Contexto donde actua el recycler view */
    private Context context;

    private Activity activity;

    private RecyclerView.LayoutManager layoutManager;

    Typeface fontPrincipal;

    private int contraer = 0;

    Boolean pulsadoArrow = false;

    private int[] tvFavoritos = new int[1000];

    private cargarUsuarioFavoritos carga = new cargarUsuarioFavoritos();
    private cargaHorarios cargaHorarios = new cargaHorarios();

    private insertarFavorito insercion = new insertarFavorito();
    private eliminarFavorito eliminacion = new eliminarFavorito();

    private Fragment_seccion1 fragment = new Fragment_seccion1();

    /**
     *
     * Constructor de la clase
     *
     * @param itemsruta -> Lista de objetos con la fuente de datos de la tabla 'Rutas'
     * @param itemsHorario -> Lista de objetos con la fuente de datos de la tabla 'Horarios'
     * @param itemslinea -> Lista de objetos con la fuente de datos de la tabla 'Lineas'
     * @param itemsestacion -> Lista de objetos con la fuente de datos de la tabla 'Estaciones'
     * @param itemsusuario -> Lista de objetos con la fuente de datos de la tabla 'Usuarios'
     * @param cntxt -> Contexto de la aplicación
     * @param favs -> Array con las posiciones de las rutas favoritas
     * @param act -> Actividad actual de la aplicación
     */
    public Adaptador_Rutas(List<Ruta> itemsruta, List<Horario> itemsHorario, List<Linea> itemslinea,
                           List<Estacion> itemsestacion, List<Usuario> itemsusuario, Context cntxt,
                           int[] favs, Activity act)
    {
        this.context = cntxt;
        this.itemsRuta = itemsruta;
        this.itemsHorario = itemsHorario;
        this.itemsLinea = itemslinea;
        this.itemsEstacion = itemsestacion;
        this.itemsUsuario = itemsusuario;
        this.tvFavoritos = favs;
        this.activity = act;
    }

    /**
     *
     * @param parent -> Controla la lista de views de la aplicación
     * @param viewType
     * @return -> Devuelve el layout asociado al adaptador
     */
    @Override
    public Adaptador_Rutas.RutaViewHolder onCreateViewHolder(ViewGroup parent, final int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lineas_fragment_seccion1, parent, false);

        return new RutaViewHolder(v);
    }

    /**
     *
     * @param holder -> Soporte de la vista
     * @param position -> posicion concreta del holder
     */
    @Override
    public void onBindViewHolder(final RutaViewHolder holder, final int position)
    {
        // Al deslizarnos por la lista de rutas el recycler de horarios asociado a la ruta se replegará en caso de
        // haya sido abierto previamente.
        if (itemsRuta.get(position).getIdRuta() > (contraer + 4) || itemsRuta.get(position).getIdRuta() < (contraer - 4))
        {
            holder.recHorarios.setVisibility(View.GONE);
        }

        fontPrincipal = Typeface.createFromAsset(context.getAssets(),  "fonts/Atlantic_Cruise.ttf");

        holder.tvNombreRuta.setTypeface(fontPrincipal);
        holder.tvNumLinea.setTypeface(fontPrincipal);
        holder.tvNomEstacion.setTypeface(fontPrincipal);

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
                intent.putExtra("Modelo", itemsLinea.get(position).getModelo());
                intent.putExtra("Capacidad", itemsLinea.get(position).getCapacidad());
                intent.putExtra("NomEstacion", itemsEstacion.get(position).getNombre());
                intent.putExtra("CP", itemsEstacion.get(position).getCP());
                intent.putExtra("Telefono", itemsEstacion.get(position).getTelefono());
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
    }

    /**
     *
     * @return -> Devuelve el tamaño de la lista de rutas
     */
    @Override
    public int getItemCount()
    {
        return itemsRuta.size();
    }

    public class RutaViewHolder extends RecyclerView.ViewHolder
    {
        // Campos respectivos de un item
        public TextView tvNombreRuta;
        public TextView tvNumLinea;
        public TextView tvNomEstacion;
        public CheckBox cbFavourite;

        public RecyclerView recHorarios;
        public ImageButton butDetalle;

        public View separatorHora;
        public ImageButton butArrowMore;
        public Drawable arrow_bottom;
        public Drawable arrow_bottom_checked;
        public Drawable grad_line;
        public Drawable grad_line_checked;

        public RutaViewHolder(View itemView) {
            super(itemView);

            tvNombreRuta = (TextView) itemView.findViewById(R.id.nomRuta);
            tvNumLinea = (TextView) itemView.findViewById(R.id.numParada);
            tvNomEstacion = (TextView) itemView.findViewById(R.id.nomEstacion);
            cbFavourite = (CheckBox) itemView.findViewById(R.id.checkFavourite);
            butDetalle = (ImageButton) itemView.findViewById(R.id.butRutaDetalle);
            butArrowMore = (ImageButton) itemView.findViewById(R.id.arrowBottom);
            separatorHora = (View) itemView.findViewById(R.id.separator_hor);

            arrow_bottom = context.getResources().getDrawable(R.drawable.icon_arrow_bottom);
            arrow_bottom_checked = context.getResources().getDrawable(R.drawable.icon_arrow_bottom_checked);
            grad_line = context.getResources().getDrawable(R.drawable.gradient_line);
            grad_line_checked = context.getResources().getDrawable(R.drawable.gradient_line_checked);

            recHorarios = (RecyclerView) itemView.findViewById(R.id.tableHorarios);
            recHorarios.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(context);
            recHorarios.setLayoutManager(layoutManager);

            // Instancia del id propio del dispositivo Android donde se ejecute la aplicación
            final String idMovil = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

            butArrowMore.setOnClickListener(new View.OnClickListener() {

                /**
                 * Evento que mostrará la lista de horarios asociada a cada ruta
                 *
                 * @param v
                 */
                @Override
                public void onClick(View v) {

                    if (pulsadoArrow == false)
                    {
                        separatorHora.setBackground(grad_line_checked);
                        butArrowMore.setImageDrawable(arrow_bottom_checked);

                        recHorarios.setVisibility(View.GONE);

                        pulsadoArrow = true;
                    }
                    else if (pulsadoArrow == true)
                    {
                        separatorHora.setBackground(grad_line);
                        butArrowMore.setImageDrawable(arrow_bottom);

                        recHorarios.setVisibility(View.VISIBLE);

                        contraer = itemsRuta.get(getAdapterPosition()).getIdRuta();
                        cargaHorarios.cargarHorarios(String.valueOf(itemsRuta.get(getAdapterPosition()).getIdRuta()), idMovil,
                                recHorarios, context, TAG, activity);

                        pulsadoArrow = false;
                    }
                }
            });
        }
    }
}

