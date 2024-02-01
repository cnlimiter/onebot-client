package cn.evole.onebot.client.instances.action;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.sdk.util.json.JsonsObject;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;

import java.io.IOException;

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
    private JsonsObject resp;


    public ActionSendUnit(OneBotClient client, WebSocket channel) {
        this(client, channel, 3000L);
    }

    /**
     * @param channel        {@link WebSocket}
     * @param requestTimeout Request Timeout
     */
    public ActionSendUnit(OneBotClient client, WebSocket channel, Long requestTimeout) {
        this.client = client;
        this.channel = channel;
        this.requestTimeout = requestTimeout;
    }

    /**
     * @param req Request json data
     * @return Response json data
     * @throws IOException          exception
     * @throws InterruptedException exception
     */
    public JsonsObject send(JsonObject req) throws IOException, InterruptedException {
        client.getLogger().debug(String.format("▌ [Action] %s", req.toString()));
        synchronized (channel) {
            channel.send(req.toString());
        }
        synchronized (lck) {
            this.wait(requestTimeout);
        }
        return resp;
    }

    /**
     * @param resp Response json data
     */
    public void onCallback(JsonsObject resp) {
        this.resp = resp;
        synchronized (lck) {
            lck.notify();
        }
    }
}
