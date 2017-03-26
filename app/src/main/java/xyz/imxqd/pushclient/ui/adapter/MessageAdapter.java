package xyz.imxqd.pushclient.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import xyz.imxqd.pushclient.R;
import xyz.imxqd.pushclient.dao.DB;
import xyz.imxqd.pushclient.dao.DMessage;

/**
 * Created by imxqd on 2017/3/26.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    private LinkedList<DMessage> mList;
    private Context mContext;

    public MessageAdapter(Context context) {
        mContext = context;
        mList = DB.list();
    }

    public void add(DMessage message) {
        mList.addFirst(message);
        notifyItemInserted(0);
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageHolder(LayoutInflater.from(mContext)
        .inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        DMessage msg = mList.get(position);
        holder.bind(msg);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
