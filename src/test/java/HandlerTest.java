import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.annotations.EventBus;
import cn.evole.onebot.client.annotations.SubscribeEvent;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.client.interfaces.Listener;
import cn.evole.onebot.sdk.event.message.GroupMessageEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/27 14:59
 * @Description:
 */

@EventBus
public class HandlerTest implements Listener {
    static OneBotClient client;
    static Logger logger;

    public static void main(String[] args) throws InterruptedException {
        logger = LogManager.getLogger("OneBot Client1");
        BotConfig config = new BotConfig("ws://192.168.1.25:5800", "123456");
        client = OneBotClient.create(config, new HandlerTest()).open();
        //client.getEventsBus().register(new HandlerTest());
    }

    @SubscribeEvent(internal = true)
    public void msg1(GroupMessageEvent event){
        System.out.println(event.getMessage());
        System.out.println(event.getRawMessage());
    }
}
