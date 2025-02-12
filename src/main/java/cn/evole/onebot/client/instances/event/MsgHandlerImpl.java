package cn.evole.onebot.client.instances.event;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.interfaces.MsgHandler;
import cn.evole.onebot.client.utils.TransUtils;
import cn.evole.onebot.sdk.event.Event;
import cn.evole.onebot.sdk.event.message.GroupMessageEvent;
import cn.evole.onebot.sdk.event.message.GuildMessageEvent;
import cn.evole.onebot.sdk.event.message.PrivateMessageEvent;
import cn.evole.onebot.sdk.util.GsonUtils;
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

    public static final String META_EVENT = "meta_event_type";
    private static final String META_HEART_BEAT = "heartbeat";
    private static final String META_LIFE_CYCLE = "lifecycle";

    protected final OneBotClient client;
    protected final Object lck = new Object();

    public MsgHandlerImpl(OneBotClient client) {
        this.client = client;
    }

    @Override
    public void handle(String msg) {
        if (msg == null) {
            client.getLogger().warn("▌ §c消息体为空");
            return;
        }
        try {
            val json2 = TransUtils.toArray(GsonUtils.parse(msg));
            client.getLogger().debug(json2.toString());
            client.getEventExecutor().execute(() -> {
                synchronized (lck) {
                    event(json2);
                }
            });

        } catch (
                JsonSyntaxException e) {
            client.getLogger().error("▌ §cJson语法错误:{}", msg);
        }

    }

    /**
     * 处理接收到的JSON对象形式的事件
     * 此方法首先尝试根据JSON对象执行一个动作，然后根据JSON对象创建一个事件对象
     * 如果创建的事件对象不为空，则根据条件判断是否执行命令，或将其发布到事件总线
     *
     * @param json 包含事件信息的JSON对象
     */
    protected void event(JsonObject json) {
        // 执行与JSON对象关联的动作
        executeAction(json);

        // 根据JSON对象创建Event实例
        Event event = client.getEventFactory().createEvent(json);

        // 如果创建的事件为空，则直接返回，不再进行后续处理
        if (event == null) {
            return;
        }

        // 尝试执行命令，如果执行失败，则将事件发布到事件总线上
        if (!executeCommand(event)) {
            client.getEventsBus().callEvent(event);
        }
    }



    /**
     * 执行一个JSON对象所代表的动作
     * 此方法主要用于处理和执行通过JSON对象描述的动作，根据JSON中的内容决定如何处理
     *
     * @param json 包含动作信息的JSON对象
     */
    protected void executeAction(JsonObject json) {
        // 检查JSON对象中是否包含API结果键
        if (json.has(API_RESULT_KEY)) {
            // 判断动作执行结果是否为失败
            if (RESULT_STATUS_FAILED.equals(GsonUtils.getAsString(json, RESULT_STATUS_KEY))) {
                // 如果执行失败，记录警告日志
                client.getLogger().warn("▌ §c请求失败: {}", GsonUtils.getAsString(json, "wording"));
            } else {
                // 如果执行成功，调用动作工厂的回调方法处理接收到的动作响应
                client.getActionFactory().onReceiveActionResp(json);//请求执行
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
