package com.pyg.search.service.mq;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;
@Component
public class SolrUpdateConsumer implements MessageListener {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String tbGoodsId = textMessage.getText();
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(Long.parseLong(tbGoodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            for (TbItem item : itemList) {
                String spec = item.getSpec();
                Map specMap = JSON.parseObject(spec, Map.class);
                item.setSpecMap(specMap);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
            System.out.println("solr update is ok!!");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
