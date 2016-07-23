package ge.edu.freeuni.android.entertrainment.server.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nika Doghonadze.
 */
public class ChatEntry {
    private String username;
    private String text;
    private long timestamp;

    public ChatEntry(String username, String text, long timestamp) {
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:SS");
        Date date = new Date(timestamp);
        return simpleDateFormat.format(date);
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("message_text", text);
            jsonObject.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static ChatEntry fromJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String username = jsonObject.getString("username");
            String message_text = jsonObject.getString("message_text");
            long timestamp = jsonObject.getLong("timestamp");
            return new ChatEntry(username, message_text, timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
