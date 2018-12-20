package com.example.acer.waybus.MisParadas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Adaptadores.*;
import com.example.acer.waybus.Rutas.Adaptador_TableHorarios;
import com.example.acer.waybus.Rutas.Fragment_seccion1;
import com.example.acer.waybus.Rutas.viewRutasDetalle;
import com.example.acer.waybus.Modelo.Estacion;
import com.example.acer.waybus.Modelo.Favoritos;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Linea;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.R;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Adaptador del recycler view de Mis Paradas.
 *
 * Se asocia a cada elemento de la vista los valores recibidos en la consulta a la base de datos.
 */
public class Fragment_MisParadasAdapter extends RecyclerView.Adapter<Fragment_MisParadasAdapter.RutaViewHolder> {

    /* Etiqueta de depuración */
    private static final String TAG = Fragment_MisParadasAdapter.class.getSimpleName();

    /* Lista de objetos {@link} que representa la fuente de datos de inflado */
    private List<Ruta> itemsRuta;
    private List<Horario> itemsHorario;
    private List<Linea> itemsLinea;
    private List<Estacion> itemsEstacion;
    private List<Favoritos> itemsFavorito;
    private List<Usuario> itemsUsuario;

    private Gson gson = new Gson();

    //private RecyclerView recyclerHorarios;
    private RecyclerView.LayoutManager layoutManager;

    /* Adaptador del recycler view */
    private Adaptador_TableHorarios adapterTable;

    /* Contexto donde actua el recycler view */
    private Context context;

    private Activity activity;

    /* Array donde se marcarán las posiciones de las rutas favoritas */
    private int[] tvFavoritos = new int[100];

    Typeface fontPrincipal;

    private int contraer = 0;
    Boolean pulsadoArrow = false;

    private Fragment_seccion1 fragment = new Fragment_seccion1();

    private insertarFavorito insercion = new insertarFavorito();
    private eliminarFavorito eliminacion = new eliminarFavorito();

