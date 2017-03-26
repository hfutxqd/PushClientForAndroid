package xyz.imxqd.push;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by imxqd on 2017/3/26.
 */
public class Message {
    private long time;
    private String content;

    public Message() {
    }

    public Message(String content) {
        this.time = System.currentTimeMillis();
        this.content = content;
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

    protected String toURLString() {
        Gson gson = new Gson();
        String str = gson.toJsonTree(this).toString();
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String toString() {
        return "DMessage{" +
                "time=" + time +
                ", content='" + content + '\'' +
                '}';
    }
}
