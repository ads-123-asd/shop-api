package com.fh.shop.api.cart.po;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fh.shop.api.common.BigDecimalJackson;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartItem implements Serializable {

    private Long goodsId;

    private String goodsName;

    @JsonSerialize(using = BigDecimalJackson.class)
    private BigDecimal price;

    private int num;

    @JsonSerialize(using = BigDecimalJackson.class)
    private BigDecimal subPrice;

    private String imgUrl;

}
