package com.fh.shop.api.brand.po;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class Brand implements Serializable {
    private Long id;
    private String brandName;
}
