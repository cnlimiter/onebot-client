import cn.evolvefield.onebot.sdk.config.BotConfig;
import cn.evolvefield.onebot.sdk.connection.ConnectFactory;
import cn.evolvefield.onebot.sdk.connection.ModWebSocketClient;
import cn.evolvefield.onebot.sdk.handler.Handler;
import cn.evolvefield.onebot.sdk.listener.SimpleListener;
import cn.evolvefield.onebot.sdk.listener.impl.GroupMessageListener;
import cn.evolvefield.onebot.sdk.model.event.EventDispatchers;
import cn.evolvefield.onebot.sdk.model.event.message.GroupMessageEvent;
import cn.evolvefield.onebot.sdk.model.event.message.PrivateMessageEvent;
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


    public static void main(String[] args) throws Exception {
        LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();//使用队列传输数据
        ModWebSocketClient service = ConnectFactory.createWebsocketClient(new BotConfig("ws://127.0.0.1:8080"),blockingQueue);
        service.create();//创建websocket客户端
        EventDispatchers dispatchers = new EventDispatchers(blockingQueue);//创建事件分发器
        GroupMessageListener groupMessageListener = new GroupMessageListener();//自定义监听规则
        groupMessageListener.addHandler("test", new Handler<GroupMessageEvent>() {
            @Override
            public void handle(GroupMessageEvent groupMessage) {
                logger.info(groupMessage.toString());

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

    }
}
