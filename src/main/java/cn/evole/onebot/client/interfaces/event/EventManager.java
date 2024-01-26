package cn.evole.onebot.client.interfaces.event;


import cn.evole.onebot.sdk.event.Event;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/26 22:32
 * @Description:
 */

public interface EventManager {
    void callEvent(Event event);

    void register(Listener var2);

    void unregister(Listener var1);
}
