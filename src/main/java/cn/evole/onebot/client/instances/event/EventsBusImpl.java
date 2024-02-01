package cn.evole.onebot.client.instances.event;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.interfaces.EventsBus;
import cn.evole.onebot.client.interfaces.Listener;
import cn.evole.onebot.sdk.event.Event;
import lombok.Getter;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/26 22:57
 * @Description:
 */

public class EventsBusImpl implements EventsBus {
    private final OneBotClient client;
    private final net.kyori.event.EventBus<Event> bus;
    private final MethodSubscriptionAdapter<Listener> msa;
    @Getter private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    public EventsBusImpl(OneBotClient client) {
        this.client = client;
        this.bus = new SimpleEventBus<>(Event.class);
        this.msa = new SimpleMethodSubscriptionAdapter<>(bus, EventExecutorFactoryImpl.INSTANCE, MethodScannerImpl.INSTANCE);
    }

    @Override
    public void callEvent(Event event) {
        final PostResult result = bus.post(event);
        if (!result.wasSuccessful()) {
            client.getLogger().error("▌ 发布事件时出现意外异常");
            for (final Throwable t : result.exceptions().values()) {
                client.getLogger().warn(t.getMessage());
            }
        }
    }

    @Override
    public void register(Listener listener) {
        try {
            msa.register(listener);
        } catch (SimpleMethodSubscriptionAdapter.SubscriberGenerationException e) {
            msa.unregister(listener); // 取消订阅
            throw e; // 抛出错误
        }
        listeners.add(listener);
    }

    @Override
    public void unregister(Listener listener) {
        msa.unregister(listener);
        listeners.remove(listener);
    }

    public boolean isSubscribed(Class<? extends Event> type) {
        return bus.hasSubscribers(type);//是否被EventHandler注解
    }
}
