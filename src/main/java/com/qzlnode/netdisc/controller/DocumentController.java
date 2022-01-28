package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.pojo.Document;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.service.DocumentService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

/**
 * @author qzlzzz
 */
@RestController
@RequestMapping("/document")
public class DocumentController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FileInfoHandler fileInfoHandler;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private AsyncService asyncService;

    @PostMapping("/single/upload")
    public Result<Document> singleUpload(@RequestParam(value = "file",required = false) MultipartFile file)
            throws InvocationTargetException, IllegalAccessException {
        if(file == null){
            logger.info("没有参数接受至服务端");
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        if(!fileInfoHandler.isSupport(file.getOriginalFilename(),Document.class)){
            return Result.error(CodeMsg.DOCUMENT_TYPE_ERROR);
        }
        Document document = documentService.saveDocument(fileInfoHandler.fileInfoToBean(
                file,
                null,
                Document.class
        ));
        if(document == null){
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        asyncService.saveDocument(file,document.getFileId());
        return Result.success(document,CodeMsg.SUCCESS);
    }


    @ExceptionHandler({
            IllegalAccessException.class,
            InvocationTargetException.class
    })
    public Result handlerError(Exception exception, HttpServletRequest request){
        String ip = Security.getIPAddress(request);
        MessageHolder.clearData();
        logger.error("handler {} error. ip address is {}.\n" +
                "the reason is {}",request.getRequestURL(),ip,exception.getMessage());
        return Result.error(CodeMsg.ERROR.fillArgs(exception.getMessage()));
    }
}
