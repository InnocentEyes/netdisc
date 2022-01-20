package com.qzlnode.netdisc.util;

import org.springframework.cglib.core.ReflectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author qzlzzz
 */
@Component
public class FileInfoHandler {


    public  <T> T fileInfoToBean(MultipartFile file,String[] filePath,Class<T> claszz) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(file == null && file.isEmpty()){
            return null;
        }
        T instance = (T) ReflectUtils.newInstance(claszz);
        for (PropertyDescriptor beanSetter : ReflectUtils.getBeanSetters(claszz)) {
            String name = beanSetter.getName();
            if(name.contains("Type")){
                beanSetter.getWriteMethod().invoke(instance,file.getContentType());
                continue;
            }
            if(name.contains("Size")){
                beanSetter.getWriteMethod().invoke(instance,file.getSize());
                continue;
            }
            if(name.contains("groupName")){
                beanSetter.getWriteMethod().invoke(instance,filePath[0]);
                continue;
            }
            if(name.contains("RemotePath")){
                beanSetter.getWriteMethod().invoke(instance,filePath[1]);
                continue;
            }
        }
        return instance;
    }

    static {
        System.out.println("FileInfoHandler已创建");
    }
}
