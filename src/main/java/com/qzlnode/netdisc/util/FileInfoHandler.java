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
     * @throws NoSuchMethodException
     */
    public  <T> T fileInfoToBean(MultipartFile file,String[] filePath,Class<T> claszz) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(file == null && file.isEmpty()){
            return null;
        }
        T instance = (T) ReflectUtils.newInstance(claszz);
        for (PropertyDescriptor beanSetter : ReflectUtils.getBeanSetters(claszz)) {
            String name = beanSetter.getName();
            if(name.contains(FILE_TYPE)){
                beanSetter.getWriteMethod().invoke(instance,file.getContentType());
                continue;
            }
            if(name.contains(FILE_SIZE)){
                beanSetter.getWriteMethod().invoke(instance,file.getSize());
                continue;
            }
            if(name.contains(FILE_GROUP_NAME)){
                beanSetter.getWriteMethod().invoke(instance,filePath[0]);
                continue;
            }
            if(name.contains(FILE_REMOTE_PATH)){
                beanSetter.getWriteMethod().invoke(instance,filePath[1]);
                continue;
            }
        }
        return instance;
    }

    /**
     *
     * @param filePaths
     * @param claszz
     * @param files
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public <T> List<T> fileInfoToBean(List<String[]> filePaths, Class<T> claszz, MultipartFile[] files) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(filePaths == null || filePaths.size() == 0){
            return null;
        }
        if(files == null || files.length == 0){
            return null;
        }
        if(filePaths.size() != files.length){
            throw new InconsistentException("文件路径列表长度与文件个数不一致!");
        }
        List<T> list = new ArrayList<>();
        Iterator<String[]> filePathsIterator = filePaths.iterator();
        for (MultipartFile file : files) {
            String[] filePath = filePathsIterator.next();
            list.add(fileInfoToBean(file,filePath,claszz));
        }
        return list;
    }

    static {
        System.out.println("FileInfoHandler已创建");
    }
}
