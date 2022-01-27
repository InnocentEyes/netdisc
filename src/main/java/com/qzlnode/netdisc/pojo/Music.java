package com.qzlnode.netdisc.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qzlzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Music {

    private Integer musicId;

    @JsonIgnore
    private Integer userId;

    private String singer;

    private String songName;

    private String musicOriginName;

    private String musicType;

    private long musicSize;

    private String groupName;

    private String musicRemotePath;
}
