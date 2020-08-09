package com.fh.shop.api.pay.biz;

import com.fh.shop.api.common.ServerResponse;

public interface PayService {

    ServerResponse createNative(Long memberId);

    ServerResponse queryStatus(Long memberId);

}
