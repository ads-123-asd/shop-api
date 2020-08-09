package com.fh.shop.api.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.fh.shop.api.annotation.Check;
import com.fh.shop.api.common.*;
import com.fh.shop.api.exception.GlobalException;
import com.fh.shop.api.member.vo.MemberVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Base64;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler)throws Exception {
        //处理跨域
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        //处理请求头
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"x-auth,content-type");
        //处理客户端发送的各种请求类型
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,"POST,DELETE,GET,POST");
        //处理options请求[并不是真正的要发送请求，里面没有携带任何数据，所以对options请求直接阻止后续访问]
        String requestMethod = request.getMethod();
        if (requestMethod.equalsIgnoreCase("options")){
            return false;
        }
        //通过自定义注解的方式来决定，那些个请求需要被拦截验证
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        Method method = handlerMethod.getMethod();
        if (!method.isAnnotationPresent(Check.class)){
            return true;
        }
        //==============拦截验证================
        //先判断是否存在头信息x-auth
        //判断头信息是否完整
        //判断用户信息是否被篡改[验签]
        //判断是否超时[redis]
        //续命[在还没有超时前，如果再次发送了请求，则重置过期时间]
        //将用户信息存入request中，方便以后使用
        //======================================
        //先判断是否存在头信息x-auth
        String header = request.getHeader("x-auth");
        if (StringUtils.isEmpty(header)) {
            throw new GlobalException(ResponseEnum.LOGIN_HEADER_IS_MISS);
        }
        //判断头信息是否完整
        String[] split = header.split("\\.");
        if (split.length != 2) {
            throw new GlobalException(ResponseEnum.LOGIN_HEADER_CONTENT_IS_MISS);
        }
        //判断用户信息是否被篡改[验签]
        String memberBase64Json = split[0];
        String signBase64 = split[1];
        //重新生成新的签名和客户端传递过来的签名进行对比
        String newSign =  MD5Util.sign(memberBase64Json, MD5Util.SECRET);
        String newSignBase64 = Base64.getEncoder().encodeToString(newSign.getBytes("utf-8"));
        if (!newSignBase64.equals(signBase64)) {
            throw new GlobalException(ResponseEnum.LOGIN_MEMBER_IS_CHANGE);
        }
        //判断是否超时[redis]
        String memberJson = new String(Base64.getDecoder().decode(memberBase64Json),"utf-8");
        MemberVo memberVo = JSONObject.parseObject(memberJson,MemberVo.class);
        Long id = memberVo.getId();
        String uuid = memberVo.getUuid();
        boolean exist = RedisUtil.exist(KeyUtil.buildMemberKey(uuid,id));
        if (!exist) {
            throw new GlobalException(ResponseEnum.LOGIN_TIME_OUT);
        }
        //续命
        RedisUtil.expire(KeyUtil.buildMemberKey(uuid,id),KeyUtil.MEMBER_KEY_EXPIRE);
        //存入request中，方便以后使用
        request.setAttribute(SystemConstant.CURR_MEMBER,memberVo);
        return true;
    }

}
