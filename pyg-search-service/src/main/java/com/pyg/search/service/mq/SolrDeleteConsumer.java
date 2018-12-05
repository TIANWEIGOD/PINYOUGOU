package com.pyg.search.service.mq;

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

@Component
public class SolrDeleteConsumer implements MessageListener {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String tbGoodsId = textMessage.getText();
            SolrDataQuery solrDataQuery = new SimpleQuery(new Criteria("item_goodsid").is(tbGoodsId));
            solrTemplate.delete(solrDataQuery);
            solrTemplate.commit();
            System.out.println("solr delete is ok!!");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
