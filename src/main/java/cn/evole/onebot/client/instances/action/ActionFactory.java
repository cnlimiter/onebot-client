package cn.evole.onebot.client.instances.action;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.sdk.action.ActionPath;
import cn.evole.onebot.sdk.util.GsonUtils;
import com.google.gson.JsonObject;
import lombok.val;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;


/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/14 15:05
 * Version: 1.0
 */

public class ActionFactory {
    private final OneBotClient client;
    /**
     * 请求回调数据
     */
    private final Map<String, ActionSendUnit> apiCallbackMap = new HashMap<>();
    /**
     * 用于标识请求，可以是任何类型的数据，OneBot 将会在调用结果中原样返回
     */
    private int echo = 0;

    public ActionFactory(OneBotClient client){
        this.client = client;
    }

    /**
     * 处理响应结果
     *
     * @param respJson 回调结果
     */
    public void onReceiveActionResp(JsonObject respJson) {
        String echo = GsonUtils.getAsString(respJson, "echo");
        ActionSendUnit actionSendUnit = apiCallbackMap.get(echo);
        if (actionSendUnit != null) {
            // 唤醒挂起的线程
            actionSendUnit.onCallback(respJson);
            apiCallbackMap.remove(echo);
        }
    }

    /**
     * @param ws Session
     * @param action  请求路径
     * @param params  请求参数
     * @return 请求结果
     */
    public JsonObject action(WebSocket ws, ActionPath action, JsonObject params) {
        if (!ws.isOpen()) {
            return null;
        }
        val reqJson = generateReqJson(action, params);
        ActionSendUnit actionSendUnit = new ActionSendUnit(client, ws);
        apiCallbackMap.put(reqJson.get("echo").getAsString(), actionSendUnit);
        JsonObject result = new JsonObject();
        try {
            result = actionSendUnit.send(reqJson);
        } catch (Exception e) {
            client.getLogger().warn("Request failed: {}", e.getMessage());
            result.addProperty("status", "failed");
            result.addProperty("retcode", -1);
        }
        return result;
    }

    /**
     * 构建请求数据
     * {"action":"send_private_msg","params":{"user_id":10001000,"message":"你好"},"echo":"123"}
     *
     * @param action 请求路径
     * @param params 请求参数
     * @return 请求数据结构
     */
    private JsonObject generateReqJson(ActionPath action, JsonObject params) {
        val json = new JsonObject();
        json.addProperty("action", action.getPath());
        json.add("params", params);
        json.addProperty("echo", echo++);
        return json;
    }
}
