package cn.evole.onebot.client.instances.event;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.utils.TransUtils;
import cn.evole.onebot.sdk.event.Event;
import cn.evole.onebot.sdk.event.message.GroupMessageEvent;
import cn.evole.onebot.sdk.event.message.GuildMessageEvent;
import cn.evole.onebot.sdk.event.message.PrivateMessageEvent;
import cn.evole.onebot.sdk.event.message.WholeMessageEvent;
import cn.evole.onebot.sdk.map.MessageMap;
import cn.evole.onebot.sdk.util.json.JsonsObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/26 23:32
 * @Description:
 */

public class EventFactory {
    protected final OneBotClient client;
    protected final EventsBusImpl eventManager;
    protected final Gson gson;

    public EventFactory(OneBotClient client) {
        this.client = client;
        this.eventManager = ((EventsBusImpl) client.getEventsBus());
        this.gson = new GsonBuilder().create();
    }


    public Event createEvent(JsonsObject json) {
        final Class<? extends Event> eventType = parseEventType(json);
        if (eventType == null) {
            return null; // 未知的消息类型
        }
        if (!eventManager.isSubscribed(eventType)) {
            // 如果不是消息事件，请确保命令系统可以接收事件。
            if (eventType != GroupMessageEvent.class
                    && eventType != PrivateMessageEvent.class
                    && eventType != WholeMessageEvent.class
                    && eventType != GuildMessageEvent.class
            ) {
                client.getLogger().warn("▌ 命令系统尚未支持");
                return null;
            }
        }

        return this.gson.fromJson(json.get(), eventType);
    }

    protected Class<? extends Event> parseEventType(JsonsObject object) {
        String type = "";
        val json = TransUtils.arrayToMsg(object);
        String postType = json.optString("post_type");
        switch (postType){
            case "message": {
                //消息类型
                switch (json.optString("message_type")){
                    case "group": {
                        //群聊消息类型
                        type = "groupMessage";
                        break;
                    }
                    case "private": {
                        //私聊消息类型
                        type = "privateMessage";
                        break;
                    }
                    case "guild": {
                        //频道消息，暂不支持私信
                        type = "guildMessage";
                        break;
                    }
                    default: {
                        type = "wholeMessage";
                        break;
                    }
                }
                break;
            }
            case "request": {
                //请求类型
                type = json.optString("request_type");
                break;
            }
            case "notice": {
                //通知类型
                type = json.optString("notice_type");
                break;
            }
            case "meta_event": {
                //周期类型
                type = json.optString("meta_event_type");
                break;
            }
            default: {
                type = "";
                break;
            }
        }
        if (type.isEmpty()) {
            client.getLogger().warn("▌ 未知消息类型");
            return null;
        }
        return MessageMap.messageMap.get(type);
    }
}
