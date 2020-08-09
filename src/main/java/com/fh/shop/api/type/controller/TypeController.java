package com.fh.shop.api.type.controller;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.type.biz.TypeService;
import com.fh.shop.api.type.po.Type;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/types")
@Api(tags = "商品类型接口")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("queryTypeList")
    public ServerResponse queryTypeList(){
        List<Type> typeList = typeService.queryTypeList();
        return ServerResponse.success(typeList);
    }

}
