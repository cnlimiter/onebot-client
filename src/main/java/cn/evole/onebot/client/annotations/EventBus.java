package cn.evole.onebot.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/2/20 10:12
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBus {
}
