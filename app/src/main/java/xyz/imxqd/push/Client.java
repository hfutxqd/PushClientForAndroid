package xyz.imxqd.push;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;

import xyz.imxqd.push.Message;

/**
 * Created by imxqd on 2017/3/26.
 */
public final class Client {
    public static final String WELCOME_STR = "MessagePushServer";
    private static final String TAG = "Client";

    private Socket mServer;
    private final String mHost;
    private final int mPort;
    private OnNewMessageListener mListener;
    private boolean isRunning = true;

    private Thread mMessageListenThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                connect(mHost, mPort);
            } catch (IOException e) {
                mListener.onDisconnected();
                e.printStackTrace();
            }
            if (mServer != null && mServer.isConnected() && !mServer.isClosed()) {
                Log.d(TAG, "isConnected");
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(mServer.getInputStream()));
                    if (!WELCOME_STR.equals(in.readLine())) {
                        Log.e(TAG, "This server is not a MessagePush server!");
                        in.close();
                        mServer.close();
                        mListener.onDisconnected();
                        return;
                    }
                    while (mServer.isConnected() && !mServer.isClosed() && isRunning) {
                        String str = in.readLine();
                        if (str == null || str.length() == 0) {
                            continue;
                        }
                        str = URLDecoder.decode(str, "UTF-8");
                        Gson gson = new Gson();
                        Message msg = gson.fromJson(str, Message.class);
                        mListener.onNewMessage(msg);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    mListener.onDisconnected();
                }

            }
        }
    });

    public Client(String host, int port, OnNewMessageListener listener) throws IOException {
        this.mHost = host;
        this.mPort = port;
        this.mListener = listener;

    }

    private void connect(String host, int port) throws IOException {
        mServer = new Socket(host, port);
        mListener.onConnected();
    }

    public void start() {
        mMessageListenThread.start();
    }

    public void stop() {
        try {
            mServer.close();
            isRunning = false;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        mListener.onDisconnected();
    }

    public interface OnNewMessageListener {
        void onNewMessage(Message message);
        void onConnected();
        void onDisconnected();
    }
}
