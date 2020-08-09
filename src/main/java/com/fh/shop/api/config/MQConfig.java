package com.fh.shop.api.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String MAILEXCHANGE = "mailExchange";

    public static final String MAILQUEUE = "mailQueue";

    public static final String MAILROUTEKEY = "mail";

    public static final String GOODS_FUNOUT_EXCHANGE = "goodsFanoutExchange";

    public static final String GOODS_QUEUE_1 = "goodsQueue1";

    public static final String GOODS_QUEUE_2 = "goodsQueue2";

    public static final String GOODS_TOPIC_EXCHANGE = "goodsTopicExchange";

    public static final String GOODS_TOPIC_QUEUE_1 = "goodsTopicQueue1";

    public static final String GOODS_TOPIC_QUEUE_2 = "goodsTopicQueue2";

    public static final String GOODS_TOPIC_QUEUE_3 = "goodsTopicQueue3";

    public static final String ORDEREXCHANGE = "orderExchange";

    public static final String ORDERQUEUE = "orderQueue" ;

    public static final String ORDERROUTEKEY = "order";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDEREXCHANGE,true,false);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDERQUEUE,true);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ORDERROUTEKEY);
    }

    @Bean
    public TopicExchange goodsTopicExchange() {
        return new TopicExchange(GOODS_TOPIC_EXCHANGE,true,false);
    }

    @Bean
    public Queue goodsTopicQueue1() {
        return new Queue(GOODS_TOPIC_QUEUE_1,true);
    }

    @Bean
    public Queue goodsTopicQueue2() {
        return new Queue(GOODS_TOPIC_QUEUE_2,true);
    }

    @Bean
    public Queue goodsTopicQueue3() {
        return new Queue(GOODS_TOPIC_QUEUE_3,true);
    }

    @Bean
    public FanoutExchange goodsFanoutExchange() {
        return new FanoutExchange(GOODS_FUNOUT_EXCHANGE,true,false);
    }

    @Bean
    public Binding goodsTopicBinding1() {
        return BindingBuilder.bind(goodsTopicQueue1()).to(goodsTopicExchange()).with("*.info.*");
    }

    @Bean
    public Binding goodsTopicBinding2() {
        return BindingBuilder.bind(goodsTopicQueue2()).to(goodsTopicExchange()).with("a.b.*");
    }

    @Bean
    public Binding goodsTopicBinding3() {
        return BindingBuilder.bind(goodsTopicQueue3()).to(goodsTopicExchange()).with("#");
    }

    @Bean
    public Queue goodsQueue1() {
        return new Queue(GOODS_QUEUE_1,true);
    }

    @Bean
    public Queue goodsQueue2() {
        return new Queue(GOODS_QUEUE_2,true);
    }

    @Bean
    public Binding goodsBinding1() {
        return BindingBuilder.bind(goodsQueue1()).to(goodsFanoutExchange());
    }

    @Bean
    public Binding goodsBinding2() {
        return BindingBuilder.bind(goodsQueue2()).to(goodsFanoutExchange());
    }

    @Bean
    public DirectExchange mailExchange() {
        return new DirectExchange(MAILEXCHANGE,true,false);
    }

    @Bean
    public Queue mailQueue() {
        return new Queue(MAILQUEUE,true,false,false);
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MAILROUTEKEY);
    }

}
