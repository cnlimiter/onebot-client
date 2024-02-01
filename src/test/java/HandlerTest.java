import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.annotations.EventHandler;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.client.interfaces.Listener;
import cn.evole.onebot.sdk.event.message.GroupMessageEvent;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/27 14:59
 * @Description:
 */

public class HandlerTest implements Listener {
    static OneBotClient client;

    public static void main(String[] args) throws InterruptedException {
        BotConfig config = new BotConfig("ws://127.0.0.1:9999");
        client = new OneBotClient(config);
        client.create();
        client.getEventsBus().register(new HandlerTest());
    }

    @EventHandler(internal = true)
    public void msg1(GroupMessageEvent event){
        System.out.println(event);
    }
}
