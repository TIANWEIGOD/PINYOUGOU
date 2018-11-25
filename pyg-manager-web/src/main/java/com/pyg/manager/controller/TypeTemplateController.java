package com.pyg.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.manager.service.TypeTemplateService;
import com.pyg.pojo.TbTypeTemplate;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService ts;

    @RequestMapping("search")
    public PageResult search(int page, int rows, @RequestBody TbTypeTemplate typeTemplate){
        PageResult pageResult = ts.search(page,rows,typeTemplate);
        return pageResult;
    }

    @RequestMapping("add")
    public PygResult add(@RequestBody TbTypeTemplate typeTemplate){
        return ts.add(typeTemplate);
    }

    @RequestMapping("findOne")
    public TbTypeTemplate findOne(long id){
        return ts.findOne(id);
    }

    @RequestMapping("update")
    public PygResult update(@RequestBody TbTypeTemplate typeTemplate){
        return ts.update(typeTemplate);
    }

    @RequestMapping("delete")
    public PygResult delete(Long[] ids){
        return ts.delete(ids);
    }
}
