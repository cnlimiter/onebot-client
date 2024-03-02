import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.client.connection.ConnectFactory;
import cn.evole.onebot.client.core.Bot;
import cn.evole.onebot.client.factory.ListenerFactory;
import cn.evole.onebot.client.interfaces.handler.Handler;
import cn.evole.onebot.client.interfaces.listener.SimpleListener;
import cn.evole.onebot.client.instance.GroupMessageListener;
import cn.evole.onebot.sdk.event.message.PrivateMessageEvent;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/10/2 19:06
 * Version: 1.0
 */
public class WebSocketClientTest {

    public static Logger logger = LoggerFactory.getLogger("test");


    public static void main1(String[] args) throws Exception {
        LinkedBlockingQueue<JsonObject> blockingQueue = new LinkedBlockingQueue<>();//使用队列传输数据
        ConnectFactory service = new ConnectFactory(
                new BotConfig("ws://127.0.0.1:8080"), blockingQueue);//创建websocket客户端
        Bot bot = service.getWs().createBot();
        ListenerFactory dispatchers = new ListenerFactory(blockingQueue);//创建事件分发器
        GroupMessageListener groupMessageListener = new GroupMessageListener();//自定义监听规则
        groupMessageListener.addHandler("test", new Handler<cn.evole.onebot.sdk.event.message.GroupMessageEvent>() {
            @Override
            public void handle(cn.evole.onebot.sdk.event.message.GroupMessageEvent groupMessage) {
                bot.sendGroupMsg(337631140, groupMessage.getMessage(), false);

            }
        });//匹配关键字监听
        dispatchers.addListener(groupMessageListener);//注册监听
        dispatchers.addListener(new SimpleListener<PrivateMessageEvent>() {//私聊监听
            @Override
            public void onMessage(PrivateMessageEvent privateMessage) {
                logger.info(privateMessage.toString());
            }
        });//快速监听

        dispatchers.stop();
        service.stop();
    }
}
