package com.pyg.data.init;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class SolrManager {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    @Test
    public void initSolr() {
        List<TbItem> itemList = itemMapper.grounding();
        for (TbItem item : itemList) {
            String spec = item.getSpec();
            Map specMap = JSON.parseObject(spec, Map.class);
            item.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }







}
