package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import utils.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    // @Value("${appid}")
    private String appid = "wx8397f8696b538317";

    // @Value("${partner}")
    private String partner = "1473426802";

    // @Value("${partnerkey}")
    private String partnerkey = "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";

    // @Value("${notifyurl}")
    private String notifyurl = "http://a31ef7db.ngrok.io/WeChatPay/WeChatPayNotify";

    public Map createNative(String out_trade_no, String total_fee) {
        Map paramMap = new HashMap();
        paramMap.put("appid", appid);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", "品优购");
        paramMap.put("out_trade_no", out_trade_no);
        paramMap.put("total_fee", total_fee);
        paramMap.put("spbill_create_ip", "127.0.0.1"); // 终端IP
        paramMap.put("notify_url", notifyurl); // 通知会掉地址可以随便写
        paramMap.put("trade_type", "NATIVE"); // 交易类型

        // https://api.mch.weixin.qq.com/pay/unifiedorder

        // 微信转换工具WXPayUtil
        try {
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey); //<sign>FC1E7F0123B8B71470579ADB2A01A4FC</sign>通过算法自动生成的

            System.out.println(xmlParam);

            httpClient.setXmlParam(xmlParam);

            httpClient.post();

            String content = httpClient.getContent();

            System.out.println("结果" + content);

            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            Map map = new HashMap(); // 创建一个新的环境

            map.put("code_url", resultMap.get("code_url"));

            map.put("out_trade_no", out_trade_no);

            map.put("total_fee", total_fee);

            return map;
        } catch (Exception e) {
            e.printStackTrace();

            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map paramMap = new HashMap();
        // 签名是通过算法自动生成的
        paramMap.put("appid", appid);
        paramMap.put("mch_id", partner);
        paramMap.put("out_trade_no", out_trade_no);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");

            httpClient.setXmlParam(xmlParam);

            httpClient.post();

            String content = httpClient.getContent();

            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            System.out.println(resultMap);

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
