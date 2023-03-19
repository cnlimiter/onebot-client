import cn.evolvefield.onebot.client.config.BotConfig;
import cn.evolvefield.onebot.client.connection.ConnectFactory;
import cn.evolvefield.onebot.client.connection.ModWebSocketClient;
import cn.evolvefield.onebot.client.core.Bot;
import cn.evolvefield.onebot.sdk.response.group.GroupMemberInfoResp;
import cn.evolvefield.onebot.sdk.util.MsgUtils;


import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/10/2 19:35
 * Version: 1.0
 */
public class ApiTest {
    public static void main(String[] a) throws Exception {
        LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();//使用队列传输数据
        ModWebSocketClient service = ConnectFactory.createWebsocketClient(new BotConfig("ws://127.0.0.1:8080"),blockingQueue);
        service.create();//创建websocket客户端
        Bot bot = service.createBot();//创建机器人实例，以调用api
        bot.sendGroupMsg(123456, MsgUtils.builder().text("123").build(), true);//发送群消息
        GroupMemberInfoResp sender = bot.getGroupMemberInfo(123456, 123456, false).getData();//获取响应的群成员信息
        System.out.println(sender.toString());//打印
    }
}
