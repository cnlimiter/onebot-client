import cn.evole.onebot.client.OneBotClient;
import cn.evole.onebot.client.core.BotConfig;
import cn.evole.onebot.sdk.action.misc.ActionData;
import cn.evole.onebot.sdk.entity.ArrayMsg;
import cn.evole.onebot.sdk.entity.MsgId;
import cn.evole.onebot.sdk.enums.MsgType;
import cn.evole.onebot.sdk.util.GsonUtils;
import cn.evole.onebot.sdk.util.MsgUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project: onebot-client
 * @Author: cnlimiter
 * @CreateTime: 2024/1/27 1:45
 * @Description:
 */

public class JsonTest {



    public static void main(String[] args) {
        List<ArrayMsg> msg = new ArrayList<>();
        Map<String, String> data = new HashMap<>();
        data.put("file", "123");
        msg.add(new ArrayMsg().setType(MsgType.text).setData(data));

        System.out.println(GsonUtils.getGson().toJson(msg));

        Map<String, Object> msg2 = new HashMap<>();
        Map<String, Object> msg3 = new HashMap<>();
        msg2.put("message_type", "private");
        msg3.put("message_type", "group");
        List<Map<String, Object>> msgList = new ArrayList<>();
        msgList.add(msg2);
        msgList.add(msg3);
        System.out.println(GsonUtils.getGson().toJson(msgList));
    }

}
