package xyz.imxqd.pushclient.ui.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import xyz.imxqd.pushclient.R;

/**
 * Created by imxqd on 2017/3/26.
 */

public class ConfigServerDialog extends DialogFragment {
    public static final String PATTERN_IP = "((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}";
    public static final String PATTERN_HOSTNAME = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";


    private EditText etHost;
    private EditText etPort;
    private CheckBox cbAuto;
    private TextInputLayout tiHost;
    private TextInputLayout tiPort;
    private View mRoot;
    private Callback mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.dialog_config_server, container, false);
        initViews();
        initCancel();
        initConfirm();
        return mRoot;
    }

    protected final <T extends View> T f(@IdRes int id) {
        return (T) mRoot.findViewById(id);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void initViews() {
        etHost = f(R.id.et_server_host);
        etPort = f(R.id.et_server_port);
        cbAuto = f(R.id.cb_auto_connect);
        tiHost = f(R.id.et_server_host_layout);
        tiPort = f(R.id.et_server_port_layout);
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (preferences.contains("host")) {
            etHost.setText(preferences.getString("host", ""));
            etPort.setText(preferences.getString("port", ""));
            cbAuto.setChecked(preferences.getBoolean("auto", false));
        }
    }

    private boolean check() {
        String host = etHost.getText().toString();
        String port = etPort.getText().toString();
        if (TextUtils.isEmpty(host)) {
            tiHost.setError(getString(R.string.error_no_empty));
            return false;
        }
        if (!host.matches(PATTERN_IP) && !host.matches(PATTERN_HOSTNAME)) {
            tiHost.setError(getString(R.string.error_host_error));
            return false;
        }
        tiHost.setError(null);
        if (TextUtils.isEmpty(port)) {
            tiPort.setError(getString(R.string.error_no_empty));
            return false;
        }
        if(Integer.valueOf(port) > 65535) {
            tiPort.setError(getString(R.string.error_out_of_range));
            return false;
        }
        tiPort.setError(null);
        return true;
    }

    private void initConfirm() {
        TextView tvConfirm = f(R.id.tv_confirm_panel);
        tvConfirm.setText(R.string.action_connect);
        tvConfirm.setTextColor(getResources().getColor(R.color.colorAccent));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    String host = etHost.getText().toString();
                    String port = etPort.getText().toString();
                    boolean auto = cbAuto.isChecked();
                    getActivity().getPreferences(Context.MODE_PRIVATE).edit()
                            .putString("host", host)
                            .putString("port", port)
                            .putBoolean("auto", auto)
                            .apply();
                    if (mCallback != null) {
                        mCallback.onServerConfigured(host, Integer.valueOf(port));
                    }
                    dismiss();
                }
            }
        });
    }

    private void initCancel() {
        TextView tvCancel = f(R.id.tv_cancel_panel);
        tvCancel.setText(R.string.action_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface Callback {
        void onServerConfigured(String host, int port);
    }
}
