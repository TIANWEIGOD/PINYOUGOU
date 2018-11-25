package com.pyg.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.manager.service.SpecificationService;
import com.pyg.mapper.TbSpecificationMapper;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.pojo.TbSpecification;
import com.pyg.pojo.TbSpecificationExample;
import com.pyg.pojo.TbSpecificationOption;
import com.pyg.pojo.TbSpecificationOptionExample;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import com.pyg.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(int pageNum, int pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = null;
        if (specification.getSpecName() != null && specification.getSpecName().trim() != "") {
                specification.setSpecName(specification.getSpecName());
                TbSpecificationExample example = new TbSpecificationExample();
                example.createCriteria().andSpecNameLike("%"+specification.getSpecName()+"%");
                page = (Page<TbSpecification>) specificationMapper.selectByExample(example);

        } else {
            page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        }

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public PygResult add(Specification specification) {
        try {
            TbSpecification tbspecification = specification.getSpecification();
            specificationMapper.insert(tbspecification);

            List<TbSpecificationOption> list = specification.getSpecificationOptionList();

            for (TbSpecificationOption option : list) {
                option.setSpecId(tbspecification.getId());
                specificationOptionMapper.insert(option);
            }

            return new PygResult(true, "注册成功");
        } catch (Exception e) {
            return new PygResult(false, "注册失败");
        }
    }

    @Override
    public Specification findOne(long id) {
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);

        Specification specification = new Specification();
        specification.setSpecification(tbSpecification);
        specification.setSpecificationOptionList(options);
        return specification;
    }

    @Override
    public PygResult delete(Long[] ids) {
        try {
            for (Long id : ids) {
                specificationMapper.deleteByPrimaryKey(id);

                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                example.createCriteria().andSpecIdEqualTo(id);

                specificationOptionMapper.deleteByExample(example);
            }
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }

    @Override
    public PygResult update(Specification specification) {

        try {
            TbSpecification spft = specification.getSpecification();

            specificationMapper.updateByPrimaryKey(spft);

            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(spft.getId());

            specificationOptionMapper.deleteByExample(example);

            // 先奸后杀
            List<TbSpecificationOption> options = specification.getSpecificationOptionList();
            for (TbSpecificationOption option : options) {
                option.setSpecId(spft.getId());
                specificationOptionMapper.insert(option);
            }

            return new PygResult(true, "更改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "更改失败");
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}
