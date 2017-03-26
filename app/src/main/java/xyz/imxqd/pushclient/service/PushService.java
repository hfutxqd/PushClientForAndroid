package xyz.imxqd.pushclient.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

import xyz.imxqd.push.Client;
import xyz.imxqd.push.Message;
import xyz.imxqd.pushclient.R;
import xyz.imxqd.pushclient.dao.DB;
import xyz.imxqd.pushclient.dao.DMessage;
import xyz.imxqd.pushclient.ui.ntf.DisconnectNotification;
import xyz.imxqd.pushclient.ui.ntf.NewMessageNotification;

public class PushService extends Service implements Client.OnNewMessageListener {

    private static final String TAG = "PushService";
    public static final String ACTION_CONNECT = "action_connect";
    public static final String ACTION_RECONNECT = "action_reconnect";
    public static final String ACTION_DISCONNECT = "action_disconnect";

    public static final String ARG_HOST = "server_host";
    public static final String ARG_PORT = "server_port";

    private static final int REQUEST_CODE_FOREGROUND = 1;


    private PushServiceBinder mBinder;
    private Client mClient;
    private int num = 0;
    private String mHost;
    private int mPort;
    private boolean isConnected = false;

    private Handler mHandler;

    public PushService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new PushServiceBinder();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent == null) {
            return START_STICKY;
        }
        if (ACTION_CONNECT.equals(intent.getAction())) {
            String host = intent.getStringExtra(ARG_HOST);
            int port = intent.getIntExtra(ARG_PORT, 73);
            if (isConnected && host.equals(mHost) && port == mPort) {
                return START_STICKY;
            }
            mHost = intent.getStringExtra(ARG_HOST);
            mPort = intent.getIntExtra(ARG_PORT, 73);
            Notification notification = new NotificationCompat.Builder(PushService.this)
                    .setSmallIcon(R.drawable.ic_stat_new_message)
                    .setContentTitle(getString(R.string.action_notification_title))
                    .setContentText(getString(R.string.action_notification_connecting, mHost, mPort))
                    .build();
            startForeground(REQUEST_CODE_FOREGROUND, notification);
            try {
                mClient = new Client(mHost, mPort, PushService.this);
                mClient.start();
                isConnected = false;
                if (mCallback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onConnecting();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (ACTION_RECONNECT.equals(intent.getAction())){
            Notification notification = new NotificationCompat.Builder(PushService.this)
                    .setSmallIcon(R.drawable.ic_stat_new_message)
                    .setContentTitle(getString(R.string.action_notification_title))
                    .setContentText(getString(R.string.action_notification_connecting, mHost, mPort))
                    .build();
            startForeground(REQUEST_CODE_FOREGROUND, notification);
            try {
                mClient = new Client(mHost, mPort, PushService.this);
                mClient.start();
                isConnected = false;
                if (mCallback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onConnecting();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (ACTION_DISCONNECT.equals(intent.getAction())) {
            if (mClient != null) {
                mClient.stop();
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onNewMessage(final Message message) {


        final DMessage msg = new DMessage(message);
        int id = DB.save(message.getTime(), message.getContent());
        msg.setId(id);
        if (mCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onNewMessage(msg);
                }
            });
        }
        NewMessageNotification.notify(getApplicationContext(), message.getContent(), num++);
    }

    @Override
    public void onDisconnected() {
        isConnected = false;
        if (mCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onDisconnected();
                }
            });
        }
        DisconnectNotification.notify(getApplicationContext(),
                getString(R.string.action_notification_disconnected), num++);
        stopForeground(true);
    }

    @Override
    public void onConnected() {
        isConnected = true;
        if (mCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onConnected();
                }
            });
        }
        Intent intent = new Intent(this, PushService.class);
        intent.setAction(PushService.ACTION_DISCONNECT);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(PushService.this)
                .setSmallIcon(R.drawable.ic_stat_new_message)
                .setContentTitle(getString(R.string.action_notification_title))
                .setContentText(getString(R.string.action_notification_connected, mHost, mPort))
                .addAction(R.drawable.ic_action_stat_reply,
                        getString(R.string.action_disconnect), pendingIntent)
                .build();
        startForeground(REQUEST_CODE_FOREGROUND, notification);
    }

    public interface Callback {
        void onNewMessage(DMessage message);
        void onConnecting();
        void onConnected();
        void onDisconnected();
    }

    private Callback mCallback;

    public class PushServiceBinder extends Binder {
        public boolean isConnected() {
            return isConnected;
        }

        public String getHost() {
            return mHost;
        }

        public int getPort() {
            return mPort;
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
            if (isConnected()) {
                mCallback.onConnected();
            }
        }

    }
}
