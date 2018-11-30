package com.pyg.manager.service;

import com.pyg.pojo.TbTypeTemplate;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    PageResult search(int page, int rows, TbTypeTemplate typeTemplate);

    PygResult add(TbTypeTemplate typeTemplate);

    TbTypeTemplate findOne(long id);

    PygResult update(TbTypeTemplate typeTemplate);

    PygResult delete(Long[] ids);

    PageResult findPage(TbTypeTemplate typeTemplate, int page, int rows);

    List<TbTypeTemplate> findAll();

    PageResult findPage(int page, int rows);

    List<Map> findSpecList(Long id);
}
