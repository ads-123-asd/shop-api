package com.fh.shop.api.order.biz;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.order.vo.OrderParam;

public interface OrderService {

    ServerResponse generateOrderConfirm(Long memberId);

    ServerResponse generateOrder(OrderParam orderParam);

    void createOrder(OrderParam orderParam);

    ServerResponse getResult(Long memberId);
}
