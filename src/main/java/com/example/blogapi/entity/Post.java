package com.example.blogapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Timestamp;
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int id;
    @Column(name = "content")
    private String content;
    @Column(name = "date_published")
    private Timestamp dateCreated;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blogUser_id")
    @JsonIgnore
    private BlogUser blogUser;
    public Post(){
        this.dateCreated = new Timestamp(System.currentTimeMillis());
    }
    public Post(String content){
        this.content = content;
    }

    public Post(String content, BlogUser blogUser) {
        this.content = content;
        this.blogUser = blogUser;
        this.dateCreated = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public BlogUser getBlogUser() {
        return blogUser;
    }

    public void setBlogUser(BlogUser blogUser) {
        this.blogUser = blogUser;
    }
}
