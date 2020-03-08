package com.study.springboot.es;

import com.study.springboot.es.entity.es.EsBlog;
import com.study.springboot.es.repository.es.EsBlogRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
class SpringbootEsApplicationTests {

    @Autowired
    private EsBlogRepository esBlogRepository;

    @Test
    void contextLoads() {
    }

    @Test
    public void testEs(){
        Iterable<EsBlog> all = esBlogRepository.findAll();
        for (EsBlog esBlog : all) {
            log.info(">>> {} <<<",esBlog.getTitle());
        }

    }

}
