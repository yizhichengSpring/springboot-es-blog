package com.study.springboot.es.entity.mysql;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author yi
 * @ClassName MySQLBlog
 * @Description TODO
 * @Date
 **/
@Table(name = "tb_blog")
@Data
@Entity
public class MySQLBlog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    @Column(columnDefinition = "mediumtext")
    private String content;
    private Date createTime;
    private Date updateTime;



}
