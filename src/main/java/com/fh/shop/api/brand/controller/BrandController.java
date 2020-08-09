package com.fh.shop.api.brand.controller;

import com.fh.shop.api.brand.biz.BrandService;
import com.fh.shop.api.brand.po.Brand;
import com.fh.shop.api.common.ServerResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@Api(tags = "品牌接口")
public class BrandController {

    @Autowired
    private BrandService brandService;
    private List<Brand> brandList;

    @PostMapping
    @ApiOperation("添加品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandName",value = "品牌名",type = "string",required = true,paramType = "query")
    })
    public ServerResponse add(Brand brand){
        return brandService.addBrand(brand);
    }

    @GetMapping
    @ApiOperation("获取所有品牌列表")
    public ServerResponse query(){
        try {
            List<Brand> brandList= brandService.queryBrand();
          return   ServerResponse.success(brandList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }
    @DeleteMapping("/{brandId}")
    @ApiOperation("根据id删除指定的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "品牌id",required = true,type = "long",paramType = "path")
    })
    public ServerResponse delete(@PathVariable("brandId") Long id) {
        return brandService.delete(id);
    }

    @PutMapping
    @ApiOperation("更新品牌")
    public ServerResponse update(@ApiParam("品牌的json格式") @RequestBody Brand brand) {
        return brandService.update(brand);
    }

    @DeleteMapping
    @ApiOperation("批量删除品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "品牌id集合",type = "string",required = true,paramType = "query")
    })
    public ServerResponse deleteBatch(String ids) {
        return brandService.deleteBatch(ids);
    }
}
