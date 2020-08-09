package com.fh.shop.api.area.controller;

import com.fh.shop.api.area.biz.AreaService;
import com.fh.shop.api.common.ServerResponse;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/areas")
@Api(tags = "地区接口")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @GetMapping
    public ServerResponse findChrds(Long id){
        return areaService.findChrds(id);
    }

}
