package cn.evole.onebot.client.util;

import cn.evole.onebot.sdk.util.json.GsonUtils;
import com.google.gson.JsonObject;
import lombok.val;

/**
 * TransUtils
 *
 * @author cnlimiter
 * @version 1.0
 * @description cq消息转换
 * @date 2024/3/2 11:33
 */
public class TransUtils {

    public static JsonObject arrayToString(JsonObject rawJson){
        if (rawJson.has("message") && GsonUtils.isArrayNode(rawJson, "message")){
            val rawMessage = GsonUtils.getAsJsonArray(rawJson, "message").toString();
            rawJson.addProperty("message", rawMessage);
        }
        return rawJson;
    }
}
