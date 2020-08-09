package com.fh.shop.api.product.controller;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.product.biz.ProductService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@Api(tags = "商品接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("queryIsHot")
    public ServerResponse queryIsHot(){
        return productService.queryIsHot();
    }

}
