package cn.evole.onebot.client.util;

import cn.evole.onebot.sdk.entity.ArrayMsg;
import cn.evole.onebot.sdk.util.BotUtils;
import cn.evole.onebot.sdk.util.json.GsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Name: onebot-client / TransUtils
 * Author: cnlimiter
 * CreateTime: 2023/11/22 11:24
 * Description:
 */

public class TransUtils {

    public static JsonObject arrayToMsg(JsonObject json){
        if (json.has("message") && GsonUtils.isArrayNode(json, "message")){
            List<ArrayMsg> msg = GsonUtils.fromJson(json.getAsJsonArray("message"), new TypeToken<List<ArrayMsg>>() {
            }.getType());
            String code = BotUtils.arrayMsgToCode(msg);
            json.addProperty("message", code);
        }
        return json;
    }
}
