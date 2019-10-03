package com.rickie.ordercenter.esorder.service;

import com.rickie.ordercenter.esorder.entity.EsEntity;
import com.rickie.ordercenter.esorder.entity.Order;
import com.rickie.ordercenter.esorder.util.EsUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private EsUtil esUtil;

    /**
     * 新增或更新单个订单
     * @param order
     * @return
     */
    public String addOrder(Order order) {
        EsEntity<Order> esEntity = new EsEntity<Order>(
                String.valueOf(order.getOrderId()), order);

        return esUtil.insertOrUpdateOne(order.getIndexName(), esEntity);
    }

    /**
     * 新增或更新批量订单
     * @param orderList
     * @return
     */
    public Map<String, String> addOrders(List<Order> orderList) {
        List<EsEntity> lst = new ArrayList<>();
        orderList.forEach(item->lst.add(
                new EsEntity(String.valueOf(item.getOrderId()),
                        item.getIndexName(),
                        item))
        );

        return esUtil.insertBatch(lst);
    }

    /**
     * 根据OrderId获取订单
     * @param orderId
     * @return
     */
    public Order getByOrderId(long orderId) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(new TermQueryBuilder(
                "orderid", orderId
        ));
        List<Order> orders = esUtil.search(EsUtil.INDEX_ALIAS, builder, Order.class);

        if(orders.size() > 0) {
            return orders.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取全部订单
     */
    public List<Order> getAllOrders() {
        return esUtil.search(EsUtil.INDEX_ALIAS, new SearchSourceBuilder(), Order.class);
    }

    /**
     * 按照订单创建时间查询
     * @param fromCreateTime
     * @param toCreateTime
     * @return
     */
    public List<Order> searchByCreateTime(Date fromCreateTime, Date toCreateTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strFrom = df.format(fromCreateTime);
        String strTo = df.format(toCreateTime);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time")
                .from(strFrom)
                .to(strTo));
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(10).query(boolQueryBuilder);

        return esUtil.search(EsUtil.INDEX_ALIAS, builder, Order.class);
    }

    /**
     * 根据order对象删除
     * @param order
     * @return
     */
    public long deleteByOrder(Order order) {
        return esUtil.deleteByQuery(order.getIndexName(),
                new TermQueryBuilder("orderid", order.getOrderId()));
    }

    /**
     * 根据order对象列表删除
     * @param orders
     * @return
     */
    public Map<String, String> deleteOrders(List<Order> orders) {
        List<EsEntity> entities = new ArrayList<>();
        orders.forEach(item->entities.add(
                new EsEntity(String.valueOf(item.getOrderId()),
                        item.getIndexName(),
                        item)
        ));

        return esUtil.deleteBatch(entities);
    }
}
