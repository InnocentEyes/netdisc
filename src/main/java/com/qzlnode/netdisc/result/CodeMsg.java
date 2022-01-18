package com.qzlnode.netdisc.result;

/**
 * @author qzlzzz
 */
public class CodeMsg {

    private int code;

    private String msg;

    /**
     * 通用的错误码
     */
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");

    public static CodeMsg MESSAGE_ERROR = new CodeMsg(-1,"验证失败");


    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");

    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");


    /**
     * 登录模块
     */
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500210, "登录密码不能为空");

    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500211, "手机号不能为空");

    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500212, "手机号不存在");

    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500213, "密码错误");

    /**
     * 注册模块
     */
    public static CodeMsg REGISTRATION_FAILED = new CodeMsg(500310,"注册失败");

    public static CodeMsg USER_MESSAGE_EMPTY = new CodeMsg(500311,"用户信息缺失");

    /**
     * 更新模块
     */
    public static CodeMsg UPDATE_ERROR = new CodeMsg(500410,"更新失败");

    /**
     * 文件传输端
     */
    public static CodeMsg FILE_UPLOAD_ERROR = new CodeMsg(500510,"文件上传失败");

    public static CodeMsg FILE_DELETE_ERROR = new CodeMsg(500511,"文件下载失败");

    public static CodeMsg FILE_DOWNLOAD_ERROR = new CodeMsg(500512,"文件下载失败");

    public CodeMsg() {
    }

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
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

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }

    @Override
    public String toString() {
        return "CodeMsg [code=" + code + ", msg=" + msg + "]";
    }
}
