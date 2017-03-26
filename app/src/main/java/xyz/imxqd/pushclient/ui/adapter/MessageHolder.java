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

    public void bind(DMessage msg) {
        time.setText(SimpleDateFormat.getInstance().format(new Date(msg.getTime())));
        content.setText(msg.getContent());
    }
    public MessageHolder(View itemView) {
        super(itemView);
        time = (TextView) itemView.findViewById(R.id.item_time);
        content = (TextView) itemView.findViewById(R.id.item_content);
    }
}
