package com.qzlnode.netdisc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;

import java.util.List;

/**
 * @author qzlzzz
 */
public class JsonUtil {

    //定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> String objectToJson(T data){
        try{
            String json = MAPPER.writeValueAsString(data);
            return json;
        }catch (JsonProcessingException e){
            throw new RuntimeException("序列化错误");
        }
    }

    public static <T> T jsonToObject(String json,Class<T> beanType){
        if(json == null){
            return null;
        }
        if (beanType == Integer.class || beanType == String.class){
            return (T) json;
        }
        try {
            T instance = MAPPER.readValue(json,beanType);
            return instance;
        }catch (JsonProcessingException e){
            throw new RuntimeException("反序列化错误");
        }
    }

    public static <T> List<T> jsonToList(String json,Class<T> beanType){
        if(json == null){
            return null;
        }
        CollectionLikeType collectionLikeType = MAPPER.getTypeFactory().constructCollectionLikeType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(json,collectionLikeType);
            return list;
        }catch (JsonProcessingException e){
            throw new RuntimeException("list 反序列化错误.");
        }
    }
}
