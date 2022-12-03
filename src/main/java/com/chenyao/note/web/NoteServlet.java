package com.chenyao.note.web;

import cn.hutool.core.util.StrUtil;
import com.chenyao.note.po.Note;
import com.chenyao.note.po.NoteType;
import com.chenyao.note.po.User;
import com.chenyao.note.service.NoteService;
import com.chenyao.note.service.NoteTypeService;
import com.chenyao.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //导航栏高亮
        req.setAttribute("menu_page","note");
        //获取用户行为
        String actionName = req.getParameter("actionName");
        if ("view".equals(actionName)){// 进入发布云记页面
            noteView(req,resp);
        }else if ("addOrUpdate".equals(actionName)){ // 添加或修改云记
            addOrUpdate(req,resp);
        }else if ("detail".equals(actionName)){
            // 查询云记详情
            noteDetail(req,resp);
        }else if ("delete".equals(actionName)){
            //删除云记
            deleteNote(req,resp);
        }
    }

    private void deleteNote(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //1. 接收参数 （noteId）
        String noteId = req.getParameter("noteId");
        //2. 调用Service层删除方法，返回状态码 （1=成功，0=失败）
        Integer code = noteService.deleteNote(noteId);
        //3. 通过流将结果响应给ajax的回调函数 （输出字符串）
        PrintWriter writer = resp.getWriter();
        writer.write(code+"");
        writer.close();
    }

    /**
     * // 查询云记详情
     * @param req
     * @param resp
     */
    private void noteDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1. 接收参数 （noteId）
        String noteId = req.getParameter("noteId");
        //2. 调用Service层的查询方法，返回Note对象
        Note note = noteService.findNoteById(noteId);
        //3. 将Note对象设置到request请求域中
        req.setAttribute("note",note);
        //4. 设置首页动态包含的页面值
        req.setAttribute("changePage","note/detail.jsp");
        //5. 请求转发跳转到index.jsp
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }

    /**
     * 添加或修改云记
     * @param req
     * @param resp
     */
    private void addOrUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //1. 接收参数 （类型ID、标题、内容）
        String typeId = req.getParameter("typeId");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        //获取经纬度
        String lon = req.getParameter("lon");
        String lat = req.getParameter("lat");

        //如果是修改操作，需接收noteId
        String noteId = req.getParameter("noteId");
//        System.out.println(noteId);


        //2. 调用Service层方法，返回resultInfo对象
        ResultInfo<Note> resultInfo = noteService.addOrUpdate(typeId,title,content,noteId,lon,lat);
        //3. 判断resultInfo的code值
        if (resultInfo.getCode()==1){
            //如果code=1，表示成功
            //重定向跳转到首页 index
            resp.sendRedirect("index");
        }else {//如果code=0，表示失败
            //将resultInfo对象设置到request作用域
            req.setAttribute("resultInfo",resultInfo);
            //请求转发跳转到note?actionName=view
            String url= "note?actionName=view";
            //如果是修改操作，需要传递noteId
            if (!StrUtil.isBlank(noteId)){
                url+="&noteId="+noteId;
            }
            req.getRequestDispatcher(url).forward(req,resp);
        }


    }

    /**
     * // 进入发布云记页面
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void noteView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //修改操作
        String noteId = req.getParameter("noteId");
        Note note = noteService.findNoteById(noteId);
        req.setAttribute("noteInfo",note);
        //修改操作


        //获取当前用户对象
        User user = (User) req.getSession().getAttribute("user");
        //通过用户ID查询用户类型列表
        List<NoteType> typeList = new NoteTypeService().findTypeList(user.getUserId());
        //3. 将类型列表设置到request请求域中
        req.setAttribute("typeList",typeList);
        //4. 设置首页动态包含的页面值
        req.setAttribute("changePage","note/view.jsp");
        //5. 请求转发跳转到index.jsp
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }
}
