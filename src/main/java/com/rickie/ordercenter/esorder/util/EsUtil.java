package com.rickie.ordercenter.esorder.util;

import com.alibaba.fastjson.JSON;
import com.rickie.ordercenter.esorder.config.ElasticsearchConfig;
import com.rickie.ordercenter.esorder.entity.EsEntity;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.IndexTemplatesExistRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;

@Component
public class EsUtil <T>{
    public static final String INDEX_TEMPLATE_NAME = "order_template";
    public static final String INDEX_TEMPLATE_JSON_FILE = "order_template.json";
    public static final String INDEX_ALIAS = "order";

    @Autowired
    public ElasticsearchConfig elasticsearchConfig;

    public RestHighLevelClient client;

    /**
     * 初始化 Index Template
     */
    @PostConstruct
    public void init() {
        try {
            if(client != null) {
                client.close();
            }
            client = elasticsearchConfig.getClient();
            if(this.indexTemplateExist(INDEX_TEMPLATE_NAME)) {
                System.out.println("Index template exists.");
                return;
            }
            String path = this.getClass().getClassLoader().getResource(INDEX_TEMPLATE_JSON_FILE).getPath();
            // 读取order template文件
            String indexTemplate = JsonUtil.readJsonFile(path);

            PutIndexTemplateRequest request = new PutIndexTemplateRequest(INDEX_TEMPLATE_NAME);
            request.source(indexTemplate, XContentType.JSON);
            AcknowledgedResponse putTemplateResponse = client.indices().putTemplate(request, RequestOptions.DEFAULT);
            if(!putTemplateResponse.isAcknowledged()) {
                throw new RuntimeException("初始化失败");
            } else {
                System.out.println("Index template has created successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @PreDestroy
    public void destory(){
        try {
            if (client != null) {
                System.out.println("释放连接 ...");
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * 判断某个index是否存在
     */
    public boolean indexExist(String index) throws Exception {
        GetIndexRequest request = new GetIndexRequest(index);
        //Whether to return local information or retrieve the state from master node
        request.local(false);
        //Return result in a format suitable for humans
        request.humanReadable(true);
        //Whether to return all default setting for each of the indices
        request.includeDefaults(false);

        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 判断索引模板是否存在
     * @param indexTemplate
     * @return
     * @throws IOException
     */
    public boolean indexTemplateExist(String indexTemplate) throws IOException {
        IndexTemplatesExistRequest request = new IndexTemplatesExistRequest(indexTemplate);
        //Whether to return local information or retrieve the state from master node
        request.setLocal(true);

        return client.indices().existsTemplate(request, RequestOptions.DEFAULT);
    }

    /**
     * 新增或更新一个document
     * @param index
     * @param entity
     * @return
     */
    public String insertOrUpdateOne(String index, EsEntity entity) {
        IndexRequest request = new IndexRequest(index);
        request.id(entity.getId());
        request.source(JSON.toJSONString(entity.getData()), XContentType.JSON);
        System.out.println(JSON.toJSONString(entity.getData()));

        try{
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return response.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 批量插入文档记录
     * @param index
     * @param list
     * @return
     */
    public Map<String, String> insertBatch(String index, List<EsEntity> list) {
        Map<String, String> ret = new HashMap<>();
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(index).id(item.getId())
            .source(JSON.toJSONString(item.getData()), XContentType.JSON)));
        try {
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            for(BulkItemResponse bulkItemResponse : bulkResponse) {
                if(bulkItemResponse.isFailed()) {
                    ret.put(bulkItemResponse.getId(), bulkItemResponse.getFailureMessage());
                } else {
                    ret.put(bulkItemResponse.getId(), "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }


    public Map<String, String> insertBatch(List<EsEntity> list) {
        Map<String, String> ret = new HashMap<>();
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(item.getIndexName())
                .id(item.getId())
                .source(JSON.toJSONString(item.getData()), XContentType.JSON)));
        try {
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            for(BulkItemResponse bulkItemResponse : bulkResponse) {
                if(bulkItemResponse.isFailed()) {
                    ret.put(bulkItemResponse.getId(), bulkItemResponse.getFailureMessage());
                } else {
                    ret.put(bulkItemResponse.getId(), "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 批量删除
     * @param entities
     */
    public Map<String, String> deleteBatch(Collection<EsEntity> entities) {
        Map<String, String> ret = new HashMap<>();
        BulkRequest request = new BulkRequest();
        entities.forEach(item -> request.add(
                new DeleteRequest(item.getIndexName(), item.getId())
        ));

        try {
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            for(BulkItemResponse bulkItemResponse : bulkResponse) {
                if(bulkItemResponse.isFailed()) {
                    ret.put(bulkItemResponse.getId(), bulkItemResponse.getFailureMessage());
                } else {
                    ret.put(bulkItemResponse.getId(), "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 搜索
     * @param index
     * @param builder
     * @param c
     * @return
     */
    public List<T> search(String index, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request = new SearchRequest(index);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();

            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }

            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * delete index
     * @param index
     */
    public boolean deleteIndex(String index) {
        try {
            AcknowledgedResponse response = client.indices().delete(
                    new DeleteIndexRequest(index), RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete by Query 根据查询结果删除
     * 返回结果：删除了多少条记录
     * @param index
     * @param builder
     */
    public long deleteByQuery(String index, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(builder);

        // 默认情况下，DeleteByQueryRequest可以批量处理1000个文档
        // 可以使用setBatchSize更改批处理大小。
        request.setBatchSize(10000);
        // 默认情况下，版本冲突会中止DeleteByQueryRequest进程
        // 下面设置版本冲突时继续
        request.setConflicts("proceed");
        try{
            BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);

            return bulkResponse.getDeleted();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
