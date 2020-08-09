package com.fh.shop.api.recipient.biz;

import com.fh.shop.api.recipient.po.Recipient;

import java.util.List;

public interface RecipientService {

    List<Recipient> findList(Long memberId);
}