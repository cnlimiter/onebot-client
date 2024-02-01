package cn.evole.onebot.client.instances.event;

import cn.evole.onebot.client.annotations.EventHandler;
import cn.evole.onebot.client.interfaces.Listener;
import net.kyori.event.PostOrders;
import net.kyori.event.method.MethodScanner;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/26 23:10
 * @Description:
 */

public final class MethodScannerImpl implements MethodScanner<Listener> {
    public static final MethodScannerImpl INSTANCE = new MethodScannerImpl();

    private MethodScannerImpl() {
    }

    @Override
    public boolean shouldRegister(@NonNull Listener listener, @NonNull Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(EventHandler.class);
    }

    @Override
    public int postOrder(@NonNull Listener listener, @NonNull Method method) {
        return method.getAnnotation(EventHandler.class).internal() ? PostOrders.EARLY : PostOrders.NORMAL;
    }

    @Override
    public boolean consumeCancelledEvents(@NonNull Listener listener, @NonNull Method method) {
        return false;
    }

}
