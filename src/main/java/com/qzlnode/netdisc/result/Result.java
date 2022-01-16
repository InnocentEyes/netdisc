package com.qzlnode.netdisc.result;

/**
 * @author qzlzzz
 * @param <T>
 */
public class Result<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
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
