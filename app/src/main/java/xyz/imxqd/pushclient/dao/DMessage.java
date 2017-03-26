package xyz.imxqd.pushclient.dao;

import xyz.imxqd.push.Message;

/**
 * Created by imxqd on 2017/3/26.
 */

public class DMessage {
    private int id;
    private long time;
    private String content;

    public DMessage() {
    }

    public DMessage(Message msg) {
        this.time = msg.getTime();
        this.content = msg.getContent();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "DMessage{" +
                "time=" + time +
                ", content='" + content + '\'' +
                '}';
    }
}
