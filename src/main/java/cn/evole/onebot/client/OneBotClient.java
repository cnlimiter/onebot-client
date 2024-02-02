package cn.evole.onebot.client;

import cn.evole.onebot.client.connection.WSClient;
import cn.evole.onebot.client.core.Bot;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.client.instances.action.ActionFactory;
import cn.evole.onebot.client.instances.event.EventFactory;
import cn.evole.onebot.client.instances.event.EventsBusImpl;
import cn.evole.onebot.client.instances.event.MsgHandlerImpl;
import cn.evole.onebot.client.interfaces.EventsBus;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/26 22:58
 * @Description:
 */

@Getter
public final class OneBotClient {
    private final ExecutorService eventExecutor = Executors.newFixedThreadPool(2, r -> new Thread(r, "OneBot Event"));
    private final ExecutorService wsPool = Executors.newFixedThreadPool(2, r -> new Thread(r, "OneBot WS"));
    private final Logger logger;
    private final BotConfig config;
    private final EventsBus eventsBus;
    private final MsgHandlerImpl msgHandler;
    private final EventFactory eventFactory;
    private final ActionFactory actionFactory;

    private WSClient ws = null;
    private Bot bot = null;

    public OneBotClient(BotConfig config) {
        this.logger = LogManager.getLogger("OneBot Client");
        this.config = config;
        this.eventsBus = new EventsBusImpl(this);
        this.msgHandler = new MsgHandlerImpl(this);
        this.eventFactory = new EventFactory(this);
        this.actionFactory = new ActionFactory(this);
    }

    public void create() {
        wsPool.execute(() -> {
            StringBuilder builder = new StringBuilder();
            String token = config.isAccessToken() ? config.getToken() : "";
            builder.append(config.getUrl())
                    .append(config.isMirai() ? "/all?verifyKey=" + token + "&qq=" + config.getBotId() : token);
            try {
                ws = new WSClient(this, URI.create(builder.toString()));
                ws.connect();
                bot = ws.createBot();
            } catch (Exception e) {
                logger.error("▌ §c{}连接错误，请检查服务端是否开启 §a┈━═☆", URI.create(builder.toString()));
            }
        });
    }

    public boolean close() {
        if (!wsPool.isShutdown()) {
            wsPool.shutdown();
            try {
               return wsPool.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("▌ §c{} 打断关闭进程的未知错误 §a┈━═☆", e);
            }
        }
        return false;
    }


}
