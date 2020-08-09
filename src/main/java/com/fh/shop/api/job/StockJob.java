package com.fh.shop.api.job;

import com.fh.shop.api.common.MailUtil;
import com.fh.shop.api.product.biz.ProductService;
import com.fh.shop.api.product.po.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockJob {

    @Autowired
    private ProductService productService;
    @Autowired
    private MailUtil mailUtil;

    @Scheduled(cron = "* 0/50 4-6 * * ?")
    public void checkStock(){
        //获取商品不足的商品
        List<Product> productList = productService.queryStockLessProductList();
        //生成表格
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("<table bgcolor=\"lime\" cellpadding=\"0\" cellspacing=\"0\" border=\"1px\" width=\"500px\">\n" +
                "    <thead>\n" +
                "        <tr>\n" +
                "            <td>商品名称</td>\n" +
                "            <td>商品价格</td>\n" +
                "            <td>商品库存</td>\n" +
                "        </tr>\n" +
                "    </thead>\n" +
                "    <tbody>");
        for (Product product:productList){
            stringBuffer.append(" <tr>\n" +
                    "            <td>"+product.getProductName()+"</td>\n" +
                    "            <td>"+product.getPrice().toString()+"</td>\n" +
                    "            <td>"+product.getStock()+"</td>\n" +
                    "        </tr>");

        }
        stringBuffer.append(" </tbody>\n" +
                "</table>");
        String tableHtml = stringBuffer.toString();
        mailUtil.DaoMail("2642476369@qq.com","商品库存足",tableHtml);
    }

}
