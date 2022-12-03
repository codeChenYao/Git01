package com.chenyao.note.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonUtil {
    public static void toJson(HttpServletResponse resp,Object result){
        //设置响应类型及编码格式
        resp.setContentType("application/json;charset=UTF-8");
        //得到输出流
        PrintWriter out = null;
        try {
            out = resp.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //通过fastjson的方法，将ResultInfo对象转换成json格式的字符串
        String json = JSON.toJSONString(result);
        //通过输出流输出json格式的字符串
        out.write(json);
        //关闭资源
        out.close();
    }
}
