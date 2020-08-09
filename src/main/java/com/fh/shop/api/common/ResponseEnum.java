package com.fh.shop.api.common;

public enum ResponseEnum {

    PAY_IS_FAIL(5000,"支付失败"),

    ORDER_STOCK_LESS(4000,"订单库存不足"),
    ORDER_SUCCESS_PD(4001,"订单正在排队"),
    ORDER_IS_ERROR(4002,"下单失败"),

    CART_PRODUCT_IS_NULL(3000,"添加的商品不存在"),
    CART_PRODUCT_IS_DOWN(3001,"商品下架了"),
    CART_NUM_IS_ERROR(3002,"商品数量不合法"),
    CART_DELETE_BATCH_IDS_IS_NULL(3003,"批量删除时ids必须传递"),

    LOGIN_INFO_IS_NULL(2000,"用户名或者密码为空"),
    LOGIN_MEMBER_NAME_IS_NOT_EXIST(2001,"用户不存在"),
    LOGIN_PASSWORD_IS_ERROR(2002,"密码错误"),
    LOGIN_HEADER_IS_MISS(2003,"头信息丢失"),
    LOGIN_HEADER_CONTENT_IS_MISS(2004,"头信息不完整"),
    LOGIN_MEMBER_IS_CHANGE(2005,"会员信息已被篡改"),
    LOGIN_TIME_OUT(2006,"登录超时"),

    REG_Member_Is_NULL(1004,"信息为空"),
    GET_PHONE_IS_ESIST(1003,"手机号已存在"),
    GET_MAIL_IS_ESIST(1002,"邮箱已存在"),
    GET_MEMBERNAME_IS_ESIST(1001,"会员名已存在"),
   GET_MEMBER_IS_NULL(1000,"注册会员信息为空");

    private int code;
    private String msg;

    private ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
