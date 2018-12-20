package com.example.acer.waybus.Avisos;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.acer.waybus.R;

/**
 * Fragment donde se recibirán los datos necesarios para la creación de la notificación de alerta
 */
public class Fragment_Avisos_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int idAviso = intent.getIntExtra("idAviso", 0);

        Uri uriDefaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_main_alert)
                .setContentTitle("Aviso")
                .setContentText("Tu autobus de la ruta " + idAviso + " está a punto de llegar")
                .setAutoCancel(true)
                .setSound(uriDefaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(idAviso, notificationBuilder.build());
    }
}
