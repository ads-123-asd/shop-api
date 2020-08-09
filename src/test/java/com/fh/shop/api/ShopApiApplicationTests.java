package com.fh.shop.api;

import com.fh.shop.api.mq.MQSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShopApiApplicationTests {

    @Autowired
    private MQSender mqSender;

    @Test
    void contextLoads() {

    }

    @Test
    public void testSendMsg() {
        mqSender.sendMail("你好！！！！");
    }

    @Test
    public void test2() {
        mqSender.sendMsg1("奥德赛");
    }

    @Test
    public void test3() {
        mqSender.sendMsg2("阿萨德");
    }

}
