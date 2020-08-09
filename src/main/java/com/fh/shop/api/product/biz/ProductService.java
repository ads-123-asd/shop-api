package com.fh.shop.api.product.biz;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.product.po.Product;

import java.util.List;

public interface ProductService {
    ServerResponse queryIsHot();

    List<Product> queryStockLessProductList();
}
