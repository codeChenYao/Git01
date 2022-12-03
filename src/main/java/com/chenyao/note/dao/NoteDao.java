package com.chenyao.note.dao;

import cn.hutool.core.util.StrUtil;
import com.chenyao.note.po.Note;
import com.chenyao.note.util.DBUtil;
import com.chenyao.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {

    public int addOrUpdate(Note note) {
        String sql = "";
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());
        if (note.getNoteId() == null) {
            sql = "insert into tb_note (typeId,title,content,pubTime,lon,lat) values(?,?,?,now(),?,?)";
            params.add(note.getLon());
            params.add(note.getLat());
        } else {
            sql = "update tb_note set typeId= ? ,title= ? ,content= ? where noteId= ? ";
            params.add(note.getNoteId());
        }
        int row = BaseDao.executeUpdate(sql, params);
        return row;
    }

    public long findNoteCount(Integer userId, String title, String date, String typeId) {
        String sql = "select count(t.typeId) from tb_note n join tb_note_type t on n.typeId=t.typeId where t.userId = ? ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        // 判断条件查询的参数是否为空 （如果查询的参数不为空，则拼接sql语句，并设置所需要的参数）
        if (!StrUtil.isBlank(title)) {
            sql += "and title like concat('%',?,'%')";
            params.add(title);
        }
        if (!StrUtil.isBlank(date)) {
            sql += " and date_format(pubTime,'%Y年%m月') = ?";
            params.add(date);
        }
        if (!StrUtil.isBlank(typeId)) {
            sql += "and n.typeId = ?";
            params.add(typeId);
        }
        long count = (long) BaseDao.findSingleValue(sql, params);
        return count;
    }

    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize, String title, String date, String typeId) {
        String sql = "select noteId,title,pubTime from tb_note n join tb_note_type t on " +
                "n.typeId=t.typeId where t.userId = ? ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        // 判断条件查询的参数是否为空 （如果查询的参数不为空，则拼接sql语句，并设置所需要的参数）
        if (!StrUtil.isBlank(title)) {
            sql += "and title like concat('%',?,'%') ";
            params.add(title);
        }
        if (!StrUtil.isBlank(date)) {
            sql += " and date_format(pubTime,'%Y年%m月') = ?";
            params.add(date);
        }
        if (!StrUtil.isBlank(typeId)) {
            sql += "and n.typeId = ?";
            params.add(typeId);
        }
        sql += "order by pubTime desc limit ?,?";
        params.add(index);
        params.add(pageSize);
        List<Note> noteList = BaseDao.querRows(sql, params, Note.class);
        return noteList;
    }

    public List<NoteVo> findNoteCountByDate(Integer userId) {
        String sql = "select count(1) noteCount,DATE_FORMAT(n.pubTime,'%Y年%m月') groupName from tb_note n " +
                " join tb_note_type t on n.typeId = t.typeId where userId = ? group by DATE_FORMAT(n.pubTime,'%Y年%m月') " +
                " order by DATE_FORMAT(n.pubTime,'%Y年%m月') desc";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteVo> list = BaseDao.querRows(sql, params, NoteVo.class);
//        System.out.println("list:"+list);
        return list;
    }

    public List<NoteVo> findNoteCountByType(Integer userId) {
        String sql = "select count(noteId) noteCount, t.typeId, typeName groupName from tb_note n right join tb_note_type t on" +
                " n.typeId = t.typeId where userId = ? group by t.typeId order by count(1) desc";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteVo> voList = BaseDao.querRows(sql, params, NoteVo.class);
//            System.out.println("voList:"+voList);
        return voList;
    }

    public Note findNoteById(String noteId) {
        String sql = "select noteId,title,content,pubTime,typeName,n.typeId from tb_note n join tb_note_type t on " +
                " n.typeId=t.typeId where noteId = ?";
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        Note note = (Note) BaseDao.querRow(sql, params, Note.class);
        return note;
    }

    public int deleteNote(String noteId) {
        String sql = "delete from tb_note where noteId =?";
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        int row = BaseDao.executeUpdate(sql, params);
        return row;
    }

    public List<Note> queryNoteList(Integer userId) {
        String sql ="select lon, lat from  tb_note n inner join tb_note_type t on n.typeId = t.typeId where userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<Note> list = BaseDao.querRows(sql,params,Note.class);
        System.out.println("list:"+list);
        return list;
    }
}
