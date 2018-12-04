package com.itheima;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testAddOrUpdate() {
        TbItem item = new TbItem();
        item.setId(11L);
        item.setTitle("测试title哈哈");
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }


    @Test
    public void testQuery() {

        Query query = new SimpleQuery("item_title:测试");
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = tbItems.getContent();
        for (TbItem item : content) {
            System.out.println(item.getId());
        }
    }

    @Test
    public void testDelete() {
        SolrDataQuery solrDataQuery = new SimpleQuery("*:*");
        solrTemplate.delete(solrDataQuery);
        solrTemplate.commit();
    }

    @Test
    public void testHighLight(){
        HighlightQuery query = new SimpleHighlightQuery(new Criteria("item_keywords").is("小米"));
        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        options.setSimplePrefix("<span style=\"color:red\">");
        options.setSimplePostfix("</span>");
        query.setHighlightOptions(options);

        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        // System.out.println(JSON.toJSONString(highlightPage,true));
        List<TbItem> content = highlightPage.getContent();
        for (TbItem item : content) {
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(item);

            if (highlights != null && highlights.size()>0){
                List<String> snipplets = highlights.get(0).getSnipplets();

                if (snipplets!= null && snipplets.size()>0){
                    item.setTitle(snipplets.get(0));
                }
            }
        }

        for (TbItem item : content) {
            System.out.println(item.getTitle());
        }

    }

    @Test
    public void testGroup(){
        Query groupQuery = new SimpleQuery(new Criteria("item_keywords").is("三星"));

        GroupOptions options = new GroupOptions();
        options.addGroupByField("item_category");
        groupQuery.setGroupOptions(options);

        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);

        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        System.out.println(JSON.toJSONString(groupResult,true));
        List<GroupEntry<TbItem>> content = groupResult.getGroupEntries().getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            String groupValue = tbItemGroupEntry.getGroupValue();
            System.out.println(groupValue);
        }
    }
}
