package com.fh.shop.api.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.api.product.po.Product;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductMapper extends BaseMapper<Product> {

    int updateStock(Long goodsId, int num);

}
