import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.sdk.action.ActionData;
import cn.evole.onebot.sdk.entity.MsgId;
import cn.evole.onebot.sdk.util.MsgUtils;
import org.apache.logging.log4j.LogManager;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.impl.SimpleWsEchoServer;

import java.net.InetSocketAddress;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/27 1:45
 * @Description:
 */

public class ApiTest {



    public static void main(String[] args) throws InterruptedException {
        BotConfig config = new BotConfig("ws://127.0.0.1:9999");
        OneBotClient client = new OneBotClient(config);
        client.create();

        Thread.sleep(3000);

        ActionData<MsgId> back =  client.getBot().sendGroupMsg(720975019, MsgUtils.builder().text("123").build(), true);//发送群消息

        System.out.println(back.getData());
    }

}
