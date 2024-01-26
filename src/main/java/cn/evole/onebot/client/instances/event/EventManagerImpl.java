package cn.evole.onebot.client.instances.event;

import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.interfaces.event.EventManager;
import cn.evole.onebot.client.interfaces.event.Listener;
import cn.evole.onebot.sdk.event.Event;
import net.kyori.event.EventBus;
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

public class EventManagerImpl implements EventManager {
    private final OneBotClient client;
    private final EventBus<Event> bus;
    private final MethodSubscriptionAdapter<Listener> msa;
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    public EventManagerImpl(OneBotClient client) {
        this.client = client;
        this.bus = new SimpleEventBus<>(Event.class);
        this.msa = new SimpleMethodSubscriptionAdapter<>(bus, EventExecutorFactoryImpl.INSTANCE, MethodScannerImpl.INSTANCE);
    }

    @Override
    public void callEvent(Event event) {
        final PostResult result = bus.post(event);
        if (!result.wasSuccessful()) {
            client.getLogger().error("Unexpected exception while posting event.");
            for (final Throwable t : result.exceptions().values()) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void register(Listener listener) {
        try {
            msa.register(listener);
        } catch (SimpleMethodSubscriptionAdapter.SubscriberGenerationException e) {
            msa.unregister(listener); // rollback
            throw e; // rethrow
        }
        listeners.add(listener);
    }

    @Override
    public void unregister(Listener listener) {
        msa.unregister(listener);
        listeners.remove(listener);
    }

    public boolean isSubscribed(Class<? extends Event> type) {
        return bus.hasSubscribers(type);
    }
}
