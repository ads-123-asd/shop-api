package com.fh.shop.api.order.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fh.shop.api.cart.po.Cart;
import com.fh.shop.api.cart.po.CartItem;
import com.fh.shop.api.common.*;
import com.fh.shop.api.config.MQConfig;
import com.fh.shop.api.exception.StockLessException;
import com.fh.shop.api.order.mapper.OrderItemMapper;
import com.fh.shop.api.order.mapper.OrderMapper;
import com.fh.shop.api.order.po.Order;
import com.fh.shop.api.order.po.OrderItem;
import com.fh.shop.api.order.vo.OrderConfirmVo;
import com.fh.shop.api.order.vo.OrderParam;
import com.fh.shop.api.paylog.mapper.PayLogMapper;
import com.fh.shop.api.paylog.po.PayLog;
import com.fh.shop.api.product.mapper.ProductMapper;
import com.fh.shop.api.recipient.biz.RecipientService;
import com.fh.shop.api.recipient.mapper.RecipientMapper;
import com.fh.shop.api.recipient.po.Recipient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RecipientService recipientService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RecipientMapper recipientMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public ServerResponse generateOrderConfirm(Long memberId) {
        //获取会员对应的收件人列表
        List<Recipient> recipientList = recipientService.findList(memberId);
        //获取会员对应的购物车信息
        String cartJson = RedisUtil.get(KeyUtil.buildCartKey(memberId));
        Cart cart = JSONObject.parseObject(cartJson, Cart.class);
        //组装要返回的信息
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        orderConfirmVo.setCart(cart);
        orderConfirmVo.setRecipientList(recipientList);
        return ServerResponse.success(orderConfirmVo);
    }

    @Override
    public ServerResponse generateOrder(OrderParam orderParam) {
        Long memberId = orderParam.getMemberId();
        //清除之前的标志位
        RedisUtil.delete(KeyUtil.buildOrderKey(memberId));
        RedisUtil.delete(KeyUtil.buildStockLessKey(memberId));
        //将订单信息发送到消息队列中
        String orderParamJson = JSONObject.toJSONString(orderParam);
        rabbitTemplate.convertAndSend(MQConfig.ORDEREXCHANGE,MQConfig.ORDERROUTEKEY,orderParamJson);
        return ServerResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderParam orderParam) {
        Long memberId = orderParam.getMemberId();
        String cartJson = RedisUtil.get(KeyUtil.buildCartKey(memberId));
        Cart cart = JSONObject.parseObject(cartJson, Cart.class);
        List<CartItem> cartItemList = cart.getCartItemList();
        //减库存[数据库乐观锁]
        //考虑到并发
        for (CartItem cartItem : cartItemList) {
            Long goodsId = cartItem.getGoodsId();
            int num = cartItem.getNum();
            int rowCount = productMapper.updateStock(goodsId, num);
            if (rowCount == 0) {
                //没更新成功  库存不足
                //及回滚  及提示
                throw new StockLessException("stock less");
            }
        }
        //获取收件人信息
        Long recipientId = orderParam.getRecipientId();
        Recipient recipient = recipientMapper.selectById(recipientId);
        //插入订单表
        Order order = new Order();
        //手动设置Id 通过雪花算法生产唯1标识
        String orderId = IdWorker.getIdStr();
        order.setId(orderId);
        order.setCreateTime(new Date());
        order.setRecipientor(recipient.getRecipientor());
        order.setPhone(recipient.getPhone());
        order.setAddress(recipient.getAddress());
        order.setUserId(memberId);
        BigDecimal totalPrice = cart.getTotalPrice();
        order.setTotalPrice(cart.getTotalPrice());
        order.setRecipientId(recipientId);
        int payType = orderParam.getPayType();
        order.setPayType(payType);
        order.setStatus(SystemConstant.OrderStatus.WAIT_PA);//未支付
        order.setTotalNum(cart.getTotalNum());
        orderMapper.insert(order);
        //插入订明细单表
        //批量插入
        //insert  into 表明（字段名1 ，字段名2），（字段名1 ，字段名2）

        List <OrderItem> OrderItemList = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(cartItem.getGoodsId());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setProductName(cartItem.getGoodsName());
            orderItem.setImgUrl(cartItem.getImgUrl());
            orderItem.setId(memberId);
            orderItem.setSubPrice(cartItem.getSubPrice());
            orderItem.setNum(cartItem.getNum());
            OrderItemList.add(orderItem);
        }
        //批量插入订单明细表
        orderItemMapper.batchInserts(OrderItemList);
        //插入支付日志表[下订单]
        PayLog payLog = new PayLog();
        String outTradeNo = IdWorker.getIdStr();
        payLog.setOutTradeNo(outTradeNo);
        payLog.setPayMoney(totalPrice);
        payLog.setUserId(memberId);
        payLog.setCreateTime(new Date());
        payLog.setOrderId(orderId);
        payLog.setPayStatus(SystemConstant.PayStatus.WAIT_PA);//等待支付
        payLog.setPayType(payType);
        payLogMapper.insert(payLog);
        //将支付日志存入redis中
        String payLogJson = JSONObject.toJSONString(payLog);
        RedisUtil.set(KeyUtil.buildOrderKey(memberId),payLogJson);
        //删除购物车中的信息
        RedisUtil.delete(KeyUtil.buildCartKey(memberId));
        //提交订单
        RedisUtil.set(KeyUtil.buildOrderKey(memberId),"ok");
    }

    @Override
    public ServerResponse getResult(Long memberId) {
        if(RedisUtil.exist(KeyUtil.buildStockLessKey(memberId))){
            //证明库存不足
            return ServerResponse.error(ResponseEnum.ORDER_STOCK_LESS);
        }
        if(RedisUtil.exist(KeyUtil.buildOrderKey(memberId))){
            //下订单成功
            return ServerResponse.success();
        }
        return ServerResponse.error(ResponseEnum.ORDER_SUCCESS_PD);
    }

}

