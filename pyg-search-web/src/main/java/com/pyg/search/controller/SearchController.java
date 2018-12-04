package com.pyg.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.search.service.SearchService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Reference
    private SearchService searchService;


    @RequestMapping("/searchByParam")
    private Map searchByParam(@RequestBody Map paramMap){
        return searchService.searchByParam(paramMap);
    }

}
