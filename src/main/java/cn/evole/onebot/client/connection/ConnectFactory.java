package cn.evole.onebot.client.connection;

import cn.evole.onebot.client.core.Bot;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.client.factory.ActionFactory;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.concurrent.BlockingQueue;

import static cn.evole.onebot.client.connection.WSClient.log;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/10/1 17:01
 * Version: 1.0
 */
public class ConnectFactory {
    private final ActionFactory actionFactory;
    @Getter public WSClient ws;
    @Getter public Thread wsThread;
    @Getter public Bot bot;
    /**
     *
     * @param config 配置
     * @param queue 队列消息
     */
    public ConnectFactory(BotConfig config, BlockingQueue<String> queue){
        this.actionFactory = new ActionFactory(config);
        String url = getUrl(config);
        try {
            this.wsThread = new Thread(() -> {
                this.ws = new WSClient(URI.create(url), queue, actionFactory);
                this.ws.connect();
                this.bot = ws.createBot();
            });
            this.wsThread.start();
        }catch (Exception e){
            log.error("▌ §c与{}连接错误，请检查服务端是否开启 §a┈━═☆", url);
        }
    }

    @NotNull
    private static String getUrl(BotConfig config) {
        StringBuilder builder = new StringBuilder();
        if (config.isMiraiHttp()){
            builder.append(config.getUrl());
            builder.append("/all");
            builder.append("?verifyKey=");
            if (config.isAccessToken()) {
                builder.append(config.getToken());
            }
            builder.append("&qq=");
            builder.append(config.getBotId());
        }
        else {
            builder.append(config.getUrl());
            if (config.isAccessToken()) {
                builder.append("?access_token=");
                builder.append(config.getToken());
            }
        }
        return builder.toString();
    }


    public void stop(){
        this.ws.close();
        this.wsThread.stop();
    }


}
