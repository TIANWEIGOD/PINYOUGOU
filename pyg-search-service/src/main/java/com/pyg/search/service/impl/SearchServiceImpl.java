package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map searchByParam(Map paramMap) {
        Map map = new HashMap();
        Object keyword = paramMap.get("keyword");

        // 分组显示开始
        List<String> categoryList = new ArrayList<>();
        Query groupQuery = new SimpleQuery(new Criteria("item_keywords").is(keyword));
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        groupQuery.setGroupOptions(groupOptions);
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            categoryList.add(groupEntry.getGroupValue());
        }

        map.put("categoryList", categoryList);
        // 分组显示结束

        // 根据第一个分类显示品牌跟规格开始
        String categoryName = categoryList.get(0);
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("cat_brand").get(categoryName);
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("cat_spec").get(categoryName);
        map.put("brandList", brandList);
        map.put("specList", specList);
        // 根据第一个分类显示品牌跟规格结束


        // 高亮显示
        HighlightQuery highlightQuery = new SimpleHighlightQuery(new Criteria("item_keywords").is(keyword));

        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        options.setSimplePrefix("<span style=\"color:red\">");
        options.setSimplePostfix("</span>");
        highlightQuery.setHighlightOptions(options);


        //过滤条件
        // 分类
        if (!paramMap.get("category").equals("")) {
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_category").is(paramMap.get("category"))));
        }
        // 品牌

        if (!paramMap.get("brand").equals("")) {
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_brand").is(paramMap.get("brand"))));
        }

        // 价格
        if (!paramMap.get("price").equals("")) {
            String[] prices = paramMap.get("price").toString().split("-");
            if (prices[1].equals("*")) {
                highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").greaterThanEqual(prices[0])));
            } else {
                highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").between(prices[0], prices[1], true, true)));
            }
        }
        // 规格"spec":{"水杯容量":"800ml","水杯材质":"玻璃"}
        Map<String, String> specMap = (Map) paramMap.get("spec");
        for (String key : specMap.keySet()) {
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_spec_" + key).is(specMap.get(key))));
        }

        //过滤结束

        // 价格排序开始
        if (paramMap.get("order").equals("asc")) {
            highlightQuery.addSort(new Sort(Sort.Direction.ASC, "item_price"));
        } else {
            highlightQuery.addSort(new Sort(Sort.Direction.DESC, "item_price"));
        }
        // 价格排序结束

        // 分页开始
        Integer pageNo = (Integer) paramMap.get("pageNo");
        highlightQuery.setOffset((pageNo-1)*60);
        highlightQuery.setRows(60);
        // 分页结束
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        List<TbItem> itemList = highlightPage.getContent();
        for (TbItem item : itemList) {
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(item);
            if (highlights != null && highlights.size() > 0) {
                List<String> snipplets = highlights.get(0).getSnipplets();
                if (snipplets != null && snipplets.size() > 0) {
                    item.setTitle(snipplets.get(0));
                }
            }
        }




        long total = highlightPage.getTotalElements();
        int totalPages = highlightPage.getTotalPages();
        map.put("total", total);
        map.put("itemList", itemList);
        map.put("totalPages",totalPages);
        return map;
    }


}
