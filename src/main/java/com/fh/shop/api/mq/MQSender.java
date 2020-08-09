package com.fh.shop.api.mq;

import com.alibaba.fastjson.JSONObject;
import com.fh.shop.api.config.MQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMail(String info) {
        amqpTemplate.convertAndSend(MQConfig.MAILEXCHANGE,MQConfig.MAILROUTEKEY,"你好============"+info);
    }

    public void sendMailMessage(MailMessage mailMessage) {
        String mailJson = JSONObject.toJSONString(mailMessage);
        amqpTemplate.convertAndSend(MQConfig.MAILEXCHANGE,MQConfig.MAILROUTEKEY,mailJson);
    }

    public void sendMsg1(String info) {
        rabbitTemplate.convertAndSend(MQConfig.GOODS_FUNOUT_EXCHANGE,"",info);
    }

    public void sendMsg2(String info) {
        rabbitTemplate.convertAndSend(MQConfig.GOODS_TOPIC_EXCHANGE,"c.info.t",info);
    }

}
