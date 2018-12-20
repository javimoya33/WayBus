package com.example.acer.waybus.tools;

/**
 * Clase que contiene los códigos usados en "wayBus" para mantener la integridad de las
 * interacciones entre actividades y fragmentos.
 */
public class Constantes {

    /**
     * Transición Rutas -> Detalle
     */
    public static final int CODIGO_DETALLE = 100;

    /**
     * Puerto que se utilizará para la conexión.
     */
    private static final String PUERTO_HOST = ":80";

    /**
     * Dirección IP del alojamiento de las carpetas de peticiones.
     */
    private static final String IP = "http://192.168.1.18";

    /**
     * URLs del Web Service
     */
    public static final String GET_RUTAS = IP + PUERTO_HOST + "/wayBus/obtener_rutas.php";
    public static final String GET_BY_ID = IP + PUERTO_HOST + "/wayBus/obtener_rutas_por_id.php";
    public static final String UPDATE = IP + PUERTO_HOST + "/wayBus/actualizar_ruta_favorita.php";
    public static final String GET_FAV = IP + PUERTO_HOST + "/wayBus/obtener_rutas_favoritas.php";
    public static final String GET_HORARIO_BY_ORDES = IP + PUERTO_HOST + "/wayBus/obtener_horarios_por_origendestino.php";
    public static final String GET_PROX_RUTAS_HOY = IP + PUERTO_HOST + "/wayBus/obtener_rutas_proximas.php";
    public static final String GET_PROX_RUTAS_LMXJV = IP + PUERTO_HOST + "/wayBus/obtener_rutas_LMXJV.php";
    public static final String GET_PROX_RUTAS_SD = IP + PUERTO_HOST + "/wayBus/obtener_rutas_SD.php";
    public static final String GET_RUTAHORARIO_FAV = IP + PUERTO_HOST + "/wayBus/obtener_rutasHorarios_favoritos.php";
    public static final String GET_RUTAHORARIO_AVISOS = IP + PUERTO_HOST + "/wayBus/obtener_avisos.php";
    public static final String GET_ITINERARIO = IP + PUERTO_HOST + "/wayBus/obtener_itinerario.php";
    public static final String GET_DESTINOS_RUTAS = IP + PUERTO_HOST + "/wayBus/obtener_destinos_rutas.php";
    public static final String GET_HORARIOS_BY_RUTA = IP + PUERTO_HOST + "/wayBus/obtener_horarios_por_rutas.php";
    public static final String GET_USUARIO = IP + PUERTO_HOST + "/wayBus/obtener_usuario_por_id.php";
    public static final String INSERT_USUARIO = IP + PUERTO_HOST + "/wayBus/insertar_nuevo_usuario.php";
    public static final String INSERT_RUTA_FAV = IP + PUERTO_HOST + "/wayBus/insertar_ruta_favorita.php";
    public static final String DELETE_RUTA_FAV = IP + PUERTO_HOST + "/wayBus/eliminar_ruta_favorita.php";
    public static final String GET_ESTACION_BY_RUTA = IP + PUERTO_HOST + "/wayBus/obtener_estacion_por_origendestino.php";
    public static final String GET_AVISOS = IP + PUERTO_HOST + "/wayBus/obtener_avisos.php";
    public static final String INSERT_AVISOS = IP + PUERTO_HOST + "/wayBus/insertar_aviso.php";
    public static final String UPDATE_FREC_AVISO = IP + PUERTO_HOST + "/wayBus/actualizar_frecuencia_aviso.php";
    public static final String UPDATE_HORA_AVISO = IP + PUERTO_HOST + "/wayBus/actualizar_hora_aviso.php";
    public static final String GET_LATLNG_ORIGEN = IP + PUERTO_HOST + "/wayBus/obtener_latlng_origen.php";
    public static final String GET_RUTAS_MIZONA = IP + PUERTO_HOST + "/wayBus/obtener_rutas_mizona.php";
    public static final String UPDATE_NOMBRE_FAV = IP + PUERTO_HOST + "/wayBus/actualizar_nombre_fav.php";
    public static final String DELETE_AVISOS_PAST = IP + PUERTO_HOST + "/wayBus/eliminar_aviso.php";
}
