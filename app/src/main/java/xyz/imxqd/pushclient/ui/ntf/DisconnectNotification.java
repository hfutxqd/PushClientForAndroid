package xyz.imxqd.pushclient.ui.ntf;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import xyz.imxqd.pushclient.service.PushService;
import xyz.imxqd.pushclient.R;

public class DisconnectNotification {
    private static final String NOTIFICATION_TAG = "Disconnect";

    public static void notify(final Context context,
                              final String content, final int number) {
        final Resources res = context.getResources();
        Intent intent = new Intent(context, PushService.class);
        intent.setAction(PushService.ACTION_RECONNECT);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final String ticker = content;
        final String title = res.getString(R.string.action_notification_title);
        final String text = content;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_new_message)
                .setContentTitle(title)
                .setContentText(text)

                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(ticker)
                .setNumber(number)

                .addAction(
                        R.drawable.ic_action_stat_reply,
                        res.getString(R.string.action_reconnect),
                        pendingIntent)

                .setAutoCancel(true);

        notify(context, builder.build());
    }

    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }

    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
    }
}
