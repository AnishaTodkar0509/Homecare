package com.example.smarthomemaintainance;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MaintenanceReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID =
            "maintenance_channel";

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        String applianceName =
                intent.getStringExtra(
                        "appliance_name"
                );

        String maintenanceType =
                intent.getStringExtra(
                        "maintenance_type"
                );

        String maintenanceDate =
                intent.getStringExtra(
                        "maintenance_date"
                );

        if (applianceName == null) {
            applianceName = "Your Appliance";
        }

        if (maintenanceType == null) {
            maintenanceType = "Maintenance";
        }

        if (maintenanceDate == null) {
            maintenanceDate = "";
        }

        createNotificationChannel(context);

        Intent openIntent =
                new Intent(
                        context,
                        MainActivity.class
                );

        openIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        openIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        String title =
                "🔧 Maintenance Reminder";

        String message =
                applianceName
                        + "\n"
                        + maintenanceType
                        + "\n"
                        + "Scheduled for "
                        + maintenanceDate;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle(title)
                        .setContentText(
                                applianceName
                                        + " • "
                                        + maintenanceType
                        )
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(message)
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setAutoCancel(true)
                        .setContentIntent(
                                pendingIntent
                        );

        if (Build.VERSION.SDK_INT >= 33) {

            if (context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        NotificationManagerCompat
                .from(context)
                .notify(
                        (int) System.currentTimeMillis(),
                        builder.build()
                );
    }

    private void createNotificationChannel(
            Context context
    ) {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Maintenance Reminders",
                            NotificationManager
                                    .IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Notifications for scheduled home appliance maintenance"
            );

            NotificationManager manager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {

                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }
}