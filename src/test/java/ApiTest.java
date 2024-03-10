import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.sdk.action.ActionData;
import cn.evole.onebot.sdk.entity.MsgId;
import cn.evole.onebot.sdk.util.MsgUtils;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/27 1:45
 * @Description:
 */

public class ApiTest {



    public static void main(String[] args) throws InterruptedException {
        BotConfig config = new BotConfig("ws://192.168.1.25:5800");
        OneBotClient client = OneBotClient.create(config).open();

        Thread.sleep(1000);

        ActionData<MsgId> back =  client.getBot().sendGroupMsg(337631140L, MsgUtils.builder().text("123").build(), true);//发送群消息

        System.out.println(back.toString());
    }

}
