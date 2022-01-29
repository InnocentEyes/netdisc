package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.exception.UploadFileToLargeException;
import com.qzlnode.netdisc.pojo.Document;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.service.DocumentService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.Security;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author qzlzzz
 */
@RestController
@RequestMapping("/document")
public class DocumentController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Integer MAX_DOCUMENT_UPLOAD = 3;

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
        asyncService.saveDocument(file,document.getFileId(),MessageHolder.getUserId());
        return Result.success(document,CodeMsg.SUCCESS);
    }

    @GetMapping("/getDetail/{documentId}")
    public Result<Document> getMusic(@PathVariable("documentId") Integer fileId){
        if(fileId < 1){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        Document document = documentService.getDocument(fileId);
        if(document == null){
            return Result.error(CodeMsg.FILE_NO_EXIST);
        }
        return Result.success(document,CodeMsg.SUCCESS);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> download(@PathVariable("documentId") Integer fileId,HttpServletRequest request)
            throws MyException, IOException {
        if(fileId < 1){
            String realIp = Security.getIPAddress(request);
            logger.error("{},参数错误",realIp);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Document document = documentService.getDocument(fileId);
        if(document == null){
            logger.error("无文件");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        byte[] downloadRes = documentService.download(document.getGroupName(),document.getFileRemotePath());
        if(downloadRes == null){
            logger.error("查找不到文件");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(document.getFileSize());
        headers.setContentDispositionFormData("attachment",document.getFileOriginName());
        return new ResponseEntity<>(downloadRes,headers,HttpStatus.OK);
    }


    @PostMapping("/multi/upload")
    public Result<List<Document>> multiUpload(@RequestParam(value = "documents",required = false) MultipartFile[] files)
            throws InvocationTargetException, IllegalAccessException {
        if(files == null){
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        if(files.length < MAX_DOCUMENT_UPLOAD){
            throw new UploadFileToLargeException("文件上传超出限制");
        }
        List<Document> documents = documentService.saveBatchDocument(files);
        if(documents == null){
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        asyncService.saveBatchDocument(
                files,
                documents.stream().map(Document::getFileId).toArray(Integer[]::new),
                MessageHolder.getUserId());
        return Result.success(documents,CodeMsg.SUCCESS);
    }

    @GetMapping("/user/get")
    public Result<List<Document>> getAllDocument(){
        List<Document> documents = documentService.getBatchDocument();
        if(documents == null){
            return Result.error(CodeMsg.FILE_NO_EXIST);
        }
        return Result.success(documents,CodeMsg.SUCCESS);
    }

}
