package xyz.imxqd.pushclient.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import xyz.imxqd.pushclient.R;
import xyz.imxqd.pushclient.dao.DMessage;

/**
 * Created by imxqd on 2017/3/26.
 */

public class MessageHolder extends RecyclerView.ViewHolder {
    TextView time;
    TextView content;
    int viewType;

    public void bind(DMessage msg) {
        if (viewType == MessageAdapter.TYPE_NORMAL) {
            time.setText(SimpleDateFormat.getInstance().format(new Date(msg.getTime())));
            content.setText(msg.getContent());
        }
    }
    public MessageHolder(View itemView, int type) {
        super(itemView);
        viewType = type;
        if (viewType == MessageAdapter.TYPE_NORMAL) {
            time = (TextView) itemView.findViewById(R.id.item_time);
            content = (TextView) itemView.findViewById(R.id.item_content);
        }
    }
}
