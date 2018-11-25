package com.pyg.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.manager.service.BrandService;
import com.pyg.mapper.TbBrandMapper;
import com.pyg.pojo.TbBrand;
import com.pyg.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Map;

@Service // 一定要用dubbo的service注解
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;


    @Override
    public List<TbBrand> findAllBrand() {
        List<TbBrand> list = brandMapper.findAllBrand();
        System.out.println(list);
        return list;
    }

    @Override
    public PageResult findAllBrandAddPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.findAllBrand();
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void addBrand(TbBrand brand) {
        brandMapper.addBrand(brand);
    }

    @Override
    public TbBrand findBrandById(int id) {
        return brandMapper.findBrandById(id);
    }

    @Override
    public void updateBrand(TbBrand brand) {
        brandMapper.updateBrand(brand);
    }

    @Override
    public void deleteBrands(int[] ids) {
        if (ids != null && ids.length > 0) {
            for (int id : ids) {
                brandMapper.deleteBrandById(id);
            }
        }
    }

    @Override
    public PageResult searchBrand(int pageNum, int pageSize, TbBrand tbBrand) {
        PageHelper.startPage(pageNum, pageSize);
        if (tbBrand.getName() != null && tbBrand.getName().trim() != "") {
            tbBrand.setName("%" + tbBrand.getName() + "%");
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.searchBrand(tbBrand);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }

}
