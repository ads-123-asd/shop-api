package com.fh.shop.api.cart.biz;

import com.fh.shop.api.common.ServerResponse;

public interface ICartService {

    ServerResponse addItem(Long memberId, Long goodsId, int num);

    ServerResponse findItemList(Long memberId);

    ServerResponse findItemCount(Long memberId);

    ServerResponse deleteCartItem(Long memberId, Long goodsId);

    ServerResponse deleteBatchItems(Long memberId, String ids);
}
