package com.pyg.itemPage.service.mq;

import com.pyg.itemPage.service.ItemPageService;
import com.pyg.pojo.TbItem;
import com.pyg.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemPageUpdateConsumer implements MessageListener {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String tbGoodsId = textMessage.getText();
            Goods goods = itemPageService.findOne(Long.parseLong(tbGoodsId));

            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            List<TbItem> itemList = goods.getItemList();
            for (TbItem item : itemList) {
                Map map = new HashMap();
                map.put("goods", goods);
                map.put("tbItem", item);

                Writer writer = new FileWriter("D:\\class69\\html\\" + item.getId() + ".html");
                template.process(map, writer);
                writer.close();
            }
            System.out.println("itempage update is ok");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
