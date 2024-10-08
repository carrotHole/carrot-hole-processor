package io.github.carrothole.processor.generateo.entity;

import io.github.carrothole.processor.generateo.enums.VOTypeEnum;

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

    private String simpleType;

    private String describe;

    private int[] types;

    private String[] simpleAnnotations;

    private String[] annotations;



    public FieldInfo(String name, String type, String describe, VOTypeEnum[] types, String[] annotations) {
        this.name = name;
        setType(type);
        this.describe = describe;
        setTypes(types);
        setAnnotations(annotations);
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
        String[] split = type.split("\\.");
        this.simpleType = split[split.length-1];
    }


    public String[] getSimpleAnnotations() {
        return simpleAnnotations;
    }
    public String[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String[] annotations) {

        String[] annoName = new String[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            String annotation = annotations[i];
            String[] split = annotation.split("\\.");
            annoName[i] = split[split.length-1];
        }
        this.annotations = annotations;
        this.simpleAnnotations = annoName;
    }

    public String getSimpleType() {
        return simpleType;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public boolean hasThis(VOTypeEnum type){
        return types[type.ordinal()] == 1;
    }

    public void setTypes(VOTypeEnum[] types) {
        this.types = new int[VOTypeEnum.values().length];
        for (VOTypeEnum voTypeEnum : types) {
            this.types[voTypeEnum.ordinal()] = 1;
        }
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", simpleType='" + simpleType + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
