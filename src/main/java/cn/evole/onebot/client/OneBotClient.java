package cn.evole.onebot.client;

import cn.evole.onebot.client.connection.WSClient;
import cn.evole.onebot.client.core.Bot;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.client.instances.action.ActionFactory;
import cn.evole.onebot.client.instances.event.EventFactory;
import cn.evole.onebot.client.instances.event.EventsBusImpl;
import cn.evole.onebot.client.instances.event.MsgHandlerImpl;
import cn.evole.onebot.client.interfaces.EventsBus;
import cn.evole.onebot.client.interfaces.Listener;
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

    private OneBotClient(BotConfig config) {
        this.logger = LogManager.getLogger("OneBot Client");
        this.config = config;
        this.eventsBus = new EventsBusImpl(this);
        this.msgHandler = new MsgHandlerImpl(this);
        this.eventFactory = new EventFactory(this);
        this.actionFactory = new ActionFactory(this);
    }

    public static OneBotClient create(BotConfig config){
        return new OneBotClient(config);
    }

    public static OneBotClient create(BotConfig config, Listener... listeners){
        return new OneBotClient(config).registerEvents(listeners);
    }

    public OneBotClient open() {
        String token = config.getToken();
        long botId = config.getBotId();
        StringBuilder url = new StringBuilder();
        wsPool.execute(() -> {
            url.append(config.getUrl())
                    .append(config.isMirai() ? "/all?verifyKey=" + token + "&qq=" + config.getBotId() : "");
            try {
                ws = new WSClient(this, URI.create(url.toString()));
                ws.addHeader("User-Agent", "OneBot Client v4");
                ws.addHeader("x-client-role", "Universal"); // koishi-adapter-onebot 需要这个字段
                if (!config.getToken().isEmpty()) ws.addHeader("Authorization", "Bearer " + token);
                if (config.getBotId() != 0) ws.addHeader("X-Self-ID", String.valueOf(botId));
                ws.connect();
                bot = ws.createBot();
            } catch (Exception e) {
                logger.error("▌ §c{}连接错误，请检查服务端是否开启 §a┈━═☆", URI.create(url.toString()));
            }
        });
        return this;
    }

    public boolean close() {
        try {
            ws.closeBlocking();
        } catch (InterruptedException e) {
            logger.error("▌ §c{} 打断关闭进程的未知错误 §a┈━═☆", e);
            ws = null;
        }
        return threadStop(eventExecutor) && threadStop(wsPool);
    }

    public OneBotClient registerEvents(Listener... listeners){
        for (Listener c : listeners){
            getEventsBus().register(c);
        }
        return this;
    }

    private boolean threadStop(ExecutorService service){
        if (!service.isShutdown()) {
            service.shutdown();
            try {
                return service.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("▌ §c{} 打断关闭进程的未知错误 §a┈━═☆", e);
                service.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }
}
