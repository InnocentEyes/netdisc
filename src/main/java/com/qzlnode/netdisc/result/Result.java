package com.qzlnode.netdisc.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qzlnode.netdisc.dto.ResultSerializer;

/**
 * @author qzlzzz
 * @param <T>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private int code;

    private String msg;

    @JsonSerialize(using = ResultSerializer.class)
    private T data;

    public static <T> Result<T> success(T data,CodeMsg codeMsg){
        return new Result<T>(data,codeMsg);
    }

    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    public static  <T> Result<T> success(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    public Result(T data,CodeMsg codeMsg){
        this.data = data;
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    public Result(T data){
        this.data = data;
    }

    public Result(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Result(CodeMsg codeMsg){
        if(codeMsg != null){
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

    public Result(){

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
