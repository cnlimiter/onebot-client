package cn.evolvefield.onebot.sdk.model.action.common;


import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/13 22:05
 * Version: 1.0
 */
@Data
public class ActionData<T> {

    @SerializedName( "status")
    private String status;
    @SerializedName( "retcode")
    private int retCode;
    @SerializedName( "data")
    private T data;
}
