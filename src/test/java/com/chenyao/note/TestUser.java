package com.chenyao.note;

import com.chenyao.note.dao.BaseDao;
import com.chenyao.note.dao.UserDao;
import com.chenyao.note.po.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestUser {
    @Test
    public void testQueryUserByName(){
        UserDao userDao = new UserDao();
        User user = userDao.queryUserByName("admin");
        System.out.println(user.getUpwd());
        System.out.println(user.getMood());

    }
    @Test
    public void testExecuteUpdate(){
        String sql = "insert into tb_user (userId,uname,upwd,nick,head,mood) value (?,?,?,?,?,?)";
        List list = new ArrayList();
        list.add("3");
        list.add("lisi");
        list.add("e10adc3949ba59abbe56e057f20f883e");
        list.add("lisi");
        list.add("404.jpg");
        list.add("Hello");
        int i = BaseDao.executeUpdate(sql, list);
        System.out.println(i);

    }
}
