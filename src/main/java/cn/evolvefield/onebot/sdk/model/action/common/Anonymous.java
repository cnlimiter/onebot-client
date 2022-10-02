package cn.evolvefield.onebot.sdk.model.action.common;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Anonymous {

    @SerializedName( "id")
    private long id;

    @SerializedName( "name")
    private String name;

    @SerializedName( "flag")
    private String flag;

}
