package cn.evole.onebot.client.internal;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.annotations.SubscribeEvent;
import cn.evole.onebot.client.interfaces.Listener;
import cn.evole.onebot.sdk.event.message.GroupMessageEvent;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/2/20 9:57
 * @Description:
 */

public class TestHandler implements Listener {
    OneBotClient client;
    public TestHandler(OneBotClient client){
        this.client = client;
    }

    @SubscribeEvent(internal = true)
    public void msg1(GroupMessageEvent event){
        client.getLogger().info(event);
    }
}
