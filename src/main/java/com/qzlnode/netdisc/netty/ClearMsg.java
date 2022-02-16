package com.qzlnode.netdisc.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClearMsg {

    private Integer currentUserId;

    private Integer currentPageUserId;
}
