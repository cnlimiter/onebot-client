package cn.evolvefield.onebot.client.connection;

import cn.evolvefield.onebot.client.config.BotConfig;
import cn.evolvefield.onebot.client.core.Bot;
import cn.evolvefield.onebot.client.handler.ActionHandler;
import cn.evolvefield.sdk.fastws.client.FastWSClient;
import cn.evolvefield.sdk.fastws.client.config.WSConfig;
import cn.evolvefield.sdk.fastws.client.core.model.NioModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/10/1 17:01
 * Version: 1.0
 */
public class ConnectFactory {
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(0, 30,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    private final BotConfig config;
    private final BlockingQueue<String> queue;
    private final ActionHandler actionHandler;

    public FastWSClient app;
    public Bot bot;

    public WSClient ws;
    /**
     *
     * @param config 配置
     * @param queue 队列消息
     */
    public ConnectFactory(BotConfig config, BlockingQueue<String> queue){
        this.config = config;
        this.queue = queue;
        this.actionHandler = new ActionHandler();
        this.app = createWebsocketClient();
        this.bot = new Bot(app.getModelCache().get(ws.id), actionHandler);

    }



    /**
     * 创建websocket客户端(支持cqhttp和mirai类型)
     * @return 连接示例
     */
    public FastWSClient createWebsocketClient(){
        FastWSClient application = FastWSClient.run(NioModel.class, threadPool);
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
        String url = builder.toString();
        this.ws = new WSClient(queue, actionHandler);
        application.connect(new WSConfig(url, ws));
        return application;
    }

    public void stop(){
        if (app != null){
            app.close();
            app.destroy();
        }
        this.threadPool.shutdownNow();
    }


}
