package cn.evole.onebot.client.utils;

import cn.evole.onebot.sdk.entity.ArrayMsg;
import cn.evole.onebot.sdk.util.BotUtils;
import cn.evole.onebot.sdk.util.json.GsonUtil;
import cn.evole.onebot.sdk.util.json.JsonsObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Name: onebot-client / TransUtils
 * Author: cnlimiter
 * CreateTime: 2023/11/22 11:24
 * Description:
 */

public class TransUtils {

    public static JsonsObject arrayToMsg(JsonsObject json){
        if (json.has("message") && json.get().get("message").isJsonArray()){
            List<ArrayMsg> msg = GsonUtil.getGson().fromJson(json.get().getAsJsonArray("message"), new TypeToken<List<ArrayMsg>>() {
            }.getType());
            String code = BotUtils.arrayMsgToCode(msg);
            json.get().addProperty("message", code);
        }
        return json;
    }
}
