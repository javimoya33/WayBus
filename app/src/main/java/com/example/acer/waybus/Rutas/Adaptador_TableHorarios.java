package com.example.acer.waybus.Rutas;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.waybus.Avisos.Fragment_Avisos_Receiver;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.R;

import com.example.acer.waybus.Adaptadores.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Adaptador del recycler view de la tabla de horarios.
 *
 * Se asocia a cada elemento de la vista los valores recibidos en la consulta a la base de datos.
 */
public class Adaptador_TableHorarios extends RecyclerView.Adapter<Adaptador_TableHorarios.HorarioViewHolder> {

    /* Etiqueta de depuración */
    private static final String TAG = Adaptador_TableHorarios.class.getSimpleName();

    /* Lista de objetos {@link Ruta} que representa la fuente de datos de inflado */
    private List<Horario> itemsHorario;
    private List<Usuario> itemsUsuario;

    /* Contexto donde actua el recycler view */
    private Context context;
    private Activity activity;

    private cargaAviso insercion = new cargaAviso();

    /**
     *
     * Constructor de la clase
     *
     * @param itemshorario -> Lista de objetos con la fuente de datos de la tabla 'Horarios'
     * @param itemsusuario -> Lista de objetos con la fuente de datos de la tabla 'Usuarios'
     * @param cntxt -> Contexto de la aplicación
     * @param act -> Actividad actual de la aplicación
     */
    public Adaptador_TableHorarios(List<Horario> itemshorario, List<Usuario> itemsusuario, Context cntxt,
                                   Activity act)
    {
        this.context = cntxt;
        this.itemsHorario = itemshorario;
        this.itemsUsuario = itemsusuario;
        this.activity = act;
    }

    /**
     *
     * @param parent -> Controla la lista de views de la aplicación
     * @param viewType
     * @return -> Devuelve el layout asociado al adaptador
     */
    @Override
    public Adaptador_TableHorarios.HorarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lineas_table_horarios, parent, false);

        return new HorarioViewHolder(v);
    }

    /**
     *
     * @param holder -> Soporte de la vista
     * @param position -> posicion concreta del holder
     */
    @Override
    public void onBindViewHolder(final Adaptador_TableHorarios.HorarioViewHolder holder, final int position) {

        final String subcadenaHoraSalida = itemsHorario.get(position).getHoraSalida().substring(0,5);
        String subcadenaHoraLlegada = itemsHorario.get(position).getHoraLlegada().substring(0,5);

        holder.tvHorario.setText(subcadenaHoraSalida + " - " + subcadenaHoraLlegada);
        holder.tvPrecio.setText(itemsHorario.get(position).getPrecio() + "€");
        holder.tvDiasSemana.setText(itemsHorario.get(position).getDiasSemana());

        holder.imgAviso.setOnClickListener(new View.OnClickListener() {

            /**
             * Evento que enviará un guardará un aviso para que suene a la hora estipulada
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                try
                {
                    String defaultRepeticion = "Sólo una vez";
                    String defaultHoraAviso = "5min.";
                    int idHorario = itemsHorario.get(position).getIdHorario();
                    int idUsuario = itemsUsuario.get(0).getIdUsuario();

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date now = new Date();
                    String strHoraActual = sdf.format(now);

                    Date horaActual = sdf.parse(strHoraActual);
                    Date horaAviso = sdf.parse(subcadenaHoraSalida);

                    String fechaAviso = insercion.fechaAviso(horaActual, horaAviso, itemsHorario.get(position).getDiasSemana());

                    insercion.insertarAviso(defaultRepeticion, defaultHoraAviso, fechaAviso, idHorario, idUsuario, TAG, context,
                            activity);

                    int horAvisoFinal = Integer.parseInt(subcadenaHoraSalida.toString().substring(0, 2));
                    int minAvisoFinal = Integer.parseInt(subcadenaHoraSalida.toString().substring(3, 5));

                    if (minAvisoFinal >= 5)
                    {
                        minAvisoFinal -= 5;
                    }
                    else
                    {
                        horAvisoFinal -= 1;

                        int resto = 5 - minAvisoFinal;
                        minAvisoFinal = 60 - resto;
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,
                            horAvisoFinal);
                    calendar.set(Calendar.MINUTE,
                            minAvisoFinal);

                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(context, Fragment_Avisos_Receiver.class);
                    intent.putExtra("ruta", "Aviso recibido");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            context, itemsHorario.get(position).getIdHorario(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    manager.set(manager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    Toast.makeText(context, "El aviso se recibirá a las " + horAvisoFinal + ":" + minAvisoFinal,
                            Toast.LENGTH_SHORT).show();
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *
     * @return -> Devuelve el tamaño de la tabla de horarios
     */
    @Override
    public int getItemCount() {

        return itemsHorario.size();
    }

    public class HorarioViewHolder extends RecyclerView.ViewHolder {

        public TextView tvHorario;
        public TextView tvPrecio;
        public TextView tvDiasSemana;
        public ImageButton imgAviso;

        public HorarioViewHolder(View itemView) {
            super(itemView);

            tvHorario = (TextView) itemView.findViewById(R.id.tabHorario);
            tvPrecio = (TextView) itemView.findViewById(R.id.tabPrecio);
            tvDiasSemana = (TextView) itemView.findViewById(R.id.tabDiasSemana);
            imgAviso = (ImageButton) itemView.findViewById(R.id.imgAvisos);
        }
    }
}
