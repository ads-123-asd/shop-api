package com.fh.shop.api.cart.controller;

import com.fh.shop.api.annotation.Check;
import com.fh.shop.api.cart.biz.ICartService;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.common.SystemConstant;
import com.fh.shop.api.member.vo.MemberVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cart")
@Api(tags = "购物车接口")
public class CartController {

    @Autowired
    private ICartService cartService;

    @PostMapping("/addItem")
    @Check
    @ApiOperation("添加商品到购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",type = "string",required = true,paramType = "header"),
            @ApiImplicitParam(name = "goodsId",value = "商品id",type = "long",required = true,paramType = "query"),
            @ApiImplicitParam(name = "num",value = "商品数量",type = "int",required = true,paramType = "query")
    })
    public ServerResponse addItem(HttpServletRequest request,Long goodsId,int num){
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        return cartService.addItem(memberId,goodsId,num);
    }

    @GetMapping("/findItemList")
    @Check
    @ApiOperation("获取指定会员对应的购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",type = "string",required = true,paramType = "header")
    })
    public ServerResponse findItemList(HttpServletRequest request) {
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        return cartService.findItemList(memberId);
    }

    @GetMapping("/findItemCount")
    @Check
    @ApiOperation("获取指定会员对应的购物车数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",type = "string",required = true,paramType = "header")
    })
    public ServerResponse findItemCount(HttpServletRequest request){
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        return cartService.findItemCount(memberId);
    }

    @DeleteMapping("deleteCartItem/{id}")
    @Check
    @ApiOperation("删除购物车指定商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",type = "string",required = true,paramType = "header"),
            @ApiImplicitParam(name = "id",value = "商品id",type = "long",required = true,paramType = "path")
    })
    public ServerResponse deleteCartItem(HttpServletRequest request, @PathVariable("id") Long goodsId){
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        return cartService.deleteCartItem(memberId,goodsId);
    }

    @DeleteMapping("deleteBatchItems")
    @Check
    @ApiOperation("批量删除购物车指定商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",type = "string",required = true,paramType = "header"),
            @ApiImplicitParam(name = "ids",value = "要删除的id集合",type = "string",required = true,paramType = "query")
    })
    public ServerResponse deleteBatchItems(HttpServletRequest request,String ids) {
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        return cartService.deleteBatchItems(memberId,ids);
    }

}
