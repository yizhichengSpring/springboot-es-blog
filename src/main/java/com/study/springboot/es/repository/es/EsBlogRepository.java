package com.study.springboot.es.repository.es;

import com.study.springboot.es.entity.es.EsBlog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author yi
 * @ClassName EsBlogRepository
 * @Description TODO
 * @Date
 **/


public interface EsBlogRepository extends ElasticsearchRepository<EsBlog,Integer> {
}
