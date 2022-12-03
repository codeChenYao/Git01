package com.chenyao.note.web;

import com.chenyao.note.po.Note;
import com.chenyao.note.po.User;
import com.chenyao.note.service.NoteService;
import com.chenyao.note.util.Page;
import com.chenyao.note.vo.NoteVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置导航高亮
        req.setAttribute("menu_page","index");

        String actionName = req.getParameter("actionName");
        if ("searchTitle".equals(actionName)){//标题查询
            String title = req.getParameter("title");
            req.setAttribute("title",title);
            noteList(req,resp,title,null,null);
        }else if ("searchDate".equals(actionName)){//时间查询
            String date = req.getParameter("date");
            req.setAttribute("date",date);
            noteList(req,resp,null,date,null);
        }else if ("searchType".equals(actionName)){//类型查询
            String typeId = req.getParameter("typeId");
            req.setAttribute("typeId",typeId);
            noteList(req,resp,null,null,typeId);
        }

        else{
            //分页查询云记列表
            noteList(req,resp,null,null,null);
        }
        //设置首页动态包含的页面
        req.setAttribute("changePage","note/list.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }

    private void noteList(HttpServletRequest req, HttpServletResponse resp,String title,String date,String typeId) {
//    1. 接收参数 （当前页、每页显示的数量）
        String pageNum = req.getParameter("pageNum");
        String pageSize = req.getParameter("pageSize");
//    2. 获取Session作用域中的user对象
        User user = (User) req.getSession().getAttribute("user");
//    3. 调用Service层查询方法，返回Page对象
        Page<Note> page = new NoteService().findNoteListByPage(pageNum,pageSize,user.getUserId(),title,date,typeId);
//    4. 将page对象设置到request作用域中
        req.setAttribute("page",page);

        //通过日期分组查询当前登录用户下的云记数量
        List<NoteVo> dateInfo = new NoteService().findNoteCountByDate(user.getUserId());
//        System.out.println("dateInfo:"+dateInfo);
        //设置集合存放在session作用域中
        req.getSession().setAttribute("dateInfo",dateInfo);
        //通过日期分组查询当前登录用户下的云记数量
        List<NoteVo> typeInfo = new NoteService().findNoteCountByType(user.getUserId());
        //设置集合存放在session作用域中
        req.getSession().setAttribute("typeInfo",typeInfo);
    }
}
