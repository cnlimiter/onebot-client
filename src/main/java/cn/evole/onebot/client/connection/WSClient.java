package cn.evole.onebot.client.connection;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.core.Bot;
import cn.evole.onebot.client.instances.action.ActionFactory;
import cn.evole.onebot.client.instances.event.HandlerImpl;
import lombok.Getter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Project: onebot-client
 * Author: cnlimiter
 * Date: 2023/4/4 2:20
 * Description:
 */
public class WSClient extends WebSocketClient {
    private final OneBotClient client;
    private final HandlerImpl handler;
    @Getter private final ActionFactory actionFactory;

    public WSClient(OneBotClient client, URI uri, ActionFactory actionFactory) {
        super(uri);
        this.client = client;
        this.actionFactory = actionFactory;
        this.handler = new HandlerImpl(client);
    }

    public Bot createBot(){
        return new Bot(this, actionFactory);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        client.getLogger().info("▌ §c已连接到服务器 §a┈━═☆");
    }

    @Override
    public void onMessage(String message) {
        handler.handle(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        client.getLogger().info("▌ §c服务器因{}已关闭",  reason);
    }

    @Override
    public void onError(Exception ex) {
        client.getLogger().error("▌ §c出现错误{}或未连接§a┈━═☆",  ex.getLocalizedMessage());
    }

}
