package cn.evole.onebot.client.instances.action;

import cn.evole.onebot.client.OneBotClient;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/14 15:06
 * Version: 1.0
 */
public class ActionSendUnit {
    private final OneBotClient client;
    private final WebSocket channel;
    private final long requestTimeout;
    protected final Object lck = new Object();
    private JsonObject resp;


    public ActionSendUnit(OneBotClient client, WebSocket channel) {
        this(client, channel, 3000L);
    }

    /**
     * @param channel        {@link WebSocket}
     * @param requestTimeout Request Timeout
     */
    public ActionSendUnit(OneBotClient client, WebSocket channel, long requestTimeout) {
        this.client = client;
        this.channel = channel;
        this.requestTimeout = requestTimeout;
    }

    /**
     * @param req Request json data
     * @return Response json data
     * @throws InterruptedException exception
     */
    public JsonObject send(JsonObject req) throws InterruptedException {
        synchronized (channel) {
            client.getLogger().debug(String.format("[Action] %s", req.toString()));
            channel.send(req.toString());
        }
        synchronized (this) {
            this.wait(requestTimeout);
        }
        return resp;
    }

    /**
     * @param resp Response json data
     */
    public void onCallback(JsonObject resp) {
        this.resp = resp;
        synchronized (lck) {
            lck.notify();
        }
    }
}
