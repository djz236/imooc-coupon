package com.imooc.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @description:  Kafka 相关的服务接口定义
 * @param:
 * @return:
 * @author Administrator
 * @date: 2021/1/22 22:49
 */
public interface IKafkaService {
    /**
     * <h2>消费优惠卷 Kafka 消息</h2>
     * @param record {@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?,?> record);
}
