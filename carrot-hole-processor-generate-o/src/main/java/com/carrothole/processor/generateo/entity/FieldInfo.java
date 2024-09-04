package com.carrothole.processor.generateo.entity;

/**
 * Description: 类成员变量 <br>
 * Date: 2024/9/4 13:43 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public class FieldInfo {

    private String name;

    private String type;

    private String describe;


    public FieldInfo() {
    }

    public FieldInfo(String name, String type, String describe) {
        this.name = name;
        this.type = type;
        this.describe = describe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "ClassFieldInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
