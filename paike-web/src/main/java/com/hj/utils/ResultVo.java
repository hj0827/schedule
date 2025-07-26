package com.hj.utils;

// import lombok.AllArgsConstructor; // 如果使用了 Lombok
// import lombok.Data; // 如果使用了 Lombok

// 数据返回对象
// @Data // 如果使用了 Lombok
// @AllArgsConstructor // 如果使用了 Lombok
public class ResultVo<T> { // <--- 声明为泛型类

    private String msg;
    private int code;
    private T data; // <--- 使用泛型 T 作为数据的类型

    // 如果没有使用 Lombok，需要手动添加构造函数、getter 和 setter
    // @AllArgsConstructor 的手动实现
    public ResultVo(String msg, int code, T data) { // <--- 构造函数参数也使用泛型 T
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    // @Data 的手动实现 (getters and setters)
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
} 