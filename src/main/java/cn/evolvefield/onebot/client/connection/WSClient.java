package cn.evolvefield.onebot.client.connection;

import cn.evolvefield.onebot.client.handler.ActionHandler;
import cn.evolvefield.onebot.sdk.util.json.JsonsObject;
import cn.evolvefield.sdk.fastws.client.WebSocketClient;
import cn.evolvefield.sdk.fastws.client.core.process.ActuatorAbstractAsync;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Project: onebot-client
 * Author: cnlimiter
 * Date: 2023/4/4 2:20
 * Description:
 */
public class WSClient extends ActuatorAbstractAsync {
    private static final Logger log = LoggerFactory.getLogger(WSClient.class);
    private final static String API_RESULT_KEY = "echo";
    private static final String FAILED_STATUS = "failed";
    private static final String RESULT_STATUS_KEY = "status";
    private static final String HEART_BEAT = "heartbeat";
    private static final String LIFE_CYCLE = "lifecycle";
    private final BlockingQueue<String> queue;
    private final ActionHandler actionHandler;

    public String id;

    public WSClient(BlockingQueue<String> queue, ActionHandler actionHandler) {
        this.queue = queue;
        this.actionHandler = actionHandler;
    }


    @Override
    public void onOpen(WebSocketClient webSocketClient) {
        log.error("▌ §c客户端:{} 已连接到服务器 §a┈━═☆", webSocketClient.getId());
        this.id = webSocketClient.getId();
    }

    @Override
    public void onClose(WebSocketClient webSocketClient) {

    }

    @Override
    public void onError(WebSocketClient webSocketClient, Throwable throwable) {
        log.error("▌ §c客户端:{} 出现错误 {} §a┈━═☆", webSocketClient.getId(), throwable.getLocalizedMessage());

    }

    @Override
    public void onMessageText(WebSocketClient webSocketClient, String message) {
        try {
            JsonObject jsonObject = new JsonsObject(message).get();

            if (message != null && !jsonObject.has(HEART_BEAT) && !jsonObject.has(LIFE_CYCLE)) {//过滤心跳
                log.debug("接收到原始消息{}", jsonObject);
                if (jsonObject.has(API_RESULT_KEY)) {
                    if (FAILED_STATUS.equals(jsonObject.get(RESULT_STATUS_KEY).getAsString())) {
                        log.debug("请求失败: {}", jsonObject.get("wording").getAsString());
                    } else
                        actionHandler.onReceiveActionResp(jsonObject);//请求执行
                } else {
                    queue.add(message);//事件监听
                }

            }
        } catch (
                JsonSyntaxException e) {
            log.error("Json语法错误:{}", message);
        }
    }

    @Override
    public void onMessageBinary(WebSocketClient webSocketClient, byte[] bytes) {

    }

    @Override
    public void onMessagePong(WebSocketClient webSocketClient, byte[] bytes) {

    }

    @Override
    public void onMessagePing(WebSocketClient webSocketClient, byte[] bytes) {

    }

    @Override
    public void onMessageContinuation(WebSocketClient webSocketClient, byte[] bytes) {

    }
}
