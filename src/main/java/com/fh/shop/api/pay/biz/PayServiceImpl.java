package com.fh.shop.api.pay.biz;

import com.alibaba.fastjson.JSONObject;
import com.fh.shop.api.common.*;
import com.fh.shop.api.config.WXConfig;
import com.fh.shop.api.order.mapper.OrderMapper;
import com.fh.shop.api.order.po.Order;
import com.fh.shop.api.paylog.mapper.PayLogMapper;
import com.fh.shop.api.paylog.po.PayLog;
import com.github.wxpay.sdk.WXPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ServerResponse createNative(Long memberId) {
        //获取会员对应的支付日志
        String payLogJson = RedisUtil.get(KeyUtil.buildPayLogKey(memberId));
        PayLog payLog = JSONObject.parseObject(payLogJson, PayLog.class);
        //获取支付相关信息
        String outTradeNo = payLog.getOutTradeNo();
        BigDecimal payMoney = payLog.getPayMoney();
        String orderId = payLog.getOrderId();
        //调用微信接口进行统一下单
        WXConfig config = new WXConfig();
        try {
            WXPay wxPay = new WXPay(config);
            Map<String,String> data = new HashMap<>();
            data.put("body","订单支付");
            BigDecimal money = BigDecimalUtil.mul(payMoney.toString(), "100");
            //订单号
            data.put("out_trade_no", outTradeNo);
            //把项目中的元转成分
            data.put("total_fee", money+"");
            data.put("notify_url", "http://www.baidu.com");
            data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
            DateUtil.se2te();
            Map<String, String> resp = wxPay.unifiedOrder(data);
            System.out.println(resp);
            String return_code = resp.get("return_code");
            String return_msg = resp.get("return_msg");
            if (!return_code.equals("SUCCESS")) {
                return ServerResponse.error(7000,return_msg);
            }
            String result_code = resp.get("result_code");
            String err_code_des = resp.get("err_code_des");
            if (!result_code.equals("SUCCESS")) {
                return ServerResponse.error(7000,err_code_des);
            }
            //证明return_code和result_code都是SUCCESS
            String code_url = resp.get("code_url");
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("code_url",code_url);
            resultMap.put("out",orderId);
            resultMap.put("totalPrice", payMoney.toString());
            return ServerResponse.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.error();
    }

    @Override
    public ServerResponse queryStatus(Long memberId) {
        WXConfig config = new WXConfig();
        try{
            String payLogJson = RedisUtil.get(KeyUtil.buildPayLogKey(memberId));
            PayLog payLog = JSONObject.parseObject(payLogJson, PayLog.class);
            String orderId = payLog.getOrderId();
            String outTradeNo = payLog.getOutTradeNo();
            WXPay wxPay = new WXPay(config);
            Map<String,String> data = new HashMap<>();
            data.put("out_trade_no",outTradeNo);
            int count = 0;
            while (true){
                Map<String,String> resp = wxPay.orderQuery(data);
                System.out.print(resp);
                String return_code = resp.get("return_code");
                String return_msg = resp.get("return_msg");
                if (!return_code.equals("SUCCESS")) {
                    return ServerResponse.error(7000,return_msg);
                }
                String result_code = resp.get("result_code");
                String err_code_des = resp.get("err_code_des");
                if (!result_code.equals("SUCCESS")) {
                    return ServerResponse.error(7000,err_code_des);
                }
                String trade_state = resp.get("trade_state");
                if (trade_state.equals("SUCCESS")) {
                    //证明支付成功
                    String transaction_id = resp.get("transaction_id");
                    //更新订单
                    Order order = new Order();
                    order.setId(orderId);
                    order.setPayTime(new Date());
                    order.setPayType(SystemConstant.OrderStatus.PA_SUCCESS);
                    orderMapper.updateById(order);
                    //更新支付日志
                    PayLog payLogInfo = new PayLog();
                    payLogInfo.setOutTradeNo(outTradeNo);
                    payLogInfo.setPayTime(new Date());
                    payLogInfo.setPayStatus(SystemConstant.PayStatus.PA_SUCCESS);
                    payLogInfo.setTransactionId(transaction_id);
                    payLogMapper.updateById(payLogInfo);
                    //删除redis中的支付日志
                    RedisUtil.delete(KeyUtil.buildPayLogKey(memberId));
                    //响应客户端
                    return ServerResponse.success();
                } else {
                    Thread.sleep(2000);
                    count++;
                    if (count > 60) {
                        //未支付成功
                        return ServerResponse.success(ResponseEnum.PAY_IS_FAIL);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }
}
