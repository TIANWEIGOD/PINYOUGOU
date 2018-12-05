package com.pyg.itemPage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.itemPage.service.ItemPageService;
import com.pyg.pojo.TbItem;
import com.pyg.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/itempage")
public class ItemPageController {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference
    private ItemPageService itemPageService;

    @RequestMapping("/gen_item")
    public String generatorItem(Long goodsId) throws Exception {
        // spu -- goodsId
        Goods goods = itemPageService.findOne(goodsId);
        // 第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是 freemarker的版本号。
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        // 第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate("item.ftl");
        // 第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
        List<TbItem> itemList = goods.getItemList();

        for (TbItem item : itemList) {
            Map modeMap = new HashMap();
            modeMap.put("goods",goods);
            modeMap.put("tbItem",item);
            // 第六步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
            Writer writer = new FileWriter("D:\\class69\\html\\"+item.getId()+".html");
            // 第七步：调用模板对象的 process 方法输出文件。
            template.process(modeMap,writer);
            // 第八步：关闭流
            writer.close();
        }


        return "success";
    }


    @RequestMapping("/gen_itemAll")
    public String generatorItemAll() throws Exception {
        // spu -- goodsId

        List<Goods> goodsList = itemPageService.findAll();

        for (Goods goods : goodsList) {
            // 第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是 freemarker的版本号。
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            // 第四步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate("item.ftl");
            // 第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。

            List<TbItem> itemList = goods.getItemList();

            for (TbItem item : itemList) {
                Map modeMap = new HashMap();
                modeMap.put("goods",goods);
                modeMap.put("tbItem",item);
                // 第六步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
                Writer writer = new FileWriter("D:\\class69\\html\\"+item.getId()+".html");
                // 第七步：调用模板对象的 process 方法输出文件。
                template.process(modeMap,writer);
                // 第八步：关闭流
                writer.close();
            }
        }

        return "success";
    }
}
