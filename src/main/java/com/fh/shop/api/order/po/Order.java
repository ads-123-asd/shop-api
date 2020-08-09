package com.fh.shop.api.order.po;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order implements Serializable {

    private String id;

    private Long userId;

    private Date createTime;

    private Date payTime;

    private int status;

    private BigDecimal totalPrice;

    private int totalNum;

    private int payType;

    private Long recipientId;

    private String recipientor;

    private String address;

    private String phone;

}
