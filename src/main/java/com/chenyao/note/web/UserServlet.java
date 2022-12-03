package com.chenyao.note.web;

import com.chenyao.note.po.User;
import com.chenyao.note.service.UserService;
import com.chenyao.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/user")
@MultipartConfig
public class UserServlet extends HttpServlet {
    private UserService userService = new UserService();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置首页高亮
        req.setAttribute("menu_page","user");

        String actionName = req.getParameter("actionName");
        if ("login".equals(actionName)){
            login(req,resp);
        }else if ("logout".equals(actionName)){
            logout(req,resp);
        }else if ("userCenter".equals(actionName)){
            userCenter(req,resp);
        }else if ("userHead".equals(actionName)){
            userHead(req,resp);
        }else if ("checkNick".equals(actionName)){
            //验证昵称的唯一性
            checkNick(req,resp);
        }else if ("updateUser".equals(actionName)) {
            // 修改用户信息
            updateUser(req, resp);
        }
    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        1. 调用Service层的方法，传递request对象作为参数，返回resultInfo对象
        ResultInfo resultInfo = userService.updateUser(req);
//        2. 将resultInfo对象存到request作用域中
        req.setAttribute("resultInfo",resultInfo);
//        3. 请求转发跳转到个人中心页面 （user?actionName=userCenter）
        req.getRequestDispatcher("user?actionName=userCenter").forward(req,resp);
    }

    private void checkNick(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //*  1. 获取参数（昵称）
        String nick = req.getParameter("nick");
        //*  2. 从session作用域获取用户对象，得到用户ID
        User user = (User) req.getSession().getAttribute("user");
        //*  3. 调用Service层的方法，得到返回的结果
        Integer code = userService.checkNick(nick,user.getUserId());
        //*  4. 通过字符输出流将结果响应给前台的ajax的回调函数
        resp.getWriter().write(code+"");
        //*  5. 关闭资源
        resp.getWriter().close();
    }

    //加载头像
    private void userHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //*  1. 获取参数 （图片名称）
        String imageName = req.getParameter("imageName");
        //*  2. 得到图片的存放路径 （request.getServletContext().getealPathR("/")）
        String realPath = req.getServletContext().getRealPath("/WEB-INF/upload/");
        //*  3. 通过图片的完整路径，得到file对象
        File file = new File(realPath + "/" +imageName);
        //*  4. 通过截取，得到图片的后缀
        String pic =imageName.substring(imageName.lastIndexOf(".")+1);
        //*  5. 通过不同的图片后缀，设置不同的响应的类型
        if ("PNG".equalsIgnoreCase(pic)){
            resp.setContentType("image/png");
        }else if ("JPG".equalsIgnoreCase(pic)||"JPEG".equalsIgnoreCase(pic)){
            resp.setContentType("image/jpeg");
        }else if ("GIF".equalsIgnoreCase(pic)){
            resp.setContentType("image/gif");
        }
        //*  6. 利用FileUtils的copyFile()方法，将图片拷贝给浏览器
        FileUtils.copyFile(file,resp.getOutputStream());
    }

    private void userCenter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("changePage","user/info.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //销毁session对象
        req.getSession().invalidate();
        //删除cookie对象
        Cookie cookie = new Cookie("user",null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        //重定向跳转至登录页面
        resp.sendRedirect("login.jsp");
    }

    private void login(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException {
        String userName = req.getParameter("userName");
        String userPwd = req.getParameter("userPwd");
        ResultInfo<User> resultInfo = userService.userLogin(userName,userPwd );
        //通过code的值判断用户是否登录成功，如果为1，表示登录成功
        if (resultInfo.getCode()==1){
            //将用户信息设置到session作用域中
            req.getSession().setAttribute("user",resultInfo.getResult());
            //通过rem判断用户是否需要记住密码
            String  rem = req.getParameter("rem");
            if ("1".equals(rem)){
                Cookie cookie = new Cookie("user",userName+"-"+userPwd);
                cookie.setMaxAge(3*24*60*60);
                resp.addCookie(cookie);
            }else {
                Cookie cookie = new Cookie("user", null);
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
            resp.sendRedirect("index");
        }else {
            req.setAttribute("resultInfo",resultInfo);
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }
}
