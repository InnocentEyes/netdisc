package com.qzlnode.netdisc.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author qzlzzz
 */
@Component
public class VideoUtil {

    /**
     * 这里截取视频文件的第一帧图片处理后作为视频封面
     * @param filestream
     * @return
     * @throws IOException
     */
    public byte[] fetchFrame(InputStream filestream) throws IOException {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(filestream);
        frameGrabber.start();
        int length = frameGrabber.getLengthInFrames();
        Frame frame = null;
        int curFrame = 0;
        do{
            frame = frameGrabber.grabFrame();
            if (frame.image != null){
                break;
            }
            curFrame++;
        }while (curFrame < length);
        int originWidth = frame.imageWidth;
        int originHeight = frame.imageHeight;
        int width = 800;
        int height = (int) ((double)(width / originWidth) * originHeight);
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage image = converter.getBufferedImage(frame);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        bi.getGraphics().drawImage(image.getScaledInstance(width,height, Image.SCALE_SMOOTH),0,0,null);
        ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", imgStream);
        return imgStream.toByteArray();
    }
}
