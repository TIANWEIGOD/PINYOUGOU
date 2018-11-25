package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.manager.service.BrandService;
import com.pyg.pojo.TbBrand;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAllBrand")
    public List<TbBrand> findAllBrand() {
        List<TbBrand> allBrand = brandService.findAllBrand();

        // System.out.println(allBrand);
        return allBrand;
    }

    @RequestMapping("/findAllBrandAddPage")
    public PageResult findAllBrandAddPage(int pageNum, int pageSize) {
        return brandService.findAllBrandAddPage(pageNum, pageSize);
    }

    @RequestMapping("addBrand")
    public PygResult addBrand(@RequestBody TbBrand brand) {
        System.out.println(brand);
        try {
            brandService.addBrand(brand);
            return new PygResult(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "添加失败");
        }
    }

    @RequestMapping("findBrandById")
    public TbBrand findBrandById(int id) {
        System.out.println(id);
        return brandService.findBrandById(id);
    }

    @RequestMapping("updateBrand")
    public PygResult updateBrand(@RequestBody TbBrand brand) {
        System.out.println(brand);
        try {
            brandService.updateBrand(brand);
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "修改失败");
        }
    }

    @RequestMapping("deleteBrands")
    public PygResult deleteBrands(int[] ids) {
        for (int id : ids) {
            System.out.println(id);
        }
        try {
            brandService.deleteBrands(ids);
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }

    @RequestMapping("searchBrand")
    public PageResult searchBrand(int pageNum, int pageSize, TbBrand tbBrand) throws UnsupportedEncodingException {
        if (tbBrand.getName() != null && tbBrand.getName().trim() != "") {
            // 解决get乱码问题
            tbBrand.setName(new String(tbBrand.getName().getBytes("ISO8859-1"), "utf-8"));
        }
        System.out.println(tbBrand);
        return brandService.searchBrand(pageNum, pageSize, tbBrand);
    }

    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
