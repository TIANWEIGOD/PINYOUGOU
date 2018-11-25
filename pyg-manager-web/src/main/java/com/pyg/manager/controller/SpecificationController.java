package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.manager.service.SpecificationService;
import com.pyg.pojo.TbSpecification;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import com.pyg.vo.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    @RequestMapping("search")
    public PageResult search(int pageNum, int pageSize, @RequestBody TbSpecification specification) {
        return specificationService.search(pageNum, pageSize, specification);
    }

    @RequestMapping("add")
    public PygResult add(@RequestBody Specification specification) {
        PygResult pygResult = specificationService.add(specification);
        return pygResult;
    }

    @RequestMapping("findOne")
    public Specification findOne(long id) {
        return specificationService.findOne(id);
    }

    @RequestMapping("update")
    public PygResult update(@RequestBody Specification specification){
        return specificationService.update(specification);
    }

    @RequestMapping("delete")
    public PygResult delete(Long[] ids){
        return specificationService.delete(ids);
    }

    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return specificationService.selectOptionList();
    }

}