    /**
     *
     * Constructor de la clase
     *
     * @param itemsruta -> Lista de objetos con la fuente de datos de la tabla 'Rutas'
     * @param itemsHorario -> Lista de objetos con la fuente de datos de la tabla 'Horarios'
     * @param itemslinea -> Lista de objetos con la fuente de datos de la tabla 'Lineas'
     * @param itemsestacion -> Lista de objetos con la fuente de datos de la tabla 'Estaciones'
     * @param itemsFavorito -> Lista de objetos con la fuente de datos de la tabla 'Favoritos'
     * @param itemsusuario -> Lista de objetos con la fuente de datos de la tabla 'Usuarios'
     * @param cntxt -> Contexto de la aplicación
     * @param act -> Actividad de la aplicación
     */
    public Fragment_MisParadasAdapter(List<Ruta> itemsruta, List<Horario> itemsHorario, List<Linea> itemslinea,
                            List<Estacion> itemsestacion, List<Favoritos> itemsFavorito,
                            List<Usuario> itemsusuario, Context cntxt, int[] favs, Activity act)
    {
        this.context = cntxt;
        this.itemsRuta = itemsruta;
        this.itemsHorario = itemsHorario;
        this.itemsLinea = itemslinea;
        this.itemsEstacion = itemsestacion;
        this.itemsFavorito = itemsFavorito;
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
    public Fragment_MisParadasAdapter.RutaViewHolder onCreateViewHolder(ViewGroup parent, final int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fav_table_paradas, parent, false);

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
        if (itemsRuta.get(position).getIdRuta() > (contraer + 4) || itemsRuta.get(position).getIdRuta() < (contraer - 4))
        {
            holder.recHorarios.setVisibility(View.GONE);
        }

        fontPrincipal = Typeface.createFromAsset(context.getAssets(),  "fonts/Atlantic_Cruise.ttf");

        holder.tvNombreRuta.setTypeface(fontPrincipal);
        holder.tvNumLinea.setTypeface(fontPrincipal);
        holder.tvNomEstacion.setTypeface(fontPrincipal);

        // Si el nombre de la ruta es demasiado largo se sustituirá por puntos suspensivos
        final int cadenaRuta = itemsFavorito.get(position).getNomFav().length();
        if (cadenaRuta < 23)
        {
            int resto = 23 - cadenaRuta;
            String espacio = "";

            for (int i = 1; i <= resto; i++)
            {
                espacio += " ";
            }

            holder.tvNombreRuta.setText(itemsFavorito.get(position).getNomFav());
        }
        else if (cadenaRuta > 23)
        {
            String txtRuta = itemsFavorito.get(position).getNomFav();

            String subcadenaRuta = txtRuta.substring(21, txtRuta.length());
            String nuevaTxtRuta = txtRuta.replace(subcadenaRuta, "...");

            holder.tvNombreRuta.setText(nuevaTxtRuta);
        }
        else
        {
            holder.tvNombreRuta.setText(itemsFavorito.get(position).getNomFav());
        }

        holder.tvNombreRuta.setOnClickListener(new View.OnClickListener() {

            /**
             * Evento que se llamará al clickar sobre la etiqueta de título de la ruta. En esta vista estará
             * permitido modificar el título de la ruta
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                holder.tvNombreRuta.setVisibility(View.GONE);
                holder.editNomRuta.setVisibility(View.VISIBLE);
                holder.editNomRuta.setTypeface(fontPrincipal);
                holder.editNomRuta.setFocusable(true);
                holder.editNomRuta.setCursorVisible(true);
                holder.editNomRuta.setSelection(0);

                holder.tvNomEstacion.setVisibility(View.GONE);
                holder.butGuardarRuta.setVisibility(View.VISIBLE);
                holder.editNomRuta.setText(itemsFavorito.get(position).getNomFav());
            }
        });

        holder.butGuardarRuta.setOnClickListener(new View.OnClickListener() {

            /**
             * Evento que se llamará al clickar sobr eel botón encargado de guardar el nuevo valor del nombre de la
             * ruta modificada
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                insercion.actualizarNombreFav(holder.editNomRuta.getText().toString(),
                        itemsFavorito.get(position).getIdFavorito(), TAG, context, activity);

                holder.editNomRuta.setVisibility(View.GONE);
                holder.tvNomEstacion.setVisibility(View.VISIBLE);
                holder.butGuardarRuta.setVisibility(View.GONE);

                holder.tvNombreRuta.setVisibility(View.VISIBLE);
                holder.tvNombreRuta.setText(holder.editNomRuta.getText().toString());

                Toast.makeText(context, "Nombre de la ruta guardado " + itemsFavorito.get(position).getIdFavorito()
                        + " " + holder.editNomRuta.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {

                holder.editNomRuta.setVisibility(View.GONE);
                holder.tvNomEstacion.setVisibility(View.VISIBLE);
                holder.butGuardarRuta.setVisibility(View.GONE);

                holder.tvNombreRuta.setVisibility(View.VISIBLE);
                holder.tvNombreRuta.setText(holder.editNomRuta.getText().toString());
            }
        };

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

        holder.cbFavourite.setChecked(true);
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
        public EditText editNomRuta;
        public Button butGuardarRuta;

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
            editNomRuta = (EditText) itemView.findViewById(R.id.editNomRuta);
            butGuardarRuta = (Button) itemView.findViewById(R.id.btnGuardarRuta);

            arrow_bottom = context.getResources().getDrawable(R.drawable.icon_arrow_bottom);
            arrow_bottom_checked = context.getResources().getDrawable(R.drawable.icon_arrow_bottom_checked);
            grad_line = context.getResources().getDrawable(R.drawable.gradient_line);
            grad_line_checked = context.getResources().getDrawable(R.drawable.gradient_line_checked);

            recHorarios = (RecyclerView) itemView.findViewById(R.id.tableHorarios);
            recHorarios.setHasFixedSize(true);

            // Usar un administrador para LinearLayout
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
                        cargarHorarios(String.valueOf(itemsRuta.get(getAdapterPosition()).getIdRuta()), idMovil,
                                recHorarios);

                        pulsadoArrow = false;
                    }
                }
            });
        }
    }

    /**
     * Método que devuelve la lista de horarios asociado a uns ruta particular
     *
     * @param idRuta -> id de la ruta que asocia la lista de horarios
     * @param idMovil -> id del dispositivo móvil que utiliza el usuario
     * @param recyclerHorarios -> Contenedor con la lista de horarios
     */
    public void cargarHorarios(String idRuta, String idMovil, final RecyclerView recyclerHorarios)
    {
        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_HORARIOS_BY_RUTA + "?Horarios_idRutas=" + idRuta + "&idMovil=" + idMovil;

        // Petición GET que devolverán la lista de horarios asociado a una ruta
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuestaHorarios(response, recyclerHorarios);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    /**
     *
     * @param response -> Objeto JSON que devuelve la respuesta a la consulta a la base de datos.
     * @param recyclerHorarios -> Contenedor con la lista de horarios
     */
    private void procesarRespuestaHorarios(JSONObject response, RecyclerView recyclerHorarios)
    {
        try
        {
            // En función de si la consulta devuelve algún valor o no se mostrarán o no la lista con las rutas
            String estado = response.getString("estado");
            switch (estado)
            {
                case "1": // ÉXITO
                    // Obtener array "metas" Json
                    JSONArray mensaje = response.getJSONArray("horarios");
                    JSONArray mensaje2 = response.getJSONArray("usuario");
                    // Parsear con Gson
                    Horario[] horarios = gson.fromJson(mensaje.toString(), Horario[].class);
                    Usuario[] usuarios = gson.fromJson(mensaje2.toString(), Usuario[].class);

                    // Inicializar adaptador
                    adapterTable = new Adaptador_TableHorarios(Arrays.asList(horarios),
                            Arrays.asList(usuarios), context, activity);
                    // Setear adaptador a la lista
                    recyclerHorarios.setAdapter(adapterTable);
                    break;

                case "2": // FALLIDO
                    String mensaje3 = response.getString("estado");
                    Toast.makeText(
                            context,
                            "No hay horarios de esta ruta por mostrar",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}