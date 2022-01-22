package com.qzlnode.netdisc.util;

import com.qzlnode.netdisc.exception.InconsistentException;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author qzlzzz
 */
@SuppressWarnings("unchecked")
@Component
public class FileInfoHandler {

    private static final String FILE_TYPE = "Type";

    private static final String FILE_SIZE = "Size";

    private static final String FILE_GROUP_NAME = "groupName";

    private static final String FILE_REMOTE_PATH = "RemotePath";

    /**
     *
     * @param file
     * @param filePath
     * @param claszz
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public  <T> T fileInfoToBean(MultipartFile file, String[] filePath, Class<T> claszz)
            throws InvocationTargetException, IllegalAccessException{
        if(file == null && file.isEmpty()){
            return null;
        }
        T instance = (T) ReflectUtils.newInstance(claszz);
        for (PropertyDescriptor beanSetter : ReflectUtils.getBeanSetters(claszz)) {
            String name = beanSetter.getName();
            if(name.contains(FILE_TYPE)){
                beanSetter.getWriteMethod().invoke(instance,file.getContentType());
            }
            if(name.contains(FILE_SIZE)){
                beanSetter.getWriteMethod().invoke(instance,file.getSize());
            }
            if(name.contains(FILE_GROUP_NAME)){
                beanSetter.getWriteMethod().invoke(instance,filePath[0]);
            }
            if(name.contains(FILE_REMOTE_PATH)){
                beanSetter.getWriteMethod().invoke(instance,filePath[1]);
            }
        }
        return instance;
    }

    /**
     *
     * @param resPath
     * @param claszz
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public <T> T pathToBean(String[] resPath,Class<T> claszz)
            throws InvocationTargetException, IllegalAccessException {
        T instance =  (T)ReflectUtils.newInstance(claszz);
        for (PropertyDescriptor beanSetter : ReflectUtils.getBeanSetters(claszz)) {
            String name = beanSetter.getName();
            if(name.contains(FILE_GROUP_NAME)){
                beanSetter.getWriteMethod().invoke(instance,resPath[0]);
            }
            if(name.contains(FILE_REMOTE_PATH)){
                beanSetter.getWriteMethod().invoke(instance,resPath[0]);
            }
        }
        return instance;
    }

    static {
        System.out.println("FileInfoHandler已创建");
    }
}
