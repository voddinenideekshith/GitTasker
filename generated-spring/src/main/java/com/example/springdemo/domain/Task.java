package com.example.springdemo.domain;

public class Task {
    private Long id;
    private String title;
    private String description;
    private String cid;
    private String status;
    private String owner;

    public Task() {}

    public Task(Long id, String title, String description, String cid, String status, String owner) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cid = cid;
        this.status = status;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
