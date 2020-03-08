package com.study.springboot.es.controller;

import com.study.springboot.es.entity.es.EsBlog;
import com.study.springboot.es.entity.mysql.MySQLBlog;
import com.study.springboot.es.repository.es.EsBlogRepository;
import com.study.springboot.es.repository.mysql.MysqlBlogRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author yi
 * @ClassName DataController
 * @Description TODO
 * @Date
 **/
@RestController
@Slf4j
public class DataController {

    @Autowired
    private MysqlBlogRepository mysqlBlogRepository;
    @Autowired
    private EsBlogRepository esBlogRepository;

    @GetMapping("/blogs")
    public Object blog(){
        List<MySQLBlog> mysqlBlogs = mysqlBlogRepository.findAll();
        return mysqlBlogs;
    }

    @PostMapping("/search")
    public Object search(@RequestBody Param param){
        Map<String, Object> map = new HashMap<>(10);
        String type = param.getType();
        StopWatch watch = new StopWatch();
        watch.start();
        if(type.equalsIgnoreCase("mysql")){
            List<MySQLBlog> mysqlBlogs = mysqlBlogRepository.queryBlogs(param.getKeyword());
            map.put("list",mysqlBlogs);
        }else if(type.equalsIgnoreCase("es")){
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            builder.should(QueryBuilders.matchPhraseQuery("title",param.getKeyword()));
            builder.should(QueryBuilders.matchPhraseQuery("content",param.getKeyword()));
            String s = builder.toString();
            log.info(">>> {} <<<",s);
            //Iterable<EsBlog> esBlogs = esBlogRepository.search(builder);
            Page<EsBlog> esBlogPage = (Page<EsBlog>)esBlogRepository.search(builder);
//            List<EsBlog> esBlogList = new ArrayList<>();
//            for (EsBlog esBlog : esBlogs) {
//                esBlogList.add(esBlog);
//            }
            map.put("list",esBlogPage.getContent());
        }else {
            return ">>> 不知道 <<<";
        }
        watch.stop();
        Long totalTimeMillis = watch.getTotalTimeMillis();
        map.put("duration",totalTimeMillis);
        return map;
    }

    @Data
    public static class Param{
        // String,es
        private String type;
        private String keyword;
    }
}
