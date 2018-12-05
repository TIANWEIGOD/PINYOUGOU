package com.pyg.data.init;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.mapper.TbTypeTemplateMapper;
import com.pyg.pojo.TbItemCat;
import com.pyg.pojo.TbSpecificationOption;
import com.pyg.pojo.TbSpecificationOptionExample;
import com.pyg.pojo.TbTypeTemplate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

public class RedisManager {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbTypeTemplateMapper templateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Test
    public void deleteRedis(){
        redisTemplate.delete("cat_brand");
        redisTemplate.delete("cat_spec");
    }

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
