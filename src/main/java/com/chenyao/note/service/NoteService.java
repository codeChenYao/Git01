package com.chenyao.note.service;

import cn.hutool.core.util.StrUtil;
import com.chenyao.note.dao.BaseDao;
import com.chenyao.note.dao.NoteDao;
import com.chenyao.note.po.Note;
import com.chenyao.note.util.Page;
import com.chenyao.note.vo.NoteVo;
import com.chenyao.note.vo.ResultInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteService {
    private NoteDao noteDao = new NoteDao();

    /**
     * 添加或修改云记
     *
     * @param typeId
     * @param title
     * @param content
     * @return
     */
    public ResultInfo<Note> addOrUpdate(String typeId, String title, String content, String noteId, String lon, String lat) {
        ResultInfo<Note> resultInfo = new ResultInfo<>();
//        1. 参数的非空判断
//        如果为空，code=0，msg=xxx，result=note对象，返回resultInfo对象
        if (StrUtil.isBlank(typeId)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("请选择云记类型！");
            return resultInfo;
        }
        if (StrUtil.isBlank(title)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("标题不能为空！");
            return resultInfo;
        }
        if (StrUtil.isBlank(content)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("内容不能为空！");
            return resultInfo;
        }
        //设置经纬度的默认值，默认设置为北京
        if (lon == null || lat == null) {
            lon = "116.404";
            lat = "39.915";
        }

//        1. 设置回显对象 Note对象
        Note note = new Note();
        note.setTypeId(Integer.parseInt(typeId));
        note.setTitle(title);
        note.setContent(content);
        note.setLon(Float.parseFloat(lon));
        note.setLat(Float.parseFloat(lat));

        //判断noteId是否为空(用于修改操作）
        if (!StrUtil.isBlank(noteId)) {
            note.setNoteId(Integer.parseInt(noteId));
        }

        resultInfo.setResult(note);
//        2. 调用Dao层，添加云记记录，返回受影响的行数
        int row = noteDao.addOrUpdate(note);
//        3. 判断受影响的行数
        if (row > 0) {//如果大于0，code=1
            resultInfo.setCode(1);
        } else {//如果不大于0，code=0，msg=xxx，result=note对象
            resultInfo.setCode(0);
            resultInfo.setMsg("添加失败！");
            resultInfo.setResult(note);
        }
        //4. 返回resultInfo对象
        return resultInfo;
    }

    /**
     * 分页查询云记列表
     *
     * @param pageNumStr
     * @param pageSizeStr
     * @param userId
     * @return
     */
    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr,
                                         Integer userId, String title, String date, String typeId) {
        //设置参数的默认值
        Integer pageNum = 1;
        Integer pageSize = 5;
        //    1. 参数的非空校验，如果分页参数为空，则设置默认值
        if (!StrUtil.isBlank(pageNumStr)) {
            pageNum = Integer.parseInt(pageNumStr);
        }
        if (!StrUtil.isBlank(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
        }
//    2. 查询当前登录用户的云记数量，返回总记录数 （long类型）
        long count = noteDao.findNoteCount(userId, title, date, typeId);
//    3. 判断总记录数是否大于0
        if (count < 0) {
            return null;
        }
//    4. 如果总记录数大于0，调用Page类的带参构造，得到其他分页参数的值，返回Page对象
        Page<Note> page = new Page<>(pageNum, pageSize, count);
//    5. 查询当前登录用户下当前页的数据列表，返回note集合
        Integer index = (pageNum - 1) * pageSize;
        List<Note> noteList = noteDao.findNoteListByPage(userId, index, pageSize, title, date, typeId);
//    6. 将note集合设置到page对象中
        page.setDataList(noteList);
//    7. 返回Page对象
        return page;
    }

    public List<NoteVo> findNoteCountByDate(Integer userId) {
        return noteDao.findNoteCountByDate(userId);
    }

    public List<NoteVo> findNoteCountByType(Integer userId) {
        return noteDao.findNoteCountByType(userId);
    }

    public Note findNoteById(String noteId) {
        //1. 参数的非空判断
        if (StrUtil.isBlank(noteId)) {
            return null;
        }
        //2. 调用Dao层的查询，通过noteId查询note对象
        Note note = noteDao.findNoteById(noteId);
        //3. 返回note对象
        return note;
    }

    public Integer deleteNote(String noteId) {
        //判断参数
        if (StrUtil.isBlank(noteId)) {
            return 0;
        }
        //调用Dao层的更新方法，返回受影响行数
        int row = noteDao.deleteNote(noteId);
        //判断受影响行数是否大于0
        if (row > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public ResultInfo<Map<String, Object>> queryNoteCountByMonth(Integer userId) {
        ResultInfo<Map<String, Object>> resultInfo = new ResultInfo<>();
        List<NoteVo> list = new ArrayList<>();
        // 通过月份分类查询云记数量
        list = noteDao.findNoteCountByDate(userId);
        if (list != null && list.size() > 0) {// 判断集合是否存在
            List monthList = new ArrayList();// 得到月份
            List noteCountList = new ArrayList();// 得到云记集合

            // 遍历月份分组集合
            for (NoteVo noteVo : list) {
                monthList.add(noteVo.getGroupName());
                noteCountList.add(noteVo.getNoteCount());

            }
            // 准备Map对象，封装对应的月份与云记数量
            Map<String, Object> map = new HashMap<>();
            map.put("monthArray", monthList);
            map.put("dataArray", noteCountList);

            resultInfo.setCode(1);
            // 将map对象设置到ResultInfo对象中
            resultInfo.setResult(map);
        }
        return resultInfo;
    }


    public ResultInfo<List<Note>> queryNoteLonAndLat(Integer userId) {
        ResultInfo<List<Note>> resultInfo = new ResultInfo<>();
        List<Note> noteList = noteDao.queryNoteList(userId);
        // 判断是否为空
        if (noteList != null && noteList.size() > 0) {
            resultInfo.setCode(1);
            resultInfo.setResult(noteList);
        }
        return resultInfo;
    }
}
