package com.chenyao.note.filter;

import cn.hutool.core.util.StrUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")//过滤所有资源
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //基于Http
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //处理post请求（只针对post请求有效，get请求不受影响）
        request.setCharacterEncoding("UTF-8");
        //得到请求类型
        String method = request.getMethod();
        //如果是get请求，判断服务器版本
        if ("GET".equalsIgnoreCase(method)) {
            //得到服务器版本
            String serverInfo = request.getServletContext().getServerInfo();
            //通过截取字符串，得到具体的版本号
            String version = serverInfo.substring(serverInfo.lastIndexOf("/") + 1, serverInfo.indexOf("."));
            //判断服务器版本是否是在tomcat7及以下
            if (version != null && Integer.parseInt(version) < 8) {
                //7及以下版本服务器的get请求
                MyWapper myRequest = new MyWapper(request);
                //放行资源
                filterChain.doFilter(myRequest, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    /**
     * 1. 定义内部类 （类的本质是request对象）
     * 2. 继承 HttpServletRequestWrapper包装类
     * 3. 重写getarameterP()方法
     */
    class MyWapper extends HttpServletRequestWrapper {
        // 定义成员变量 HttpServletRequest对象 （提升构造器中request对象的作用域）
        private HttpServletRequest request;

        /**
         * 带参构造
         * 可以得到需要处理的request对象
         *
         * @param request
         */
        public MyWapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        /**
         * 重写getParameter方法，处理乱码问题
         *
         * @param name
         * @return
         */
        @Override
        public String getParameter(String name) {
            //获取参数（乱码的参数值）
            String value = request.getParameter(name);
            //判断数值是否为空
            if (StrUtil.isBlank(value)) {
                return value;
            }
            //通过new string（）处理乱码
            try {
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return value;
        }
    }
}