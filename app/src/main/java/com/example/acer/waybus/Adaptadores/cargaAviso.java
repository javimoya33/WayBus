package com.example.acer.waybus.Adaptadores;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Avisos.Fragment_Avisos_Receiver;
import com.example.acer.waybus.Modelo.Itinerario;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase contenedora de los métodos para la carga de avisos
 */
public class cargaAviso {

    /**
     * Método encargado de actualizar un valor de la tabla de avisos
     *
     * @param valor -> Campo de la tabla aviso que se actualizará
     * @param idAviso -> id del aviso a actualizar
     * @param constante -> URL correspondiente a la consulta de actualización a la base de datos
     * @param TAG -> Etiqueta de depuración
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad de la aplicación
     */
    public void actualizarAviso(String valor, int idAviso, String constante, final String TAG,
                                final Context context, final Activity activity)
    {
        // Mapeo encargado de actualizar la referencia a los datos de un aviso concreto
        HashMap<String, Object> map = new HashMap<>();
        map.put("valor", valor);
        map.put("idAviso", idAviso);

        JSONObject jsonObject = new JSONObject(map);

        Log.d(TAG, jsonObject.toString());

        // Petición POST que actualizará el valor del aviso pasado por parámetro
        VolleySingleton.getInstance(context)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                constante,
                                jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuestaAviso(response, context, activity);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                    }
                                }
                        ) {
                            @Override
                            public Map<String, String> getHeaders()
                            {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType()
                            {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
    }

    /**
     * Método encargado de insertar nuevos avisos en la base de datos
     *
     * @param repeticion -> valor que tomará el campo Repetición en la tabla de Avisos
     * @param horaAviso -> hora a la que se lanzará el aviso
     * @param fechaAviso -> día concreto en el que se lanzará el aviso
     * @param idHorario -> id del horario relativo al aviso que se va a lanzar
     * @param idUsuario -> id del usuario que ha lanzado el aviso
     * @param TAG -> Etiqueta de depuración
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad de la aplicación
     */
    public void insertarAviso(String repeticion, String horaAviso, String fechaAviso, int idHorario, int idUsuario,
                              final String TAG, final Context context, final Activity activity)
    {
        // Mapeo encargado de insertar un nuevo registro en la tabla de avisos
        HashMap<String, Object> map = new HashMap<>();
        map.put("repeticion", repeticion);
        map.put("horaAviso", horaAviso);
        map.put("fechaAviso", fechaAviso);
        map.put("idRuta", idHorario);
        map.put("idUsuario", idUsuario);

        JSONObject jsonObject = new JSONObject(map);

        Log.d(TAG, jsonObject.toString());

        // Petición POST que insertará un nuevo aviso en la base de datos
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_AVISOS,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                procesarRespuestaAviso(response, context, activity);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders(){

                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType(){
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    /**
     * Método encargado de eliminar los avisos de la tabla cuando estos ya han quedado pasados en el tiempo
     *
     * @param TAG -> Etiqueta de depuración
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad de la aplicación
     */
    public void eliminarAvisosPasados(final String TAG, final Context context, final Activity activity)
    {
        JSONObject jsonObject = new JSONObject();

        Log.d(TAG, jsonObject.toString());

        // Petición POST que eliminará el aviso de la base de datos
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.DELETE_AVISOS_PAST,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                procesarRespuestaAviso(response, context, activity);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    /**
     * Procesar la petición POST
     *
     * @param response -> Objeto JSON que devuelve la respuesta a la consulta a la base de datos.
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad de la aplicación
     */
    public void procesarRespuestaAviso(JSONObject response, Context context, Activity activity)
    {
        try {
            String estado = response.getString("estado");
            String mensaje = response.getString("mensaje");

            switch (estado)
            {
                case "1":
                    Toast.makeText(context, "especial", Toast.LENGTH_SHORT).show();
                    // Enviar código de éxito
                    activity.setResult(Activity.RESULT_OK);
                    // Terminar actividad
                    activity.finish();;
                    break;

                case "2":

                    // Enviar código de falla
                    activity.setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    activity.finish();
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Método encargado de gestionar el dia en el que será emitida la alerta
     *
     * @param horaEnvio -> Hora a la que se lanza la petición de alerta
     * @param horaAviso -> Hora a la que se emitirá la alerta
     * @param frecuencia -> Frecuencia a la que se lanzará el aviso
     * @return -> Devolverá el día concreto en el que lanzará la alerta.
     */
    public String fechaAviso(Date horaEnvio, Date horaAviso, String frecuencia)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        GregorianCalendar gregorianHoy = new GregorianCalendar();
        GregorianCalendar gregorianManana = new GregorianCalendar();
        GregorianCalendar gregorianPasadoManana = new GregorianCalendar();
        String fechaHoy = sdf.format(gregorianHoy.getTime());

        // Fecha de mañana
        gregorianManana.add(Calendar.DAY_OF_MONTH, 1);
        String fechaManana = sdf.format(gregorianManana.getTime());

        // Fecha de pasado mañana
        gregorianPasadoManana.add(Calendar.DAY_OF_MONTH, 2);
        String fechapasadoManana = sdf.format(gregorianPasadoManana.getTime());

        int diaSemanaHoy = gregorianHoy.get(Calendar.DAY_OF_WEEK);

        // Si la ruta del aviso pulsado se realiza de Luneas a Viernes
        if (frecuencia.equals("LMXJV"))
        {
            // Si hoy no es fin de semana
            if (diaSemanaHoy >= 2 && diaSemanaHoy <= 6)
            {
                // Si la hora a la que presionamos el botón aviso es menor a la hora del aviso
                // el aviso será lanzado en el día de hoy. En caso contrario el aviso sera lanzado
                // en el día de mañana
                if (horaEnvio.compareTo(horaAviso) < 0)
                {
                    return fechaHoy;
                }
                else
                {
                    return fechaManana;
                }
            }
            else if (diaSemanaHoy == 0) // Si hoy es domingo el aviso será lanzado mañana
            {
                return fechaManana;
            }
            else if (diaSemanaHoy == 7) // Si hoy es domingo el aviso será lanzado pasado mañana
            {
                return fechapasadoManana;
            }
            else
            {
                return fechapasadoManana;
            }
        }
        else if (frecuencia.equals("SD")) // Si la ruta del aviso pulsado se realiza los fines de semana
        {
            // Si hoy no es fin de semana
            if (diaSemanaHoy >= 2 && diaSemanaHoy <= 6)
            {
                // Cuantos días quedan hasta el sabado
                int difSabado = 7 - diaSemanaHoy;

                // El aviso será realizado el próximo sábado
                gregorianHoy.add(Calendar.DAY_OF_MONTH, difSabado);
                String proxSabado = sdf.format(gregorianHoy.getTime());

                return proxSabado;
            }
            else if (diaSemanaHoy == 0 || diaSemanaHoy == 7)
            {
                // Si la hora a la que presionamos el botón aviso es menor a la hora del aviso
                // el aviso será lanzado en el día de hoy. En caso contrario el aviso sera lanzado
                // en el día de mañana
                if (horaEnvio.compareTo(horaAviso) < 0)
                {
                    return fechaHoy;
                }
                else
                {
                    return fechaManana;
                }
            }
            else
            {
                return fechaManana;
            }
        }
        else
        {
            return fechaManana;
        }
    }

    /**
     * Método encargado de emitir el aviso al receiver
     *
     * @param horAvisoFinal -> Hora exacta a la que se emitirá el aviso
     * @param minAvisoFinal -> Minuto exacto a la que se emitirá el aviso
     * @param minResto -> Minutos de anticipación a la salida de la ruta
     * @param valRep -> Frecuencia a la se emitirá el aviso
     * @param context -> Contexto de la aplicación
     * @param idNotify -> id de la notificación
     */
    public void enviarAviso(int horAvisoFinal, int minAvisoFinal, int minResto, String valRep, Context context, int idNotify)
    {

        if (minAvisoFinal >= minResto)
        {
            minAvisoFinal -= minResto;
        }
        else
        {
            if (horAvisoFinal == 0)
            {
                horAvisoFinal = 23;
            }
            else
            {
                horAvisoFinal -= 1;

                int resto = minResto - minAvisoFinal;
                minAvisoFinal = 60 - resto;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,
                horAvisoFinal);
        calendar.set(Calendar.MINUTE,
                minAvisoFinal);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, Fragment_Avisos_Receiver.class);
        intent.putExtra("idAviso", idNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, idNotify, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (valRep.equals("Sólo una vez"))
        {
            Toast.makeText(context, "El aviso se recibirá a las " + horAvisoFinal + ":" + minAvisoFinal,
                    Toast.LENGTH_SHORT).show();
            manager.set(manager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else if (valRep.equals("Diariamente"))
        {
            Toast.makeText(context, "El aviso se recibirá cada dia a las " + horAvisoFinal + ":" + minAvisoFinal,
                    Toast.LENGTH_SHORT).show();
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else if (valRep.equals("Semanalmente"))
        {
            Toast.makeText(context, "El aviso se recibirá una vez en semana a las " + horAvisoFinal + ":" + minAvisoFinal,
                    Toast.LENGTH_SHORT).show();
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
        }
        else
        {
            manager.set(manager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }

    /**
     * Método encargado de emitir el aviso al receiver en caso de que queramos anticipar 1 hora la alerta
     *
     * @param horAvisoFinal -> Hora exacta a la que se emitirá el aviso
     * @param minAvisoFinal -> Minuto exacto a la que se emitirá el aviso
     * @param valRep -> Frecuencia a la se emitirá el aviso
     * @param context -> Contexto de la aplicación
     * @param idNotify -> id de la notificación
     */
    public void enviarAvisoEspecial(int horAvisoFinal, int minAvisoFinal, String valRep, Context context, int idNotify)
    {
        if (horAvisoFinal == 0)
        {
            horAvisoFinal = 23;
        }
        else
        {
            horAvisoFinal -= 1;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,
                horAvisoFinal);
        calendar.set(Calendar.MINUTE,
                minAvisoFinal);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, Fragment_Avisos_Receiver.class);
        intent.putExtra("idAviso", idNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, idNotify, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (valRep.equals("Sólo una vez"))
        {
            Toast.makeText(context, "El aviso se recibirá a las " + horAvisoFinal + ":" + minAvisoFinal,
                    Toast.LENGTH_SHORT).show();
            manager.set(manager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else if (valRep.equals("Diariamente"))
        {
            Toast.makeText(context, "El aviso se recibirá cada dia a las " + horAvisoFinal + ":" + minAvisoFinal,
                    Toast.LENGTH_SHORT).show();
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else if (valRep.equals("Semanalmente"))
        {
            Toast.makeText(context, "El aviso se recibirá una vez en semana a las " + horAvisoFinal + ":" + minAvisoFinal,
                    Toast.LENGTH_SHORT).show();
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
        }
        else
        {
            manager.set(manager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
