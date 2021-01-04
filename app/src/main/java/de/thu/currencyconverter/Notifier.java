package de.thu.currencyconverter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class Notifier {

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private Context context;
    public Notifier(Context context) {
        this.context = context;
        // 1. Create Manager
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 2. Create Notification Channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel("converter_channel");
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel("converter_channel",
                        "show update state",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // 3. Create Notification Builder

            // Custom layout for notification
            RemoteViews contentView = new RemoteViews(this.context.getPackageName(), R.layout.notification_update_rates);
            contentView.setImageViewResource(R.id.image, R.drawable.icon);
            contentView.setTextViewText(R.id.title, "CurrencyConverter is now up-to-date! ");
            contentView.setTextViewText(R.id.text, "Updating...");

        notificationBuilder = new NotificationCompat.Builder(context,"converter_channel")
                .setSmallIcon(R.drawable.ic_update)
               // .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(contentView)

              //  .setContentTitle("CurrencyConverter is up-to-date!")
                .setAutoCancel(false); //if you click on it it gets removed

      //  Notification notification = notificationBuilder.build();
        /*
        *  Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(1, notification);
        * */

    }


    public void showOrUpdateNotification(int value) {
        notificationBuilder.setContentText(value + " items updated");
        notificationManager.notify(123, notificationBuilder.build());
    }

    public void removeNotification() {
        notificationManager.cancel(123);
    }
}
