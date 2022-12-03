package com.chenyao.note.dao;

import com.chenyao.note.po.NoteType;
import com.chenyao.note.util.DBUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础的JDBC操作类
 * 更新操作 （添加、修改、删除）      executeUpdate()
 * 查询操作
 * 1. 查询一个字段 （只会返回一条记录且只有一个字段；常用场景：查询总数量）  findSingleValue()
 * 2. 查询集合        querRows()
 * 3. 查询某个对象    querRow()
 */
public class BaseDao {
    /**
     * 更新操作
     * 添加、修改、删除
     * 1. 得到数据库连接
     * 2. 定义sql语句 （添加语句、修改语句、删除语句）
     * 3. 预编译
     * 4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
     * 5. 执行更新，返回受影响的行数
     * 6. 关闭资源
     * <p>
     * 注：需要两个参数:sql语句、所需参数的集合
     *
     * @param sql
     * @param params
     * @return
     */
    public static int executeUpdate(String sql, List<Object> params) {
        int row = 0;
        Connection connetion = null;
        PreparedStatement preparedStatement = null;
        try {
            //获取连接
            connetion = DBUtil.getConnetion();
            //预编译
            preparedStatement = connetion.prepareStatement(sql);
            //如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
            if (params != null && params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }
            //执行更新，返回受影响的行数
            row = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, preparedStatement, connetion);
        }
        return row;
    }

    /**
     * 查询一个字段 （只会返回一条记录且只有一个字段；常用场景：查询总数量）
     * 1. 得到数据库连接
     * 2. 定义sql语句
     * 3. 预编译
     * 4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
     * 5. 执行查询，返回结果集
     * 6. 判断并分析结果集
     * 7. 关闭资源
     * <p>
     * 注：需要两个参数:sql语句、所需参数的集合
     *
     * @param sql
     * @param params
     * @return
     */
    public static Object findSingleValue(String sql, List<Object> params) {
        Object object = null;
        Connection connetion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //得到数据库连接
            connetion = DBUtil.getConnetion();
            //预编译
            preparedStatement = connetion.prepareStatement(sql);
            //如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
            if (params.size() > 0 && params != null) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }
            //执行查询，返回结果集
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                object = resultSet.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(resultSet, preparedStatement, connetion);
        }
        return object;
    }

    /**
     * 查询集合 （JavaBean中的字段与数据库中表的字段对应）
     * 1. 获取数据库连接
     * 2. 定义SQL语句
     * 3. 预编译
     * 4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
     * 5. 执行查询，得到结果集
     * 6. 得到结果集的元数据对象（查询到的字段数量以及查询了哪些字段）
     * 7. 判断并分析结果集
     * 8. 实例化对象
     * 9. 遍历查询的字段数量，得到数据库中查询到的每一个列名
     * 10. 通过反射，使用列名得到对应的field对象
     * 11. 拼接set方法，得到字符串
     * 12. 通过反射，将set方法的字符串反射成类中的指定set方法
     * 13. 通过invoke调用set方法
     * 14. 将对应的JavaBean设置到集合中
     * 15. 关闭资源
     *
     * @param sql
     * @param params
     * @param cls
     * @return
     */
    public static List querRows(String sql, List<Object> params, Class cls) {
        List list = new ArrayList();
        Connection connetion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //获取数据库连接
            connetion = DBUtil.getConnetion();
            //预编译
            preparedStatement = connetion.prepareStatement(sql);
            //如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
            if (params != null && params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(1 + i, params.get(i));
                }
            }
            //执行查询，获得结果集
            resultSet = preparedStatement.executeQuery();
            //得到结果集的元数据对象（查询到的字段数量以及查询了哪些字段）
            ResultSetMetaData metaData = resultSet.getMetaData();
            //// 得到查询的字段数量
            int fieldNum = metaData.getColumnCount();
            //判断并分析结果集
            while (resultSet.next()) {
                //实例化对象
                Object object = cls.newInstance();
                for (int i = 1; i <= fieldNum; i++) {
                    //通过查询的字段数量，得到数据库中查询到的每一个列名
                    String columnName = metaData.getColumnLabel(i);
                    //通过反射，使用列名得到对应的field对象
                    Field field = cls.getDeclaredField(columnName);
                    //拼接set方法，得到字符串
                    String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    //通过反射，将set方法的字符串反射成类中的指定set方法
                    Method method = cls.getDeclaredMethod(setMethod, field.getType());
                    //通过invoke调用set方法
                    method.invoke(object, resultSet.getObject(columnName));
                }
                //将对应的JavaBean设置到集合中
                list.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(resultSet, preparedStatement, connetion);
        }
        return list;
    }

    /**
     * 查询某个对象
     *
     * @param sql
     * @param params
     * @param cls
     * @return
     */
    public static Object querRow(String sql, List<Object> params, Class cls) {
        Object object = null;
        List list = querRows(sql, params, cls);
        if (list != null && list.size() > 0) {
            object = list.get(0);
        }
        return object;
    }
    /**
     * 查询当前登录用户下，类型名称是否唯一
     *     返回1，表示成功
     *     返回0，表示失败
     * @param typeName
     * @param userId
     * @param typeId
     * @return
     */
    public static Integer checkTypeName(String typeName, Integer userId, String typeId) {
        String sql = "select * from tb_note_type where userId = ? and typeName = ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(typeName);
        NoteType noteType = (NoteType) BaseDao.querRow(sql,params,NoteType.class);
        //如果对象为空，表示可用
        if (noteType==null){
            return 1;
        }else {
            //如果是修改操作，则需要判断是否是当前记录本身
            if (typeId.equals(noteType.getTypeId().toString())){
                return 1;
            }
        }
        return 0;
    }

    /**
     * 添加方法，返回主键
     * @param typeName
     * @param userId
     * @return
     */
    public static Integer addType(String typeName, Integer userId) {
        Integer key = null;
        Connection connection=null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet =null;
        try{
            connection = DBUtil.getConnetion();
            String sql = "insert into tb_note_type (typeName,userId) value (?,?)";
            preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,typeName);
            preparedStatement.setInt(2,userId);
            //执行更新操作，返回受影响的行数
            int row = preparedStatement.executeUpdate();
            if (row>0){
                resultSet=preparedStatement.getGeneratedKeys();
                if (resultSet.next()){
                    key = resultSet.getInt(1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet,preparedStatement,connection);
        }
        return key;
    }
    /**
     * 修改方法，返回受影响的行数
     * @param typeName
     * @param typeId
     * @return
     */
    public static Integer updateType(String typeName, String typeId) {
        String sql = "update tb_note_type set typeName=? where typeId=?";
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(typeId);
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }

}
