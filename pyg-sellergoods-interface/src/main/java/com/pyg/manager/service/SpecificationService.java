package com.pyg.manager.service;

import com.pyg.pojo.TbSpecification;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import com.pyg.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    PageResult search(int pageNum, int pageSize, TbSpecification specification);

    PygResult add(Specification specification);

    Specification findOne(long id);

    PygResult delete(Long[] ids);

    PygResult update(Specification specification);

    List<Map> selectOptionList();
}
