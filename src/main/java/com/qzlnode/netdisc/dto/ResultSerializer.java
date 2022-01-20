package com.qzlnode.netdisc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.qzlnode.netdisc.pojo.Img;

import java.io.IOException;

public class ResultSerializer extends JsonSerializer {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(Object values, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        if(values instanceof Img){
            mapper.writerWithView(Img.ImgView.class);
            jsonGenerator.setCodec(mapper);
            jsonGenerator.writeObject(values);
            return;
        }
        jsonGenerator.setCodec(mapper);
        jsonGenerator.writeObject(values);
    }
}
