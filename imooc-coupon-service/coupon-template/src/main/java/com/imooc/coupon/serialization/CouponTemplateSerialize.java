package com.imooc.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/** 
 * @description: 优惠券模板实体类自定义序列化器 
 * @param:  
 * @return:
 * @author Administrator
 * @date: 2021/1/18 17:37
 */ 
public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {
    @Override
    public void serialize(CouponTemplate value, JsonGenerator generator,
                          SerializerProvider serializerProvider) throws IOException {
        //开始序列化对象
       generator.writeStartObject();
       generator.writeStringField("id",value.getId().toString());
       generator.writeStringField("name",value.getName());
       generator.writeStringField("logo",value.getLogo());
       generator.writeStringField("desc",value.getDesc());
       generator.writeStringField("category",value.getCategory().getDescription());
       generator.writeStringField("productLine",value.getProductLine().getDescription());
       generator.writeStringField("count",value.getCount().toString());
       generator.writeStringField("createTime",
            new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").format(value.getCreateTime()));
       generator.writeStringField("userId",value.getUserId().toString());
       generator.writeStringField("key",value.getKey()+String.format("%04d",value.getId()));
       generator.writeStringField("target",value.getTarget().getDescription());
       generator.writeStringField("rule", JSON.toJSONString(value.getRule()));
       //generator结束序列化对象
       generator.writeEndObject();

    }
}
