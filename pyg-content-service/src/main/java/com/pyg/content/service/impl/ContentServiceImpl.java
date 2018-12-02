package com.pyg.content.service.impl;

import java.util.List;

import com.pyg.content.service.ContentService;
import com.pyg.pojo.TbContentExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbContentMapper;
import com.pyg.pojo.TbContent;

import com.pyg.utils.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContent content) {
        contentMapper.insert(content);
        redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbContent content) {
        TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
        contentMapper.updateByPrimaryKey(content);

        redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());

        if (tbContent.getCategoryId().longValue() != content.getCategoryId().longValue()){
            redisTemplate.boundHashOps("contentList").delete(tbContent.getCategoryId());
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbContent tbContent = contentMapper.selectByPrimaryKey(id);
            contentMapper.deleteByPrimaryKey(id);
            redisTemplate.boundHashOps("contentList").delete(tbContent.getCategoryId());
        }
    }


    @Override
    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }

        }

        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<TbContent> findByCategotyId(Long cid) {
        List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(cid);
        if (contentList == null) {
            TbContentExample example = new TbContentExample();
            example.createCriteria().andCategoryIdEqualTo(cid);
            example.createCriteria().andStatusEqualTo("1");
            example.setOrderByClause("sort_order");
            redisTemplate.boundHashOps("contentList").put(cid,contentMapper.selectByExample(example));
            System.out.println("from MYSQL");
            return contentMapper.selectByExample(example);
        } else {
            System.out.println("from REDIS");
        }

        return contentList;
    }

}
