package com.fh.shop.api.member.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.common.*;
import com.fh.shop.api.member.mapper.MemberMapper;
import com.fh.shop.api.member.po.Member;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.api.mq.MQSender;
import com.fh.shop.api.mq.MailMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService{

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private MQSender mqSender;

    @Override
    public ServerResponse addMember(Member member) throws Exception {
        String memberName= member.getMemberName();
        String password= member.getPassword();
        String mail= member.getMail();
        String phone= member.getPhone();
        if (StringUtils.isEmpty(memberName) ||
                StringUtils.isEmpty(password)||
                StringUtils.isEmpty(mail)||
                StringUtils.isEmpty(phone)){
            return ServerResponse.error(ResponseEnum.GET_MEMBER_IS_NULL);
        }
        QueryWrapper<Member> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("memberName",memberName);
        Member member1=memberMapper.selectOne(queryWrapper);
        if (member1 != null){
            return ServerResponse.error(ResponseEnum.GET_MEMBERNAME_IS_ESIST);
        }
        QueryWrapper<Member> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("mail",mail);
        Member member2=memberMapper.selectOne(queryWrapper1);
        if (member2 != null){
            return ServerResponse.error(ResponseEnum.GET_MEMBERNAME_IS_ESIST);
        }
        QueryWrapper<Member> queryWrapper2=new QueryWrapper<>();
        queryWrapper2.eq("phone",phone);
        Member member3=memberMapper.selectOne(queryWrapper2);
        if (member3 != null){
            return ServerResponse.error(ResponseEnum.GET_MEMBERNAME_IS_ESIST);
        }

        memberMapper.addMember(member);

        mailUtil.DaoMail(mail,"注册成功","恭喜"+member.getRealName()+"成为我们的会员");
        return ServerResponse.success();
    }

    @Override
    public ServerResponse validaterMemName(String memberName) {
        if(StringUtils.isEmpty(memberName)){
            return ServerResponse.error(ResponseEnum.REG_Member_Is_NULL);
        }
        QueryWrapper<Member> objectQueryWrapper = new QueryWrapper();
        objectQueryWrapper.eq("memberName",memberName);
        Member member1 = memberMapper.selectOne(objectQueryWrapper);
        if(member1!=null){
            return ServerResponse.error(ResponseEnum.GET_MEMBERNAME_IS_ESIST);
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse validaterEmail(String mail) {
        if(StringUtils.isEmpty(mail)){
            return ServerResponse.error(ResponseEnum.REG_Member_Is_NULL);
        }
        QueryWrapper<Member> objectQueryWrapper1 = new QueryWrapper();
        objectQueryWrapper1.eq("mail",mail);
        Member member2 = memberMapper.selectOne(objectQueryWrapper1);
        if(member2!=null){
            return ServerResponse.error(ResponseEnum.GET_MAIL_IS_ESIST);
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse validaterPhone(String phone) {
        if(StringUtils.isEmpty(phone)){
            return ServerResponse.error(ResponseEnum.REG_Member_Is_NULL);
        }
        QueryWrapper<Member> objectQueryWrapper2 = new QueryWrapper();
        objectQueryWrapper2.eq("phone",phone);
        Member member3 = memberMapper.selectOne(objectQueryWrapper2);
        if(member3!=null){
            return ServerResponse.error(ResponseEnum.GET_PHONE_IS_ESIST);
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse login(String userName, String password) {
        //非空判断
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            return ServerResponse.error(ResponseEnum.LOGIN_INFO_IS_NULL);
        }
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("memberName",userName);
        Member member =memberMapper.selectOne(queryWrapper);
        if (member == null) {
            return ServerResponse.error(ResponseEnum.LOGIN_MEMBER_NAME_IS_NOT_EXIST);
        }
        //判断密码是否正确
        if (!password.equals(member.getPassword())) {
            return  ServerResponse.error(ResponseEnum.LOGIN_PASSWORD_IS_ERROR);
        }
        //===============生产token===============
        //生产token的样子类似于xxx.yyy 用户信息.对用户信息的签名
        //签名的目的:保证用户信息不被篡改
        //怎么生产签名:md5(用户信息 结合 秘钥)
        //秘钥是在服务端保存的，黑客，攻击者他们获取不到
        //=======================================
        //生产用户信息对应的json
        MemberVo memberVo = new MemberVo();
        Long memberId = member.getId();
        memberVo.setId(memberId);
        memberVo.setMemberName(member.getMemberName());
        memberVo.setRealName(member.getRealName());
        String uuid = UUID.randomUUID().toString();
        memberVo.setUuid(uuid);
        //转换Java对象到json
        String memberJosn = JSONObject.toJSONString(memberVo);
        //对用户信息进行base64编码[起到一定的安全作用(对于初学者来说，唬他一下，但是对于专业人士来说，起不到任何作用，可以直接进行解码)]
        //jdk1.8内部直接提供base64的工具类，如果jdk低于1.8就需要第三方提供的来完成base64编码
        try {
            String memberJosnBase64 = Base64.getEncoder().encodeToString(memberJosn.getBytes("utf-8"));
            //生成用户信息所对应的签名
            String sign = MD5Util.sign(memberJosnBase64,MD5Util.SECRET);
            //对签名也进行base64编码
            String signBase64 = Base64.getEncoder().encodeToString(sign.getBytes("utf-8"));
            //处理超时
            RedisUtil.setEx(KeyUtil.buildMemberKey(uuid,memberId), "",KeyUtil.MEMBER_KEY_EXPIRE);
            //发送邮件
            String mail = member.getMail();
            //mailUtil.DaoMail(mail,member.getMemberName()+"在"+ DateUtil.se2te(new Date(),DateUtil.FULL_TIME)+"登录了","登录成功");
            MailMessage mailMessage = new MailMessage();
            mailMessage.setMail(mail);
            mailMessage.setTitle("登录成功");
            mailMessage.setRealName(member.getRealName());
            mailMessage.setContent(member.getRealName()+"在"+ DateUtil.se2te(new Date(),DateUtil.FULL_TIME)+"登录了");
            mqSender.sendMailMessage(mailMessage);
            //响应数据给客户端
            return ServerResponse.success(memberJosnBase64+"."+signBase64);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
