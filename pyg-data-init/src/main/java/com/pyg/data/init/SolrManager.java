package com.pyg.data.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.mapper.TbTypeTemplateMapper;
import com.pyg.pojo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
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


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbItemCatMapper itemCatMapper;
    
    @Autowired
    private TbTypeTemplateMapper templateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    
    @Test
    public void initRedis(){

        List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(null);

        for (TbItemCat tbItemCat : tbItemCats) {
            Long typeId = tbItemCat.getTypeId();
            TbTypeTemplate tbTypeTemplate = templateMapper.selectByPrimaryKey(typeId);
            List<Map> brandMaps = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("cat_brand").put(tbItemCat.getName(),brandMaps);

            String specIds = tbTypeTemplate.getSpecIds();
            List<Map> specMaps = JSON.parseArray(specIds, Map.class);
            for (Map specMap : specMaps) {
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                example.createCriteria().andSpecIdEqualTo(Long.parseLong(specMap.get("id")+""));
                List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
                specMap.put("option",options);
            }

            redisTemplate.boundHashOps("cat_spec").put(tbItemCat.getName(),specMaps);
        }




    }


}
