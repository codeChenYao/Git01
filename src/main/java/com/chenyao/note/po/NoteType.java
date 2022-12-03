package com.chenyao.note.po;

public class NoteType {
    private Integer typeId;//类型ID
    private String typeName;//类型名称
    private Integer userId;//用户ID

    public NoteType() {
    }

    public NoteType(Integer typeId, String typeName, Integer userId) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.userId = userId;
    }

    /**
     * 获取
     * @return typeId
     */
    public Integer getTypeId() {
        return typeId;
    }

    /**
     * 设置
     * @param typeId
     */
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    /**
     * 获取
     * @return typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * 设置
     * @param typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 获取
     * @return userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String toString() {
        return "NoteType{typeId = " + typeId + ", typeName = " + typeName + ", userId = " + userId + "}";
    }
}
