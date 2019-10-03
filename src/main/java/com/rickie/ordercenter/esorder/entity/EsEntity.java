package com.rickie.ordercenter.esorder.entity;

/**
 * Elasticsearch 实体类
 */
public final class EsEntity<T> {

    private String id;
    private String indexName;
    private T data;

    public EsEntity() {
    }

    public EsEntity(String id, T data) {
        this.data = data;
        this.id = id;
    }

    public EsEntity(String id, String indexName, T data) {
        this.data = data;
        this.id = id;
        this.indexName = indexName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

