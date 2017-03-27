package xyz.imxqd.pushclient.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_EMPTY = 1;
    private LinkedList<DMessage> mList;
    private Context mContext;

    public MessageAdapter(Context context) {
        mContext = context;
        mList = DB.list();
    }

    public void add(DMessage message) {
        mList.addFirst(message);
        notifyDataSetChanged();
    }

    public void update() {
        mList = DB.list();
        notifyDataSetChanged();
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = null;
        switch (viewType) {
            case TYPE_EMPTY:
                item = LayoutInflater.from(mContext)
                        .inflate(R.layout.empty_view, parent, false);
                break;
            case TYPE_NORMAL:
            default:
                item = LayoutInflater.from(mContext)
                        .inflate(R.layout.message_item, parent, false);
                break;
        }
        return new MessageHolder(item, viewType);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            DMessage msg = mList.get(position);
            holder.bind(msg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return TYPE_EMPTY;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 1;
        }
        return mList.size();
    }
}
