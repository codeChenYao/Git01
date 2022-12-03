package com.chenyao.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.chenyao.note.dao.UserDao;
import com.chenyao.note.po.User;
import com.chenyao.note.vo.ResultInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class UserService {
    private UserDao userDao = new UserDao();

    public  Integer checkNick(String nick, Integer userId) {
        //* 1. 判断昵称是否为空
        if (StrUtil.isBlank(nick)){
            return 0;
        }
        //* 2. 调用Dao层，通过用户ID和昵称查询用户对象
        User user = userDao.queryUserByNickAndUserId(nick,userId);
        //* 3. 判断用户对象存在
        if (user!=null){
            return 0;
        }
        return 1;
    }

    public ResultInfo<User> userLogin(String userName,String userPwd){
        ResultInfo<User> resultInfo = new ResultInfo<>();
        User u = new User();
        u.setUname(userName);
        u.setUpwd(userPwd);
        resultInfo.setResult(u);
        //用户名或密码为空
        if (StrUtil.isBlank(userName)||StrUtil.isBlank(userPwd)){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户名或密码不能为空！");
            return resultInfo;
        }
        //用户名不为空，但数据库中不存在该用户
        User user = userDao.queryUserByName(userName);
        if (user==null){
            resultInfo.setCode(0);
            resultInfo.setMsg("该用户名不存在！");
            return resultInfo;
        }
        //将传入密码与数据库中密码进行比较
        // 将前台传递的密码按照MD5算法的方式加密
        userPwd = DigestUtil.md5Hex(userPwd);
        if (!userPwd.equals(user.getUpwd())){
            resultInfo.setCode(0);
            resultInfo.setMsg("密码错误！");
            return resultInfo;
        }
        resultInfo.setCode(1);
        resultInfo.setResult(user);
        return resultInfo;
    }

    public ResultInfo updateUser(HttpServletRequest req) {
        ResultInfo<Object> resultInfo = new ResultInfo<>();
//        1. 获取参数（昵称、心情）
        String nick = req.getParameter("nick");
        String mood = req.getParameter("mood");
//        2. 参数的非空校验（判断必填参数非空）
        if (StrUtil.isBlank(nick)){
            //        如果昵称为空，将状态码和错误信息设置resultInfo对象中，返回resultInfo对象
            resultInfo.setCode(0);
            resultInfo.setMsg("用户昵称不能为空！");
            return resultInfo;
        }
//        3. 从session作用域中获取用户对象（获取用户对象中默认的头像）
        User user = (User) req.getSession().getAttribute("user");
        //设置修改的昵称和头像
        user.setNick(nick);
        user.setMood(mood);
//        4. 实现上上传文件
        try {
//        1. 获取Part对象 request.getPart("name"); name代表的是file文件域的那么属性值
            Part part = req.getPart("img");
//        2. 通过Part对象获取上传文件的文件名
            String header = part.getHeader("Content-Disposition");
            //获取具体的请求头对应的值
          String str = header.substring(header.lastIndexOf("=")+2);
          //获取上传的文件名
          String fileName = str.substring(0,str.length()-1);
//        3. 判断文件名是否为空
          if (!StrUtil.isBlank(fileName)){
              //如果用户上传了头像，则更新用户对象中的头像
              user.setHead(fileName);
        //4. 获取文件存放的路径  WEB-INF/upload/目录中
              String filePath = req.getServletContext().getRealPath("/WEB-INF/upload/");
        //5. 上传文件到指定目录
              part.write(filePath+"/"+fileName);
          }
        }catch (Exception e){
            e.printStackTrace();
        }
//        6. 调用Dao层的更新方法，返回受影响的行数
        int row = userDao.updateUser(user);
//        7. 判断受影响的行数
        if (row>0){
            resultInfo.setCode(1);
            //更新session中用户对象
            req.getSession().setAttribute("user",user);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败");
        }
        return resultInfo;
    }
}
