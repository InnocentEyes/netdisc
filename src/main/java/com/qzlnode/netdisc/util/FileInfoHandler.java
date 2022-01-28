package com.qzlnode.netdisc.util;

import com.qzlnode.netdisc.pojo.Img;
import com.qzlnode.netdisc.pojo.Music;
import com.qzlnode.netdisc.pojo.Video;
import com.sun.javafx.scene.ImageViewHelper;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

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

    private static final String FILE_ORIGIN_NAME = "OriginName";

    private static final String MUSIC_LOGO = "-";

    private static final String UNNAMED_SINGER = "未知歌手";

    private static final String[] SUPPORT_MUSIC = {"mp3", "m4a", "wav", "amr", "awb", "mka", "m3u", "pls"};

    private static final String[] SUPPORT_VIDEO = {"mpeg", "mp4", "m4v", "mkv", "mpg", "rmvb", "avi"};

    private static final String[] SUPPORT_IMG = {"jpg","jpeg","gif","png","bmp","wimp"};

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
        if(file == null && file.isEmpty() && file.getOriginalFilename() == null){
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
            if(name.contains(FILE_ORIGIN_NAME)){
                beanSetter.getWriteMethod().invoke(instance,file.getOriginalFilename());
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

    /**
     *
     * @param originName
     * @param claszz
     * @return
     */
    public boolean isSupport(String originName,Class<?> claszz){
        if(claszz.isAssignableFrom(Music.class)){
            return check(originName,SUPPORT_MUSIC);
        }
        if(claszz.isAssignableFrom(Video.class)){
            return check(originName,SUPPORT_VIDEO);
        }
        if(claszz.isAssignableFrom(Img.class)){
            return check(originName,SUPPORT_IMG);
        }
        return false;
    }

    private boolean check(String originName,String[] supports){
        if(originName == null || supports == null){
            return false;
        }
        for (String support : supports) {
            if(originName.endsWith(support)){
                return true;
            }
        }
        return false;
    }


    public Music handlerNameInfo(String fileName,Music music){
        if(!fileName.contains(MUSIC_LOGO)){
            music.setSinger(UNNAMED_SINGER);
            music.setSongName(fileName);
            return music;
        }
        String[] detail = fileName.split("-", 2);
        music.setSinger(detail[0]);
        music.setSongName(detail[1]);
        return music;
    }

    static {
        System.out.println("FileInfoHandler已创建");
    }
}
