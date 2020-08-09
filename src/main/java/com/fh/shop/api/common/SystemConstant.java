package com.fh.shop.api.common;

public class SystemConstant {

    public static final String CURR_MEMBER = "user";

    public static final int PRODUCT_IS_DOWN = 0;

    public interface OrderStatus {
       int WAIT_PA = 10;
       int PA_SUCCESS = 20;
       int SEND_SUCCESS = 30;
    }

    public interface PayStatus {
        int WAIT_PA = 10;
        int PA_SUCCESS = 20;
    }
}
