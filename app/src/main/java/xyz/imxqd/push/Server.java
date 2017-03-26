package xyz.imxqd.push;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by imxqd on 2017/3/26.
 */
public final class Server {

    private static Server mInstance;

    private final Queue<Message> mMessageQueue;
    private final int mPort;
    private Socket mClient;

    private ServerSocket mSSocket;
    private PrintWriter mPushWriter;

    private Thread mClientListenThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("ClientListenThread started.");
                mSSocket = new ServerSocket(mPort);
                while (true) {
                    mClient = mSSocket.accept();
                    mPushWriter = new PrintWriter(mClient.getOutputStream(),true);
                    System.out.println("New client connected.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }, "ClientListenThread");

    private Thread mPushThread = new Thread(new Runnable() {
        @Override
        public void run() {
            System.out.println("PushThread started.");
            while (true) {
                synchronized (mMessageQueue) {
                    if (isConnected() && mMessageQueue.size() > 0) {
                        Message msg = mMessageQueue.poll();
                        pushToClient(msg);
                    }
                    if (mMessageQueue.size() == 0) {
                        try {
                            mMessageQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }, "PushThread");

    private Server(int port) {
        System.out.println("new Server");
        mPort = port;
        mMessageQueue = new LinkedList<Message>();
        mClientListenThread.start();
        mPushThread.start();
    }

    public static void init() {
        if (mInstance == null) {
            mInstance = new Server(73);
        }
    }

    public static void init(int port) {
        if (mInstance == null) {
            mInstance = new Server(port);
        }
    }

    public static Server getInstance() {
        return mInstance;
    }

    public void push(Message message) {
        System.out.println("pushToQueue : " + message);
        synchronized (mMessageQueue) {
            mMessageQueue.add(message);
            mMessageQueue.notifyAll();
        }
    }

    private boolean pushToClient(Message message) {
        System.out.println("pushToClient : " + message);
        if (mClient.isConnected() && !mClient.isClosed()) {
            mPushWriter.println(message.toURLString());
            mPushWriter.flush();
            System.out.println("success");
            return true;
        } else {
            System.out.println("fail");
            return false;
        }
    }

    public boolean isConnected() {
        if (mClient != null && mClient.isConnected() && !mClient.isClosed() &&
                mPushWriter != null ) {
            try {
                mClient.sendUrgentData(' ');
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
