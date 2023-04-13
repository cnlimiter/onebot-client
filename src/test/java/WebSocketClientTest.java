import cn.evolvefield.onebot.client.config.BotConfig;
import cn.evolvefield.onebot.client.connection.ConnectFactory;
import cn.evolvefield.onebot.client.core.Bot;
import cn.evolvefield.onebot.client.handler.EventBus;
import cn.evolvefield.onebot.client.handler.Handler;
import cn.evolvefield.onebot.client.listener.SimpleListener;
import cn.evolvefield.onebot.client.listener.impl.GroupMessageListener;
import cn.evolvefield.onebot.client.util.LogUtils;
import cn.evolvefield.onebot.sdk.event.message.GroupMessageEvent;
import cn.evolvefield.onebot.sdk.event.message.PrivateMessageEvent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/10/2 19:06
 * Version: 1.0
 */
public class WebSocketClientTest {

    public static LogUtils logger = LogUtils.getLog("test");


    public static void main(String[] args) throws Exception {
        LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();//使用队列传输数据
        ConnectFactory service = new ConnectFactory(
                new BotConfig("ws://127.0.0.1:8080"),blockingQueue)
                ;//创建websocket客户端
        Bot bot = service.ws.createBot();
        EventBus dispatchers = new EventBus(blockingQueue);//创建事件分发器
        GroupMessageListener groupMessageListener = new GroupMessageListener();//自定义监听规则
        groupMessageListener.addHandler("test", new Handler<GroupMessageEvent>() {
            @Override
            public void handle(GroupMessageEvent groupMessage) {
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

        dispatchers.start(10);//线程组处理任务

        dispatchers.stop();
        service.stop();



    }
}
