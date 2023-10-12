package com.example.lucescasa;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Crea la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm_channel")
                .setContentTitle("Luces encendiadas")
                .setContentText("Se han enciendido las luces de manera utomatica")
                .setSmallIcon(R.drawable.cartoon_light)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Muestra la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
