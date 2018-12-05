package com.pyg.itemPage.service.mq;

import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

@Component
public class ItemPageDeleteConsumer implements MessageListener {

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
                boolean delete = new File("D:\\class69\\html\\" + item.getId() + ".html").delete();
                System.out.println(delete);
            }
            System.out.println("itempage delete is ok");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
