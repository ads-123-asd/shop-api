package com.fh.shop.api.product.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.common.RedisUtil;
import com.fh.shop.api.common.ServerResponse;

import com.fh.shop.api.product.mapper.ProductMapper;
import com.fh.shop.api.product.po.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse queryIsHot() {
        String hotProductList = RedisUtil.get("hotProductList");
        if (StringUtils.isNotEmpty(hotProductList)){
            List<Product> productList = JSONObject.parseArray(hotProductList, Product.class);
        return ServerResponse.success(productList);
        }
        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("isHot",1);
        queryWrapper.eq("status",1);
        List<Product> productList=productMapper.selectList(queryWrapper);
        String hotProductListJson = JSONObject.toJSONString(productList);
        RedisUtil.set("hotProductList",hotProductListJson);
        return ServerResponse.success(productList);
    }

    @Override
    public List<Product> queryStockLessProductList() {

        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        queryWrapper.lt("stock",10);
        List<Product> productList = productMapper.selectList(queryWrapper);

        return productList;
    }

}
