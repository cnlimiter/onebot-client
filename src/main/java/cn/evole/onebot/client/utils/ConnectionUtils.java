package cn.evole.onebot.client.utils;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/8/8 下午7:44
 * @Description:
 */
public class ConnectionUtils {

    /**
     * 获取连接的 QQ 号
     *
     * @param session {@link WebSocket}
     * @return QQ 号
     */
    public static long parseSelfId(ClientHandshake session) {
        String selfIdStr = session.getFieldValue("x-self-id");
        try {
            return Long.parseLong(selfIdStr);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
