package cn.evolvefield.onebot.sdk.connection;

import cn.evolvefield.onebot.sdk.config.BotConfig;
import cn.evolvefield.onebot.sdk.core.Bot;
import cn.evolvefield.onebot.sdk.handler.ActionHandler;
import cn.evolvefield.onebot.sdk.util.ReSchedulableTimerTask;
import cn.evolvefield.onebot.sdk.util.json.util.JsonsObject;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/10/1 16:53
 * Version: 1.0
 */
public class ModWebSocketClient extends WebSocketClient implements Connection {
    private final boolean debug = true;

    private final int reconnectInterval = 1000;


    private final int maxReconnectInterval = 30000;

    private final double reconnectDecay = 1.5;

    private int reconnectAttempts = 0;

    private final int maxReconnectAttempts;

    /*
    断线重连
     */
    private final boolean reconnect;

    private Timer reconnectTimer;

    private volatile boolean isReconnecting = false;

    private ReSchedulableTimerTask reconnectTimerTask;
    private final static String API_RESULT_KEY = "echo";
    private static final String FAILED_STATUS = "failed";
    private static final String RESULT_STATUS_KEY = "status";
    private static final String HEART_BEAT = "heartbeat";
    private static final String LIFE_CYCLE = "lifecycle";
    private final BlockingQueue<String> queue;

    private static final Logger log = LoggerFactory.getLogger(ModWebSocketClient.class);

    private final ActionHandler actionHandler;
    private int sendFlag = 0;
    private String result = null;
    public ModWebSocketClient(BotConfig config, URI uri, BlockingQueue<String> queue, ActionHandler actionHandler) {
        super(uri);
        this.queue = queue;
        this.actionHandler = actionHandler;
        this.reconnect = config.isReconnect();
        this.maxReconnectAttempts = config.getMaxReconnectAttempts();
    }

    public Bot createBot(){
        return new Bot(this, actionHandler);
    }


    /**
     * 带返回值的消息请求
     * @param text 请求json字符串
     * @return 返回json字符串
     */
    public String sendStr(String text){
        synchronized(this){
            sendFlag = 1;
            this.send(text);
            while(sendFlag != 0){
                log.debug("等待返回值中 =============== " + sendFlag);
            }
            return result;
        }
    }



    /**
     * 建立连接
     *
     * @param serverHandshake
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("已连接到服务器：{}，开始监听事件", this.uri);

    }

    /**
     * 收到消息
     *
     * @param message
     */
    @Override
    public void onMessage(String message) {
        JsonObject jsonObject = new JsonsObject(message).get();

        if (message != null && !jsonObject.has(HEART_BEAT) && !jsonObject.has(LIFE_CYCLE)) {//过滤心跳
            result = message;
            sendFlag = 0;
            log.debug("接收到原始消息{}", jsonObject.toString());
            if (jsonObject.has(API_RESULT_KEY)) {
                if (FAILED_STATUS.equals(jsonObject.get(RESULT_STATUS_KEY).getAsString())) {
                    log.error("请求失败: {}", jsonObject.get("wording").getAsString());
                }
                actionHandler.onReceiveActionResp(jsonObject);//请求执行
            } else {
                queue.add(message);//事件监听
            }

        }


    }

    @Override
    public void onClose(int i, String s, boolean b) {
        result = null;
        sendFlag = 0;
        if (!reconnect) {
            // 调用close 方法
            this.close(i, s);
        } else {
            if (!isReconnecting) {
                restartReconnectionTimer();
            }
            isReconnecting = true;
        }
    }

    /**
     * 出现异常
     *
     * @param e
     */
    @Override
    public void onError(Exception e) {
        result = null;
        sendFlag = 0;
        log.warn(e.getMessage());
    }

    @Override
    public void create() {
        super.connect();
    }

    /*
    重连方法
     */
    private void restartReconnectionTimer() {
        cancelReconnectionTimer();
        reconnectTimer = new Timer("reconnectTimer");
        reconnectTimerTask = new ReSchedulableTimerTask() {
            @Override
            public void run() {
                if (reconnectAttempts >= maxReconnectAttempts) {
                    cancelReconnectionTimer();
                    if (debug) {
                        log.info("以达到最大重试次数:" + maxReconnectAttempts + "，已停止重试!!!!");
                    }
                }
                reconnectAttempts++;
                try {
                    boolean isOpen = reconnectBlocking();
                    if (isOpen) {
                        if (debug) {
                            log.info("连接成功，重试次数为:" + reconnectAttempts);
                        }
                        cancelReconnectionTimer();
                        reconnectAttempts = 0;
                        isReconnecting = false;
                    } else {
                        if (debug) {
                            log.info("连接失败，重试次数为:" + reconnectAttempts);
                        }
                        double timeoutd = reconnectInterval * Math.pow(reconnectDecay, reconnectAttempts);
                        int timeout = Integer.parseInt(new java.text.DecimalFormat("0").format(timeoutd));
                        timeout = Math.min(timeout, maxReconnectInterval);
                        log.info(timeout + "");
                        reconnectTimerTask.re_schedule2(timeout);
                    }
                } catch (InterruptedException e) {
                    log.warn(e.getMessage());
                }
            }
        };
        reconnectTimerTask.schedule(reconnectTimer, reconnectInterval);
    }

    private void cancelReconnectionTimer() {
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
            reconnectTimer = null;
        }

        if (reconnectTimerTask != null) {
            reconnectTimerTask.cancel();
            reconnectTimerTask = null;
        }
    }
}
