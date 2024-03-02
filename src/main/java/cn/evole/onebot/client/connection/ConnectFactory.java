package cn.evole.onebot.client.connection;

import cn.evole.onebot.client.core.Bot;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.client.factory.ActionFactory;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.concurrent.BlockingQueue;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/10/1 17:01
 * Version: 1.0
 */
@Getter
public class ConnectFactory {
    private final WSClient ws;
    private final Bot bot;
    /**
     *
     * @param config 配置
     * @param queue 队列消息
     */
    public ConnectFactory(BotConfig config, BlockingQueue<JsonObject> queue){
        String url = getUrl(config);
        this.ws = new WSClient(URI.create(url), queue, new ActionFactory(config));
        this.ws.connect();
        this.bot = ws.createBot();
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
    }


}
