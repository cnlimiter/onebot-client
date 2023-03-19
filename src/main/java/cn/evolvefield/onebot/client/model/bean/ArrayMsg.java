package cn.evolvefield.onebot.client.model.bean;

import cn.evolvefield.onebot.client.model.enums.MsgTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * Project: onebot-sdk
 * Author: cnlimiter
 * Date: 2023/2/10 1:30
 * Description:
 */
@Data
public class ArrayMsg {
    private MsgTypeEnum type;

    private Map<String, String> data;
}
