package com.fh.shop.api.common;

public class KeyUtil {

    public static final int MEMBER_KEY_EXPIRE = 5 * 60 ;

    public static String buildMemberKey(String uuid, Long memberId){
        return "member:" + uuid + ":" + memberId;
    }

    public static String buildCartKey(Long memberId) {
        return "cart:" + memberId;
    }

    public static String buildStockLessKey(Long memberId) { return "order:stock:less:" + memberId; }

    public static String buildOrderKey(Long memberId) { return "order:" + memberId; }

    public static String buildPayLogKey(Long memberId) { return "paylog" + memberId; }

}
