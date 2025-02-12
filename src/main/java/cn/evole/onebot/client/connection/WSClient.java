package cn.evole.onebot.client.connection;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.core.Bot;
import lombok.Getter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.ConnectException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2023/4/4 2:20
 * @Description:
 */
public class WSClient extends WebSocketClient {
    @Getter private final Timer timer = new Timer();
    private final OneBotClient client;
    private int reconnectTimes = 1;
    public WSClient(OneBotClient client, URI uri) {
        super(uri);
        this.client = client;
        this.setConnectionLostTimeout(0);
        addHeader("User-Agent", "OneBot Client v4");
        addHeader("x-client-role", "Universal"); // koishi-adapter-onebot 需要这个字段
        if (!client.getConfig().getToken().isEmpty()) addHeader("Authorization", "Bearer " + client.getConfig().getToken());
        if (client.getConfig().getBotId() != 0) addHeader("X-Self-ID", String.valueOf(client.getConfig().getBotId()));
    }

    public Bot createBot(){
        return new Bot(this, client.getActionFactory());
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        client.getLogger().info("▌ §c已连接到服务器 {} §a┈━═☆", getURI());
        reconnectTimes = 1;
        //handshake.iterateHttpFields().forEachRemaining(s -> System.out.println(s + ": " + handshake.getFieldValue(s)));
    }

    @Override
    public void onMessage(String message) {
        client.getMsgHandler().handle(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (client.getConfig().isReconnect() && remote && reconnectTimes <= client.getConfig().getReconnectMaxTimes()) {
            reconnectWebsocket();
        } else client.getLogger().info("▌ §c服务器{}因{}已关闭", getURI(), reason);

    }

    @Override
    public void onError(Exception ex) {
        if (ex instanceof ConnectException && ex.getMessage().equals("Connection refused: connect")
                && client.getConfig().isReconnect()
                && reconnectTimes <= client.getConfig().getReconnectMaxTimes()) {
            reconnectWebsocket();
        } else client.getLogger().error("▌ §c服务器{}出现错误{}或未连接§a┈━═☆", getURI(), ex.getLocalizedMessage());
    }

    @Override
    public void send(String text) {
        if (isOpen()) {
            super.send(text);
            client.getLogger().debug("▌ §c向服务端{}发送{}", getURI(), text);
        } else {
            client.getLogger().debug("▌ §c向服务端{}发送{}失败", getURI(), text);
        }
    }

    @Override
    public void reconnect() {
        reconnectTimes++;
        super.reconnect();
        if (reconnectTimes == client.getConfig().getReconnectMaxTimes() + 1) {
            client.getLogger().info("▌ §c连接至{}已达到最大次数", getURI());
        }
    }

    public void reconnectWebsocket() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                reconnect();
            }
        };
        timer.schedule(timerTask, client.getConfig().getReconnectInterval() * 1000L);
    }


    public void stopWithoutReconnect(int code, String reason) {
        timer.cancel();
        close(code, reason);
    }
}
