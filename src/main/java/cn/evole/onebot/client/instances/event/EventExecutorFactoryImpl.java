package cn.evole.onebot.client.instances.event;

import cn.evole.onebot.client.interfaces.event.Listener;
import cn.evole.onebot.sdk.event.Event;
import net.kyori.event.method.EventExecutor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/26 23:08
 * @Description:
 */

public class EventExecutorFactoryImpl implements EventExecutor.Factory<Event, Listener> {
    public static final EventExecutorFactoryImpl INSTANCE = new EventExecutorFactoryImpl();

    private EventExecutorFactoryImpl() {
    }

    @Override
    public @NonNull EventExecutor<Event, Listener> create(@NonNull Object object, @NonNull Method method) throws Exception {
        method.setAccessible(true);
        final Class<? extends Event> actualEventType = method.getParameterTypes()[0].asSubclass(Event.class);
        if (Modifier.isAbstract(actualEventType.getModifiers())) {
            throw new IllegalArgumentException("You cannot create listener for an abstract event type.");
        }
        final MethodHandle handle = MethodHandles.lookup().unreflect(method).bindTo(object);
        return (listener, event) -> {
            if (!actualEventType.isInstance(event))
                return;
            handle.invoke(event);
        };
    }

}
