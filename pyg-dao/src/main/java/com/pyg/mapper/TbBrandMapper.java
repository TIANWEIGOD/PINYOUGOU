package com.pyg.mapper;

import com.github.pagehelper.Page;
import com.pyg.pojo.TbBrand;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface TbBrandMapper {


    /*@Select("select * from tb_brand")
    @Results({
            @Result(column = "first_char",property = "firstChar")
    })*/
    List<TbBrand> findAllBrand();

    void addBrand(TbBrand brand);

    TbBrand findBrandById(int id);

    void updateBrand(TbBrand brand);

    void deleteBrandById(int id);

    List<TbBrand> searchBrand(TbBrand tbBrand);

    List<Map> selectOptionList();
}
