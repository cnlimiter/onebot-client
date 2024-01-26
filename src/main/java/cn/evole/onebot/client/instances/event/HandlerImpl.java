package cn.evole.onebot.client.instances.event;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.connection.WSClient;
import cn.evole.onebot.client.interfaces.event.Handler;
import cn.evole.onebot.client.utils.TransUtils;
import cn.evole.onebot.sdk.event.Event;
import cn.evole.onebot.sdk.event.message.GroupMessageEvent;
import cn.evole.onebot.sdk.event.message.GuildMessageEvent;
import cn.evole.onebot.sdk.event.message.PrivateMessageEvent;
import cn.evole.onebot.sdk.util.json.JsonsObject;
import com.google.gson.JsonSyntaxException;
import lombok.val;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/27 0:04
 * @Description:
 */

public class HandlerImpl implements Handler {
    private final static String API_RESULT_KEY = "echo";

    private static final String RESULT_STATUS_KEY = "status";
    private static final String RESULT_STATUS_FAILED = "failed";

    private static final String META_EVENT = "meta_event_type";
    private static final String META_HEART_BEAT = "heartbeat";
    private static final String META_LIFE_CYCLE = "lifecycle";

    protected final OneBotClient client;
    protected final WSClient ws;
    protected final Object lck = new Object();

    public HandlerImpl(OneBotClient client, WSClient ws) {
        this.client = client;
        this.ws = ws;
    }

    @Override
    public void handle(String msg) {
        try {
            if (msg == null) client.getLogger().warn("▌ §c消息体为空");
            val json = TransUtils.arrayToMsg(new JsonsObject(msg));
            if (!META_HEART_BEAT.equals(json.optString(META_EVENT))) {
                client.getEventExecutor().execute(() -> {
                    synchronized (lck) {
                        event(json);
                    }
                });
            }
        } catch (
                JsonSyntaxException e) {
            client.getLogger().error("▌ §cJson语法错误:{}", msg);
        }

    }

    protected void event(JsonsObject json) {
        executeAction(json);
        Event event = client.getEventFactory().createEvent(json);
        if (event == null) {
            return;
        }
        if (!executeCommand(event)) {
            client.getEventManager().callEvent(event);
        }
    }



    protected void executeAction(JsonsObject json) {
        if (json.has(API_RESULT_KEY)) {
            if (RESULT_STATUS_FAILED.equals(json.optString(RESULT_STATUS_KEY))) {
                client.getLogger().debug("▌ §c请求失败: {}", json.optString("wording"));
            } else
                ws.getActionHandler().onReceiveActionResp(json);//请求执行
        }
    }

    //todo 命令系统
    protected boolean executeCommand(Event event) {
        if (!(
                event instanceof GroupMessageEvent
                        || event instanceof PrivateMessageEvent
                        || event instanceof GuildMessageEvent
        ))
            return false;

        return false;
    }
}
