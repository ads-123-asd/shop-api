package com.fh.shop.api.order.vo;

import com.fh.shop.api.cart.po.Cart;
import com.fh.shop.api.recipient.po.Recipient;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderConfirmVo implements Serializable {

    private List<Recipient> recipientList = new ArrayList<>();

    private Cart cart;

}
