package com.chenyao.note.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteVo {
    private String groupName;//分组名称
    private long noteCount;//云记数量

    private Integer typeId;//类型id；

    public NoteVo() {
    }

    public NoteVo(String groupName, long noteCount, Integer typeId) {
        this.groupName = groupName;
        this.noteCount = noteCount;
        this.typeId = typeId;
    }

    public String toString() {
        return "NoteVo{groupName = " + groupName + ", noteCount = " + noteCount + ", typeId = " + typeId + "}";
    }
}
