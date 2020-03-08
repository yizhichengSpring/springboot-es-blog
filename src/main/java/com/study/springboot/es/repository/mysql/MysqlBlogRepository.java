package com.study.springboot.es.repository.mysql;

import com.study.springboot.es.entity.mysql.MySQLBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MysqlBlogRepository extends JpaRepository<MySQLBlog,Long> {


    @Query("select e from MySQLBlog e where e.title like concat('%',:keyword,'%') " +
            "or e.content like concat('%',:keyword,'%') order by e.createTime desc")
    List<MySQLBlog> queryBlogs(@Param("keyword") String keyword);
}
