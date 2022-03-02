package com.qzlnode.netdisc.pojo.tencent;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "tencent.user-server",ignoreUnknownFields = true)
public class TencentUser {

    private String secretId;

    private String secretKey;

    private String region;
}
