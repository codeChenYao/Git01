package com.chenyao.note.web;

import com.chenyao.note.po.Note;
import com.chenyao.note.po.User;
import com.chenyao.note.service.NoteService;
import com.chenyao.note.util.JsonUtil;
import com.chenyao.note.vo.NoteVo;
import com.chenyao.note.vo.ResultInfo;

import javax.rmi.CORBA.Util;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置导航栏高亮
        req.setAttribute("menu_page","report");
        //通过actionName获取用户行为
        String actionName = req.getParameter("actionName");
        System.out.println("actionName::"+actionName);
        //判断用户行为
        if ("info".equals(actionName)){
            //进入报表页面
            reportInfo(req,resp);
        }else if ("month".equals(actionName)){
            //通过月份查询对应的云记数量
            queryNoteCountByMonth(req,resp);
        }else if ("location".equals(actionName)){
            // 查询用户发布云记时的坐标
            queryNoteLonAndLat(req, resp);
        }

    }

    /**
     * 查询用户发布云记时的坐标
     * @param req
     * @param resp
     */
    private void queryNoteLonAndLat(HttpServletRequest req, HttpServletResponse resp) {
        User user = (User) req.getSession().getAttribute("user");
        ResultInfo<List<Note>> resultInfo = noteService.queryNoteLonAndLat(user.getUserId());
        JsonUtil.toJson(resp,resultInfo);
    }

    /**
     * 通过月份查询对应的云记数量
     * @param req
     * @param resp
     */
    private void queryNoteCountByMonth(HttpServletRequest req, HttpServletResponse resp) {
        //接收当前用户对象
        User user = (User) req.getSession().getAttribute("user");
        //调用service层，返回结果集ResultInfo
        ResultInfo<Map<String,Object>> resultInfo = noteService.queryNoteCountByMonth(user.getUserId());
        // 将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数
        JsonUtil.toJson(resp,resultInfo);
    }

    private void reportInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置动态包含
        req.setAttribute("changePage","report/info.jsp");
        //请求转发至首页
        req.getRequestDispatcher("index.jsp").forward(req,resp);

    }
}
