package com.qzlnode.netdisc.pojo.tencent;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "tencent.code-server",ignoreUnknownFields = true)
@Component
@Data
@Accessors(chain = true)
public class TenCentShortMessage {

    private static final String SIGNNAME = "个人云盘";

    //2分钟
    private static final String EXIPRE_TIME = "2";

    private String phonePerfix;

    private String sdkAppId;

    private String templateId;

    public static String getSign(){
        return SIGNNAME;
    }

    public static String getExipreTime(){
        return EXIPRE_TIME;
    }
}
