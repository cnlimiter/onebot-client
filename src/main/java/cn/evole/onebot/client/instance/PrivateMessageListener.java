package cn.evole.onebot.client.instance;


import cn.evole.onebot.client.interfaces.handler.DefaultHandler;
import cn.evole.onebot.client.interfaces.handler.Handler;
import cn.evole.onebot.sdk.event.message.PrivateMessageEvent;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/14 16:29
 * Version: 1.0
 */
public class PrivateMessageListener extends DefaultHandler<PrivateMessageEvent> {
    @Override
    public void onMessage(PrivateMessageEvent privateMessage) {
        //处理逻辑
        String message = privateMessage.getMessage();
        String[] split = message.split(" ");
        String key = split[0];
        Handler<PrivateMessageEvent> handler = getHandler(key);
        if (handler != null) {
            handler.handle(privateMessage);
        }
    }
}
