package com.study.springboot.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = {"com.study.springboot.es.repository.es"})
public class SpringbootEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootEsApplication.class, args);
    }

}
