package com.pyg.manager.service;

import com.pyg.pojo.TbBrand;
import com.pyg.utils.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    List<TbBrand> findAllBrand();

    PageResult findAllBrandAddPage(int pageNum, int pageSize);

    void addBrand(TbBrand brand);

    TbBrand findBrandById(int id);

    void updateBrand(TbBrand brand);

    void deleteBrands(int[] ids);

    PageResult searchBrand(int pageNum, int pageSize, TbBrand tbBrand);

    List<Map> selectOptionList();
}
