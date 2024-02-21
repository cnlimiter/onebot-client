package cn.evole.onebot.client.factory;

import cn.evole.onebot.client.interfaces.listener.EnableListener;
import cn.evole.onebot.client.interfaces.listener.Listener;
import cn.evole.onebot.client.util.ListenerUtils;
import cn.evole.onebot.client.util.TransUtils;
import cn.evole.onebot.sdk.event.Event;
import cn.evole.onebot.sdk.util.json.GsonUtils;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Project: onebot-client
 * Author: cnlimiter
 * Date: 2023/3/19 15:45
 * Description:
 */
@SuppressWarnings("unused")
public class ListenerFactory implements Runnable {
    private static final Logger log = LogManager.getLogger(ListenerFactory.class);
    //存储监听器对象
    protected List<Listener<?>> eventlistenerlist = new CopyOnWriteArrayList<>();
    //缓存类型与监听器的关系
    protected Map<Class<? extends Event>, List<Listener<?>>> cache = new ConcurrentHashMap<>();
    //线程池 用于并发执行队列中的任务
    protected ExecutorService service;
    protected BlockingQueue<String> queue;
    private boolean close = false;

    public ListenerFactory(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void addListener(Listener<?> Listener) {
        this.eventlistenerlist.add(Listener);
    }


    public void start() {
        start(1);
    }

    public void start(Integer threadCount) {
        if (threadCount <= 0) {
            threadCount = 1;
        }
        service = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            service.submit(this);
        }
    }


    public void stop() {
        this.close = true;
        this.cache.clear();
        this.eventlistenerlist.clear();
        this.service.shutdownNow();
    }

    @Override
    public void run() {
        try {
            while (!this.close) {
                this.runTask();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 执行任务
     */
    protected void runTask() {
        String message = this.getTask();//获取消息
        if (message.equals("null")) {
            log.debug("消息队列为空");
            return;
        }
        JsonObject msg = TransUtils.arrayToMsg(GsonUtils.parse(message));
        Class<? extends Event> messageType = ListenerUtils.parseEventType(msg, log);//获取消息对应的实体类型
        if (messageType == null) {
            return;
        }
        log.debug(String.format("接收到上报消息内容：%s", messageType));
        Event bean = GsonUtils.fromJson(msg.toString(), messageType);//将消息反序列化为对象
        List<Listener<?>> executes = this.cache.get(messageType);
        if (this.cache.get(messageType) == null){
            executes = getMethod(messageType);
            this.cache.put(messageType, executes);
        }

        for (Listener listener : executes) {
            listener.onMessage(bean);//调用监听方法
        }

    }

    /**
     * 从队列中获取任务
     *
     * @return 任务
     */
    protected String getTask() {
        try {
            return this.queue.take();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "null";
    }

    /**
     * 获取能监听器列表
     *
     * @param messageType 消息类型
     * @return 处理完的监听器列表
     */
    protected List<Listener<?>> getMethod(Class<? extends Event> messageType) {
        List<Listener<?>> listeners = new ArrayList<>();
        for (Listener<?> listener : this.eventlistenerlist) {
            try {
                try {
                    listener.getClass().getMethod("onMessage", messageType);//判断是否注册监听器
                } catch (NoSuchMethodException e) {
                    continue;//不支持则跳过
                }
                if (listener instanceof EnableListener) {
                    EnableListener<?> enableListener = (EnableListener<?>) listener;
                    if (!enableListener.enable()) {//检测是否开启该插件
                        continue;
                    }
                }
                listeners.add(listener);//开启后添加入当前类型的插件
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }
        }
        return listeners;
    }

    public List<Listener<?>> getListenerList() {
        return this.eventlistenerlist;
    }

    /**
     * 清除类型缓存
     */
    public void cleanCache() {
        this.cache.clear();
    }


}
