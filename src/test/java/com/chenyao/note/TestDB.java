package com.chenyao.note;

import com.chenyao.note.util.DBUtil;
import org.junit.Test;

public class TestDB {

    @Test
    public void testConnection(){
        System.out.println(DBUtil.getConnetion());
    }
}
