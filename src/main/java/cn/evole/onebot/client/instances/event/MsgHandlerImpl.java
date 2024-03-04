package cn.evole.onebot.client.instances.event;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.interfaces.MsgHandler;
import cn.evole.onebot.client.utils.TransUtils;
import cn.evole.onebot.sdk.event.Event;
import cn.evole.onebot.sdk.event.message.GroupMessageEvent;
import cn.evole.onebot.sdk.event.message.GuildMessageEvent;
import cn.evole.onebot.sdk.event.message.PrivateMessageEvent;
import cn.evole.onebot.sdk.util.json.GsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.val;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/27 0:04
 * @Description:
 */

public class MsgHandlerImpl implements MsgHandler {
    private final static String API_RESULT_KEY = "echo";

    private static final String RESULT_STATUS_KEY = "status";
    private static final String RESULT_STATUS_FAILED = "failed";

    private static final String META_EVENT = "meta_event_type";
    private static final String META_HEART_BEAT = "heartbeat";
    private static final String META_LIFE_CYCLE = "lifecycle";

    protected final OneBotClient client;
    protected final Object lck = new Object();

    public MsgHandlerImpl(OneBotClient client) {
        this.client = client;
    }

    @Override
    public void handle(String msg) {
        if (msg == null) client.getLogger().warn("▌ §c消息体为空");
        assert msg != null;
        try {
            val json = TransUtils.arrayToMsg(GsonUtils.parse(msg));
            client.getLogger().debug(json.toString());
            try {
                if (!META_HEART_BEAT.equals(GsonUtils.getAsString(json, META_EVENT))) {
                    client.getEventExecutor().execute(() -> {
                        synchronized (lck) {
                            event(json);
                        }
                    });
                }
            } catch (NullPointerException e) {
                client.getLogger().error("catch NullPointerException: ", e);
            }
        } catch (
                JsonSyntaxException e) {
            client.getLogger().error("▌ §cJson语法错误:{}", msg);
        }

    }

    protected void event(JsonObject json) {
        executeAction(json);
        Event event = client.getEventFactory().createEvent(json);
        if (event == null) {
            return;
        }
        if (!executeCommand(event)) {
            client.getEventsBus().callEvent(event);
        }
    }



    protected void executeAction(JsonObject json) {
        if (json.has(API_RESULT_KEY)) {
            try {
                if (RESULT_STATUS_FAILED.equals(GsonUtils.getAsString(json, RESULT_STATUS_KEY))) {
                    client.getLogger().debug("▌ §c请求失败: {}", GsonUtils.getAsString(json, "wording"));
                } else
                    client.getActionFactory().onReceiveActionResp(json);//请求执行
            } catch (NullPointerException e) {
                client.getLogger().error("catch NullPointerException: ", e);
            }
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
