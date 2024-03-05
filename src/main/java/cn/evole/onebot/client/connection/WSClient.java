package cn.evole.onebot.client.connection;

import cn.evole.onebot.client.core.Bot;
import cn.evole.onebot.client.factory.ActionFactory;
import cn.evole.onebot.client.util.TransUtils;
import cn.evole.onebot.sdk.util.json.GsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.BlockingQueue;

/**
 * Project: onebot-client
 * Author: cnlimiter
 * Date: 2023/4/4 2:20
 * Description:
 */
public class WSClient extends WebSocketClient {
    public static final Logger log = LogManager.getLogger("WSClient");
    private static final String META_EVENT = "meta_event_type";
    private final static String API_RESULT_KEY = "echo";
    private static final String FAILED_STATUS = "failed";
    private static final String RESULT_STATUS_KEY = "status";
    private static final String HEART_BEAT = "heartbeat";
    private static final String LIFE_CYCLE = "lifecycle";
    private final BlockingQueue<JsonObject> queue;
    private final ActionFactory actionFactory;

    public WSClient(URI uri, BlockingQueue<JsonObject> queue, ActionFactory actionFactory) {
        super(uri);
        this.queue = queue;
        this.actionFactory = actionFactory;
    }

    public Bot createBot(){
        return new Bot(this, actionFactory);
    }


    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("▌ §c已连接到服务器 §a┈━═☆");
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonObject json = TransUtils.arrayToString(GsonUtils.parse(message));
            if (json.has(META_EVENT)) return;//过滤心跳
            log.debug("▌ §c接收到原始消息{}", json.toString());
            if (json.has(API_RESULT_KEY)) {
                if (FAILED_STATUS.equals(GsonUtils.getAsString(json, RESULT_STATUS_KEY))) {
                    log.debug("▌ §c请求失败: {}", GsonUtils.getAsString(json, "wording"));
                } else
                    actionFactory.onReceiveActionResp(json);//请求执行
            } else if (!queue.offer(json)){//事件监听
                log.error("▌ §c监听错误: {}", message);
            }
        } catch (JsonSyntaxException e) {
            log.error("▌ §cJson语法错误:{}", message);
        } catch (NullPointerException e) {
            log.error("▌ §c意外的null:{}", e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("▌ §c服务器因{}已关闭",  reason);
    }

    @Override
    public void onError(Exception ex) {
        log.error("▌ §c出现错误{}或未连接§a┈━═☆",  ex.getLocalizedMessage());
    }
}
