package cn.evole.onebot.client.util;

import cn.evole.onebot.sdk.event.Event;
import cn.evole.onebot.sdk.map.MessageMap;
import cn.evole.onebot.sdk.util.json.GsonUtils;
import com.google.gson.JsonObject;
import lombok.val;
import org.apache.logging.log4j.Logger;


/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/14 17:03
 * Version: 1.0
 */
public class ListenerUtils {

    /**
     * 获取消息对应的实体类型
     *
     * @param rawJson json
     * @return
     */
    public static Class<? extends Event> parseEventType(JsonObject rawJson, Logger log) {
        String type;
        String postType = GsonUtils.getAsString(rawJson, "post_type");
        switch (postType){
            case "message": {
                //消息类型
                val json = TransUtils.arrayToMsg(rawJson);
                switch (GsonUtils.getAsString(json, "message_type")){
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
                type = GsonUtils.getAsString(rawJson, "request_type");
                break;
            }
            case "notice": {
                //通知类型
                type = GsonUtils.getAsString(rawJson,"notice_type");
                break;
            }
            case "meta_event": {
                //周期类型
                type = GsonUtils.getAsString(rawJson,"meta_event_type");
                break;
            }
            default: {
                type = "";
                break;
            }
        }
        if (type.isEmpty()) {
            log.warn("▌ 未知消息类型");
            return null;
        }
        return MessageMap.messageMap.get(type);
    }
}
