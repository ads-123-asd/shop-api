package com.fh.shop.api.member.biz;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.member.po.Member;

public interface MemberService {
    ServerResponse addMember(Member member) throws Exception;

    ServerResponse validaterMemName(String member);
    ServerResponse validaterEmail(String mail);
    ServerResponse validaterPhone(String phone);

    ServerResponse login(String userName, String password);
}
