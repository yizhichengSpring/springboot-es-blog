package com.study.springboot.es.controller;

import com.study.springboot.es.entity.es.EsBlog;
import com.study.springboot.es.entity.mysql.MySQLBlog;
import com.study.springboot.es.repository.es.EsBlogRepository;
import com.study.springboot.es.repository.mysql.MysqlBlogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author yi
 * @ClassName IndexController
 * @Description TODO
 * @Date
 **/
@RestController
@Slf4j
public class IndexController {

    @Autowired
    private MysqlBlogRepository mysqlBlogRepository;
    @Autowired
    private EsBlogRepository esBlogRepository;

    @RequestMapping("/")
    public String index(){
//        List<MySQLBlog> all = mysqlBlogRepository.findAll();
//        log.info(">>> {} <<<",all.size());
        Iterable<EsBlog> all = esBlogRepository.findAll();
        Iterator<EsBlog> iterator = all.iterator();
        EsBlog next = iterator.next();
        log.info(">>> {} <<<",next.getTitle());
        return "index.html";
    }

    @GetMapping("/blog/{id}")
    public Object blog(@PathVariable Long id) {
        Optional<MySQLBlog> byId = mysqlBlogRepository.findById(id);
        MySQLBlog mySQLBlog =  byId.get();
        return mySQLBlog;
    }
}
