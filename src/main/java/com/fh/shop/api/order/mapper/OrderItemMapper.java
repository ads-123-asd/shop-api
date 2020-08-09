package com.fh.shop.api.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.api.order.po.OrderItem;

import java.util.List;

public interface OrderItemMapper extends BaseMapper<OrderItem> {

    void batchInserts(List<OrderItem> orderItemList);
    
}
