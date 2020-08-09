package com.fh.shop.api.cart.biz;

import com.alibaba.fastjson.JSONObject;
import com.fh.shop.api.cart.po.Cart;
import com.fh.shop.api.cart.po.CartItem;
import com.fh.shop.api.common.*;
import com.fh.shop.api.product.mapper.ProductMapper;
import com.fh.shop.api.product.po.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class ICartServiceImpl implements ICartService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse addItem(Long memberId, Long goodsId, int num) {
        //判断商品是否存在
        Product product = productMapper.selectById(goodsId);
        if (null == product) {
            return ServerResponse.error(ResponseEnum.CART_PRODUCT_IS_NULL);
        }
        //商品的状态是否正常
        if (product.getStatus() == SystemConstant.PRODUCT_IS_DOWN) {
            return ServerResponse.error(ResponseEnum.CART_PRODUCT_IS_DOWN);
        }
        //如果会员已有了对应的购物车
        String cartKey = KeyUtil.buildCartKey(memberId);
        String cartJson = RedisUtil.get(cartKey);
        if (StringUtils.isNotEmpty(cartJson)) {
            //如果直接向购物车放入商品
            Cart cart = JSONObject.parseObject(cartJson,Cart.class);
            List<CartItem> cartItemList = cart.getCartItemList();
            boolean exist = false;
            CartItem cartItem = null;
            for (CartItem item : cartItemList) {
                if (item.getGoodsId().longValue() == goodsId.longValue()) {
                    exist = true;
                    cartItem = item;
                    break;
                }
            }
            if (cartItem != null) {
                //如果商品存在更新商品的数量和小计，更新购物车[总计，总个数]
                cartItem.setNum(cartItem.getNum()+num);
                int num1 = cartItem.getNum();
                if (num1 <= 0) {
                    //删除整个商品,从cartItemList移除商品
                    //【特别注意:倒着删除】在循环遍历list时，删除集合中的信息
                    cartItemList.remove(cartItem);
                }else {
                    BigDecimal subPrice = BigDecimalUtil.mul(num1+"",cartItem.getPrice().toString());
                    //更新当前商品小计
                    cartItem.setSubPrice(subPrice);
                }
                updateCart(memberId, cart);
            } else {
                //如果商品不存在添加商品，更新购物车[总计，总个数]
                if (num <= 0 ) {
                    return ServerResponse.error(ResponseEnum.CART_NUM_IS_ERROR);
                }
                //构建商品
                CartItem cartItemInfo = buildCartItem(num, product);
                //加入购物车
                cart.getCartItemList().add(cartItemInfo);
                //更新购物车
                updateCart(memberId, cart);
            }
        } else {
            //如果会员没有对应的购物车;
            //创建购物车
            Cart cart = new Cart();
            List<CartItem> cartItemList = cart.getCartItemList();
            //再添加商品
            //构建商品
            CartItem cartItemInfo = buildCartItem(num, product);
            //加入购物车
            cart.getCartItemList().add(cartItemInfo);
            updateCart(memberId, cart);
        }
        return ServerResponse.success();
    }

    private CartItem buildCartItem(int num, Product product) {
        CartItem cartItemInfo = new CartItem();
        cartItemInfo.setGoodsId(product.getId());
        cartItemInfo.setPrice(product.getPrice());
        cartItemInfo.setImgUrl(product.getImage());
        cartItemInfo.setGoodsName(product.getProductName());
        cartItemInfo.setNum(num);
        BigDecimal subPrice = BigDecimalUtil.mul(num + "", product.getPrice().toString());
        cartItemInfo.setSubPrice(subPrice);
        return cartItemInfo;
    }

    private void updateCart(Long memberId, Cart cart) {
        List<CartItem> cartItemList = cart.getCartItemList();
        if (cartItemList.size()==0){
            RedisUtil.delete(KeyUtil.buildCartKey(memberId));
            return;
        }
        int totalCount = 0;
        BigDecimal totalPrice = new BigDecimal(0);
        //更新购物车
        for (CartItem item : cartItemList) {
            totalCount += item.getNum();
            totalPrice = BigDecimalUtil.add(totalPrice.toString(), item.getSubPrice().toString());
        }
        cart.setTotalNum(totalCount);
        cart.setTotalPrice(totalPrice);
        //最终往redis里更新
        String cartNewJson = JSONObject.toJSONString(cart);
        String cartKey = KeyUtil.buildCartKey(memberId);
        RedisUtil.set(cartKey, cartNewJson);
    }

    @Override
    public ServerResponse findItemList(Long memberId) {
        String cartKey = KeyUtil.buildCartKey(memberId);
        String cartJson = RedisUtil.get(cartKey);
        Cart cart = JSONObject.parseObject(cartJson,Cart.class);
        return ServerResponse.success(cart);
    }

    @Override
    public ServerResponse findItemCount(Long memberId) {
        String cartKey = KeyUtil.buildCartKey(memberId);
        String cartJson = RedisUtil.get(cartKey);
        Cart cart = JSONObject.parseObject(cartJson,Cart.class);
        if (cart == null) {
            return ServerResponse.success(0);
        }
        int totalNum = cart.getTotalNum();
        return ServerResponse.success(totalNum);
    }

    @Override
    public ServerResponse deleteCartItem(Long memberId, Long goodsId) {
        //获取会员对应的购物
        String cartJson = RedisUtil.get(KeyUtil.buildCartKey(memberId));
        Cart cart = JSONObject.parseObject(cartJson, Cart.class);
        //删除购物中对应的商品
        List<CartItem> cartItemList = cart.getCartItemList();
        //边遍历，边删除
        Iterator<CartItem> iterator = cartItemList.iterator();
        while (iterator.hasNext()) {
            CartItem next = iterator.next();
            if (next.getGoodsId().longValue() == goodsId.longValue()) {
                iterator.remove();
            }
        }
        //更新购物车
        updateCart(memberId,cart);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteBatchItems(Long memberId, String ids) {
        if (StringUtils.isEmpty(ids)) {
            return ServerResponse.error(ResponseEnum.CART_DELETE_BATCH_IDS_IS_NULL);
        }
        String[] idArr = ids.split(",");

        List<Long> idsList = Arrays.stream(idArr).map(x -> Long.parseLong(x)).collect(Collectors.toList());
        String cartJson = RedisUtil.get(KeyUtil.buildCartKey(memberId));
        Cart cart = JSONObject.parseObject(cartJson, Cart.class);
        //删除购物中对应的商品
        List<CartItem> cartItemList = cart.getCartItemList();
        //边遍历，边删除
        Iterator<CartItem> iterator = cartItemList.iterator();
        for (Long id : idsList) {
            while (iterator.hasNext()) {
                CartItem next = iterator.next();
                if (next.getGoodsId().longValue() == id.longValue()) {
                    iterator.remove();
                    break;
                }
            }
        }
        //更新购物车
        updateCart(memberId,cart);
        return ServerResponse.success();
    }
}
