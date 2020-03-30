package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommends")
public class RecommendController {

    @Autowired
    RecommendService recommendService;

    @PutMapping("/{id}")
    public HttpStatus pushRecommend(@PathVariable Integer id){
        recommendService.updateRecommend(id);
        return HttpStatus.OK;
    }

    @PutMapping("/{id}/cancel")
    public HttpStatus cancelRecommend(@PathVariable Integer id){
        recommendService.cancelRecommend(id);
        return HttpStatus.OK;
    }

}
