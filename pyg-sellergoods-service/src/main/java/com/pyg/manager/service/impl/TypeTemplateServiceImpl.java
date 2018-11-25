package com.pyg.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.manager.service.TypeTemplateService;
import com.pyg.mapper.TbTypeTemplateMapper;
import com.pyg.pojo.TbTypeTemplate;
import com.pyg.pojo.TbTypeTemplateExample;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper templateMapper;

    @Override
    public PageResult search(int page, int rows, TbTypeTemplate typeTemplate) {
        Page<TbTypeTemplate> pages = null;
        PageHelper.startPage(page, rows);

        if (typeTemplate.getName() != null && typeTemplate.getName().trim() != "") {
            TbTypeTemplateExample example = new TbTypeTemplateExample();
            example.createCriteria().andNameLike("%" + typeTemplate.getName() + "%");
            pages = (Page<TbTypeTemplate>) templateMapper.selectByExample(example);
        } else {
            pages = (Page<TbTypeTemplate>) templateMapper.selectByExample(null);
        }

        return new PageResult(pages.getTotal(), pages.getResult());
    }

    @Override
    public PygResult add(TbTypeTemplate typeTemplate) {
        try {
            templateMapper.insert(typeTemplate);
            return new PygResult(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "添加失败");
        }
    }

    @Override
    public TbTypeTemplate findOne(long id) {
        return templateMapper.selectByPrimaryKey(id);
    }

    @Override
    public PygResult update(TbTypeTemplate typeTemplate) {

        try {
            templateMapper.updateByPrimaryKey(typeTemplate);
            return new PygResult(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "添加失败");
        }
    }

    @Override
    public PygResult delete(Long[] ids) {
        try {
            for (Long id : ids) {
                templateMapper.deleteByPrimaryKey(id);
            }
            return new PygResult(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "添加失败");
        }
    }

    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int page, int rows) {
        return null;
    }

    @Override
    public List<TbTypeTemplate> findAll() {
        return null;
    }

    @Override
    public PageResult findPage(int page, int rows) {
        return null;
    }
}
