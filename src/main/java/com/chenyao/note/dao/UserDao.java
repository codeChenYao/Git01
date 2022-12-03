package com.chenyao.note.dao;

import com.chenyao.note.po.User;
import com.chenyao.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao{
    public User queryUserByName(String userName){
        String sql = "select * from tb_user where uname = ?";
        List<Object> list= new ArrayList<>();
        list.add(userName);
        User user = (User) BaseDao.querRow(sql, list, User.class);
        return  user;
    }
    public User queryUserByName02(String userName){
        User user = null;
        Connection connetion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
        //1.获取数据库连接
        connetion = DBUtil.getConnetion();
        //2.定义sql语句
        String sql = "select * from tb_user where uname = ?";
        //3.预编译
        preparedStatement = connetion.prepareStatement(sql);
        //4.设置参数
        preparedStatement.setString(1,userName);
        //5.执行查询，返回结果集
        resultSet = preparedStatement.executeQuery();
        //6.判断并分析结果集
        if (resultSet.next()){
            user = new User();
            user.setUserId(resultSet.getInt("userId"));//用户id
            user.setUname(userName);
            user.setUpwd(resultSet.getString("upwd"));//用户密码
            user.setNick(resultSet.getString("nick"));//用户昵称
            user.setHead(resultSet.getString("head"));//用户头像
            user.setMood(resultSet.getString("mood"));//用户签名
        }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
        //7.关闭资源
            DBUtil.close(resultSet,preparedStatement,connetion);
        }

        return user;
    }
    //通过昵称与用户ID查询用户对象
    public User queryUserByNickAndUserId(String nick, Integer userId) {
        //1.定义SQL语句
        String sql = "select * from tb_user where nick = ? and userId != ?";
        //2.设置参数集合
        List<Object> list = new ArrayList<>();
        list.add(nick);
        list.add(userId);
        User user = (User) BaseDao.querRow(sql,list,User.class);
        return user;
    }

    public int updateUser(User user) {
        String sql = "update tb_user set nick=?,mood=?,head=?where userId =?";
        List<Object> params = new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());

        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }
}
