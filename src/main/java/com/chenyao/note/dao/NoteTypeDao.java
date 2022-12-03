package com.chenyao.note.dao;

import com.chenyao.note.po.NoteType;

import java.util.ArrayList;
import java.util.List;

public class NoteTypeDao {
    public List<NoteType> findTypeListByUserId(Integer userId) {
//        1. 定义SQL语句
        String sql = "select typeId,typeName,userId from tb_note_type where userId = ? ";
//        2. 设置参数列表
        List<Object> params = new ArrayList<>();
        params.add(userId);
//        3. 调用BaseDao的查询方法，返回集合
        List<NoteType> typeList =  BaseDao.querRows(sql, params, NoteType.class);
//        4. 返回集合
        return typeList;
    }

    /**
     * 通过类型id查询查询云日记的数量并返回
     * @param typeId
     * @return
     */
    public long findNoteCountByTypeId(String typeId) {
        String sql = "select count(1) from tb_note_type where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        long noteCount = (long) BaseDao.findSingleValue(sql,params);
        return noteCount;
    }

    /**
     * 通过类型id删除指定的类型记录，返回受影响行数
     * @param typeId
     * @return
     */
    public int deleteTypeById(String typeId) {
        String sql = "delete from tb_note_type where typeId =?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }
}
