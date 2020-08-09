package com.fh.shop.api.order.controller;

import com.fh.shop.api.annotation.Check;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.common.SystemConstant;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.api.order.biz.OrderService;
import com.fh.shop.api.order.vo.OrderParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/order")
@Api(tags = "订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("generateOrderConfirm")
    @Check
    @ApiOperation("生产确认订单页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",required = true,paramType = "header",type = "string")
})
    public ServerResponse generateOrderConfirm(HttpServletRequest request){
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        return orderService.generateOrderConfirm(memberId);
    }

    @GetMapping("generateOrder")
    @Check
    @ApiOperation("生产订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",required = true,paramType = "header",type = "string"),
            @ApiImplicitParam(name = "recipientId",value = "收件人id",required = true,paramType = "query",type = "long"),
            @ApiImplicitParam(name = "payType",value = "支付类型",required = true,paramType = "query",type = "int")
    })
    public ServerResponse generateOrder(HttpServletRequest request, OrderParam orderParam) {
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        orderParam.setMemberId(memberId);
        return orderService.generateOrder(orderParam);
    }

    @GetMapping("getResult")
    @Check
    @ApiOperation("获取订单结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name="x-auth",value = "头部信息" ,type = "string" ,required = true, paramType = "header"),
            @ApiImplicitParam(name="payType",value = "1微信2支付宝" ,type = "int" ,required = true, paramType = "query"),
            @ApiImplicitParam(name="recipientId",value = "收件人id‘" ,type = "long" ,required = true, paramType = "query")
    })
    public ServerResponse getResult(HttpServletRequest request) {

        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        return orderService.getResult(memberId);
    }

}
