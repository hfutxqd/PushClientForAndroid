package xyz.imxqd.pushclient.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import xyz.imxqd.pushclient.dao.DB;
import xyz.imxqd.pushclient.dao.DMessage;
import xyz.imxqd.pushclient.service.PushService;
import xyz.imxqd.pushclient.R;
import xyz.imxqd.pushclient.ui.adapter.MessageAdapter;
import xyz.imxqd.pushclient.ui.dialog.ConfigServerDialog;

public class MainActivity extends AppCompatActivity implements ConfigServerDialog.Callback, ServiceConnection, PushService.Callback {

    private static final String TAG = "MainActivity";

    private PushService.PushServiceBinder mPushService;
    private boolean isServiceConnected = false;
    private String mHost;
    private int mPort;

    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new MessageAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        connectToServer();
    }

    @Override
    protected void onDestroy() {
        if (isServiceConnected) {
            unbindService(this);
        }
        isServiceConnected = false;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_config) {
            ConfigServerDialog dialog = new ConfigServerDialog();
            dialog.setCallback(this);
            dialog.show(getFragmentManager(), "");
            return true;
        } else if (item.getItemId() == R.id.action_clear) {
            DB.deleteAll();
            mAdapter.update();
        }
        return false;
    }

    private void connectToServer() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        if (preferences.getBoolean("auto", false)) {
            String host = preferences.getString("host", "");
            String port = preferences.getString("port", "73");
            onServerConfigured(host, Integer.valueOf(port));
        } else {
            ConfigServerDialog dialog = new ConfigServerDialog();
            dialog.setCallback(this);
            dialog.show(getFragmentManager(), "");
        }
    }

    @Override
    public void onServerConfigured(String host, int port) {

        Intent intent = new Intent(this, PushService.class);
        intent.setAction(PushService.ACTION_CONNECT);
        intent.putExtra(PushService.ARG_HOST, host);
        intent.putExtra(PushService.ARG_PORT, port);
        startService(intent);
        if (!isServiceConnected) {
            bindService(intent, this, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        isServiceConnected = true;
        mPushService = (PushService.PushServiceBinder) service;
        mPushService.setCallback(this);
        mHost = mPushService.getHost();
        mPort = mPushService.getPort();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isServiceConnected = false;
        Log.d(TAG, "onServiceDisconnected: ");
    }

    @Override
    public void onNewMessage(DMessage message) {
        Log.d(TAG, "onNewMessage: " + message);
        mAdapter.add(message);
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onConnecting() {
        if (isServiceConnected) {
            mHost = mPushService.getHost();
            mPort = mPushService.getPort();
            initTitle(getString(R.string.action_connecting));
        }
        Log.d(TAG, "onConnecting: ");
    }

    @Override
    public void onConnected() {
        if (isServiceConnected) {
            mHost = mPushService.getHost();
            mPort = mPushService.getPort();
            initTitle(getString(R.string.action_connected));
        }
        Log.d(TAG, "onConnected: ");
    }

    @Override
    public void onDisconnected() {
        if (isServiceConnected) {
            mHost = mPushService.getHost();
            mPort = mPushService.getPort();
            initTitle(getString(R.string.action_disconnected));
        }
        Log.d(TAG, "onDisconnected: ");
    }

    private void initTitle(String action) {
        getSupportActionBar().setTitle(mHost + ":" + mPort);
        getSupportActionBar().setSubtitle(action);
    }
}
