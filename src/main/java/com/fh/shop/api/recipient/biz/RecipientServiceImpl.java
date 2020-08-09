package com.fh.shop.api.recipient.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.recipient.mapper.RecipientMapper;
import com.fh.shop.api.recipient.po.Recipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipientServiceImpl implements RecipientService {

    @Autowired
    private RecipientMapper recipientMapper;

    @Override
    public List<Recipient> findList(Long memberId) {
        QueryWrapper<Recipient> recipientQueryWrapper = new QueryWrapper<>();
        recipientQueryWrapper.eq("memberId",memberId);
        List<Recipient> recipientList = recipientMapper.selectList(recipientQueryWrapper);
        return recipientList;
    }
}
